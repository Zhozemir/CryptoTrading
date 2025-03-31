package com.example.crypto.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceService {

    private BigDecimal balance = BigDecimal.valueOf(10000.0);

    public BigDecimal getBalance() {
        return balance;
    }

    public void addToBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void subtractFromBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void resetBalance() {
        this.balance = BigDecimal.valueOf(10000.0);
    }
}