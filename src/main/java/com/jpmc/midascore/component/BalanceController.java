package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    private final DatabaseConduit databaseConduit;

    public BalanceController(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam(name = "userId") Long userId) {
        // Fetch the user from the database
        UserRecord user = databaseConduit.getUser(userId);

        // If the user exists, return their balance. Otherwise, return 0.
        if (user != null) {
            return new Balance(user.getBalance());
        } else {
            return new Balance(0);
        }
    }
}