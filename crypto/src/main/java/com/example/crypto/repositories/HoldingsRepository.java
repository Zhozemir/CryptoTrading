package com.example.crypto.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class HoldingsRepository {

    private final JdbcTemplate jdbcTemplate;

    public HoldingsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findBySymbol(String symbol) {

        String sql = "SELECT id, quantity, average_cost FROM holdings WHERE crypto = ?";
        return jdbcTemplate.queryForList(sql, symbol);

    }

    public void insertHolding(String symbol, BigDecimal quantity, BigDecimal avgCost) {

        String sql = "INSERT INTO holdings (crypto, quantity, average_cost) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, symbol, quantity, avgCost);

    }

    public void updateHolding(int id, BigDecimal newQty, BigDecimal newAvgCost) {

        String sql = "UPDATE holdings SET quantity = ?, average_cost = ? WHERE id = ?";
        jdbcTemplate.update(sql, newQty, newAvgCost, id);

    }

    public void deleteHolding(int id) {

        String sql = "DELETE FROM holdings WHERE id = ?";
        jdbcTemplate.update(sql, id);

    }

    public void deleteAll() {
        jdbcTemplate.update("TRUNCATE TABLE holdings");
    }
}
