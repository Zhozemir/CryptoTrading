package com.example.crypto.repositories;

import com.example.crypto.models.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;

@Repository
public class TransactionsRepository {

    private final JdbcTemplate jdbcTemplate;

    public TransactionsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertTransaction(String type, String symbol, BigDecimal quantity, BigDecimal price, BigDecimal profitLoss) {

        String sql = "INSERT INTO transactions (type, crypto, quantity, price, profit_loss) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, type, symbol, quantity, price, profitLoss);

    }

    public List<Transaction> findAllTransactions() {

        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";

        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) -> {

            Transaction t = new Transaction();
            t.setId(String.valueOf(rs.getInt("id")));
            t.setType(rs.getString("type"));
            t.setCrypto(rs.getString("crypto"));
            t.setQuantity(rs.getBigDecimal("quantity"));
            t.setPrice(rs.getBigDecimal("price"));
            BigDecimal profitLoss = rs.getBigDecimal("profit_loss");
            t.setProfitLoss(profitLoss);
            t.setTimestamp(rs.getTimestamp("timestamp"));
            return t;

        });
    }

    public void deleteAll() {
        jdbcTemplate.update("TRUNCATE TABLE transactions");
    }
}