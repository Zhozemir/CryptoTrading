package com.example.crypto.services;

import com.example.crypto.exceptions.CryptoNotFoundException;
import com.example.crypto.exceptions.InsufficientBalanceException;
import com.example.crypto.exceptions.InsufficientHoldingsException;
import com.example.crypto.exceptions.InvalidQuantityException;
import com.example.crypto.models.Transaction;
import com.example.crypto.repositories.HoldingsRepository;
import com.example.crypto.repositories.TransactionsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class TradingService {

    private final PriceService priceService;
    private final BalanceService balanceService;
    private final HoldingsRepository holdingsRepository;
    private final TransactionsRepository transactionsRepository;

    public TradingService(
            PriceService priceService,
            BalanceService balanceService,
            HoldingsRepository holdingsRepository,
            TransactionsRepository transactionsRepository) {

        this.priceService = priceService;
        this.balanceService = balanceService;
        this.holdingsRepository = holdingsRepository;
        this.transactionsRepository = transactionsRepository;

    }

    public String buyCrypto(String cryptoSymbol, BigDecimal quantity) {

        BigDecimal price = priceService.getPrice(cryptoSymbol.toUpperCase());

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0)
            throw new CryptoNotFoundException("Криптовалутата " + cryptoSymbol + " няма валидна цена.");

        if (quantity.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidQuantityException("Невалидно количество за покупка: " + quantity);

        BigDecimal totalCost = price.multiply(quantity);
        BigDecimal currentBalance = balanceService.getBalance();

        if (totalCost.compareTo(currentBalance) > 0)
            throw new InsufficientBalanceException("Недостатъчен баланс за покупка.");

        balanceService.subtractFromBalance(totalCost);

        List<Map<String, Object>> rows = holdingsRepository.findBySymbol(cryptoSymbol.toUpperCase());

        if (rows.isEmpty()) {
            holdingsRepository.insertHolding(cryptoSymbol.toUpperCase(), quantity, price);
        } else {

            Map<String, Object> row = rows.get(0);
            int id = ((Number) row.get("id")).intValue();
            BigDecimal oldQty = (BigDecimal) row.get("quantity");
            BigDecimal oldCost = (BigDecimal) row.get("average_cost");
            BigDecimal newQty = oldQty.add(quantity);

            BigDecimal newAvgCost = oldQty.multiply(oldCost)
                    .add(quantity.multiply(price))
                    .divide(newQty, RoundingMode.HALF_UP);

            holdingsRepository.updateHolding(id, newQty, newAvgCost);

        }

        transactionsRepository.insertTransaction("BUY", cryptoSymbol.toUpperCase(), quantity, price, BigDecimal.ZERO);

        return "Успешна покупка на " + quantity + " " + cryptoSymbol.toUpperCase() + " на цена " + price;
    }

    public String sellCrypto(String cryptoSymbol, BigDecimal quantity) {

        BigDecimal price = priceService.getPrice(cryptoSymbol.toUpperCase());

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0)
            throw new CryptoNotFoundException("Криптовалутата " + cryptoSymbol + " няма валидна цена.");

        if (quantity.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidQuantityException("Невалидно количество за продажба: " + quantity);

        List<Map<String, Object>> rows = holdingsRepository.findBySymbol(cryptoSymbol.toUpperCase());

        if (rows.isEmpty())
            throw new InsufficientHoldingsException("Нямате " + cryptoSymbol + " за продаване.");

        Map<String, Object> row = rows.get(0);
        int id = ((Number) row.get("id")).intValue();
        BigDecimal oldQty = (BigDecimal) row.get("quantity");
        BigDecimal oldCost = (BigDecimal) row.get("average_cost");

        if (quantity.compareTo(oldQty) > 0)
            throw new InsufficientHoldingsException("Опитвате се да продадете повече, отколкото притежавате.");

        BigDecimal profitLoss = price.subtract(oldCost).multiply(quantity);
        BigDecimal totalGain = price.multiply(quantity);

        balanceService.addToBalance(totalGain);

        BigDecimal newQty = oldQty.subtract(quantity);

        if (newQty.compareTo(BigDecimal.ZERO) == 0)
            holdingsRepository.deleteHolding(id);
        else
            holdingsRepository.updateHolding(id, newQty, oldCost);

        transactionsRepository.insertTransaction("SELL", cryptoSymbol.toUpperCase(), quantity, price, profitLoss);

        return String.format("Продадени са %.4f %s на цена %.2f. P/L = %.2f USD",
                quantity, cryptoSymbol, price, profitLoss);
    }

    public List<Transaction> getTransactions() {
        return transactionsRepository.findAllTransactions();
    }

    public void reset() {

        balanceService.resetBalance();
        holdingsRepository.deleteAll();
        transactionsRepository.deleteAll();

    }
}