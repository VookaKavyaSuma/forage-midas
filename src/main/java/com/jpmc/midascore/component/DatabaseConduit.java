package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRecordRepository recordRepository;

    public DatabaseConduit(UserRepository userRepository, TransactionRecordRepository recordRepository) {
        this.userRepository = userRepository;
        this.recordRepository = recordRepository;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    // This handles saving the new transactions!
    public void save(TransactionRecord record) {
        recordRepository.save(record);
    }

    // This fetches the user!
    public UserRecord getUser(long id) {
        return userRepository.findById(id); 
    }
}