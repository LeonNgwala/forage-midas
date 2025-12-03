package com.jpmc.midascore.component;

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

   @KafkaListener(topics = "${general.kafka-topic}", groupId = "${spring.kafka.consumer.group-id}")
   @Transactional
   public void Listen(Transaction transaction) {
        LOG.info("Received Transaction: {}", transaction);

        // Convert the float amount from the Kafka message to BigDecimal for safe calculations
        BigDecimal transactionAmount = BigDecimal.valueOf(transaction.getAmount());

        // 1. Fetch sender and recipient from the database                                             
        Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
        Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());

        // 2. Validate the transaction                                                                 
        if (senderOpt.isEmpty()) {
            LOG.warn("Transaction discarded: Sender with ID {} not found.", transaction.getSenderId());
            return; // Discard transaction
        }

        if (recipientOpt.isEmpty()) {
            LOG.warn("Transaction discarded: Recipient with ID {} not found.", transaction.getRecipientId());
            return; // Discard transaction
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        // Check for sufficient balance
        if (sender.getBalance().compareTo(transactionAmount) < 0) {
            LOG.warn("Transaction discarded: Insufficient balance for sender {}. Required: {}, Available: {}",
                sender.getName(), transactionAmount, sender.getBalance());
            return; // Discard transaction
        }

        // 3. If valid, update balances and record the transaction                                     
        LOG.info("Transaction is valid. Processing...");

        // Update balances
        sender.setBalance(sender.getBalance().subtract(transactionAmount));
        recipient.setBalance(recipient.getBalance().add(transactionAmount));                     

        // Save the updated user records
        userRepository.save(sender);
        userRepository.save(recipient);
                                                                                                         
        // Create and save the transaction record
        TransactionRecord transactionRecord = new TransactionRecord( 
        sender,
        recipient,
        transactionAmount,
        LocalDateTime.now());
        transactionRepository.save(transactionRecord);

        LOG.info("Successfully processed transaction. New sender balance: {}, New recipient balance: {}",
        sender.getBalance(), recipient.getBalance());
   }
}