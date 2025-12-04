package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class BalanceController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") String userId) {
        Optional<UserRecord> userRecordOptional = userRepository.findById(Long.parseLong(userId));
        if (userRecordOptional.isPresent()) {
            UserRecord userRecord = userRecordOptional.get();
            return new Balance(userRecord.getBalance().floatValue());
        }
        else {
            return new Balance(0);
        }
    }

}
