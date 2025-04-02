package com.example.crypto.controller;

import com.example.crypto.models.Transaction;
import com.example.crypto.services.BalanceService;
import com.example.crypto.services.PriceService;
import com.example.crypto.services.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CryptoController {

    private final PriceService priceService;
    private final BalanceService balanceService;
    private final TradingService tradingService;

    @Autowired
    public CryptoController(PriceService priceService, BalanceService balanceService, TradingService tradingService) {

        this.priceService = priceService;
        this.balanceService = balanceService;
        this.tradingService = tradingService;

    }

    @GetMapping("/prices")
    public List<Map<String, Object>> getPrices() {
        return priceService.getAllPrices();
    }

    @GetMapping("/balance")
    public Map<String, Object> getBalance() {

        Map<String, Object> response = new HashMap<>();
        response.put("balance", balanceService.getBalance());
        return response;

    }

    @PostMapping("/buy")
    public Map<String, String> buy(@RequestBody Map<String, Object> payload) {

        String cryptoSymbol = (String) payload.get("crypto");
        BigDecimal quantity = new BigDecimal(payload.get("quantity").toString());
        String message = tradingService.buyCrypto(cryptoSymbol, quantity);
        Map<String, String> resp = new HashMap<>();
        resp.put("message", message);
        return resp;

    }

    @PostMapping("/sell")
    public Map<String, String> sell(@RequestBody Map<String, Object> payload) {

        String cryptoSymbol = (String) payload.get("crypto");
        BigDecimal quantity = new BigDecimal(payload.get("quantity").toString());
        String message = tradingService.sellCrypto(cryptoSymbol, quantity);
        Map<String, String> resp = new HashMap<>();
        resp.put("message", message);
        return resp;

    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions() {
        return tradingService.getTransactions();
    }

    @PostMapping("/reset")
    public Map<String, String> reset() {

        tradingService.reset();
        Map<String, String> resp = new HashMap<>();
        resp.put("message", "The balance has been reset and all data has been cleared.");
        return resp;

    }
}
