package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.Incentive;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionListener {

    private final DatabaseConduit databaseConduit;
    private final RestTemplate restTemplate;

    public TransactionListener(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
        this.restTemplate = new RestTemplate(); // Initialize RestTemplate
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-group")
    public void listen(Transaction transaction) {
        UserRecord sender = databaseConduit.getUser(transaction.getSenderId());
        UserRecord recipient = databaseConduit.getUser(transaction.getRecipientId());

        if (sender != null && recipient != null && sender.getBalance() >= transaction.getAmount()) {
            
            // 1. Call the Incentive API
            Incentive incentive = restTemplate.postForObject(
                "http://localhost:8080/incentive", 
                transaction, 
                Incentive.class
            );
            
            // Extract the amount (default to 0 if something goes wrong)
            float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;

            // 2. Do the math (Sender loses transaction amount. Recipient gains transaction + incentive)
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

            // 3. Save the updated users
            databaseConduit.save(sender);
            databaseConduit.save(recipient);

            // 4. Save the new TransactionRecord (now with the incentive included)
            TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
            databaseConduit.save(record);
            
        } else {
            System.out.println("Invalid transaction discarded.");
        }
    }
}