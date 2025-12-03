package com.jpmc.midascore.component;
import org.springframework.web.client.RestTemplate;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;


@Component
public class TransactionListener {
   private static final Logger LOG = LoggerFactory.getLogger(TransactionListener.class);

   @Autowired
   private UserRepository userRepository;

   @Autowired
   private TransactionRepository transactionRepository;

   @Autowired
   private RestTemplate restTemplate;


    @KafkaListener(topics = "${general.kafka-topic}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void Listen(Transaction transaction) {
        LOG.info("Received Transaction: {}", transaction);

        BigDecimal transactionAmount = BigDecimal.valueOf(transaction.getAmount());

        // 1. Fetch and validate users
        Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
        Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            LOG.warn("Transaction discarded: Sender or Recipient not found.");
            return;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        if (sender.getBalance().compareTo(transactionAmount) < 0) {
            LOG.warn("Transaction discarded: Insufficient balance for sender {}.", sender.getName());
            return;
        }

        LOG.info("Transaction is valid. Processing...");

        String incentiveApiUrl = "http://localhost:8080/incentive";
        Incentive incentive = restTemplate.postForObject(incentiveApiUrl, transaction, Incentive.class);
        BigDecimal incentiveAmount = BigDecimal.valueOf(incentive.getAmount());
        LOG.info("Received incentive amount: {}", incentiveAmount);

        sender.setBalance(sender.getBalance().subtract(transactionAmount));
        recipient.setBalance(recipient.getBalance().add(transactionAmount).add(incentiveAmount));

        // Save the updated user records
        userRepository.save(sender);
        userRepository.save(recipient);



        TransactionRecord transactionRecord = new TransactionRecord(
                sender,
                recipient,
                transactionAmount,
                incentiveAmount,
                LocalDateTime.now()
        );
        transactionRepository.save(transactionRecord);

        LOG.info("Successfully processed transaction. New sender balance: {}, New recipient balance: {}",
                sender.getBalance(), recipient.getBalance());
    }
}