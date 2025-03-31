package com.example.crypto.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriceService {

    private final Map<String, BigDecimal> cryptoPrices = new HashMap<>();

    private Map<String, String> pairToSymbol = new HashMap<>();

    public PriceService() {

        pairToSymbol = new HashMap<>();
        pairToSymbol.put("XBT/USD", "BTC");
        pairToSymbol.put("ETH/USD", "ETH");
        pairToSymbol.put("LTC/USD", "LTC");
        pairToSymbol.put("XRP/USD", "XRP");
        pairToSymbol.put("BCH/USD", "BCH");
        pairToSymbol.put("ADA/USD", "ADA");
        pairToSymbol.put("DOT/USD", "DOT");
        pairToSymbol.put("LINK/USD", "LINK");
        pairToSymbol.put("XLM/USD", "XLM");
        pairToSymbol.put("SOL/USD", "SOL");
        pairToSymbol.put("UNI/USD", "UNI");
        pairToSymbol.put("EOS/USD", "EOS");
        pairToSymbol.put("TRX/USD", "TRX");
        pairToSymbol.put("XMR/USD", "XMR");
        pairToSymbol.put("DASH/USD", "DASH");
        pairToSymbol.put("ZEC/USD", "ZEC");
        pairToSymbol.put("ETC/USD", "ETC");
        pairToSymbol.put("ATOM/USD", "ATOM");
        pairToSymbol.put("FIL/USD", "FIL");
        pairToSymbol.put("MATIC/USD", "MATIC");

    }

    public void updatePrice(String krakenPair, BigDecimal lastPrice) {

        String symbol = pairToSymbol.get(krakenPair);

        if (symbol != null)
            cryptoPrices.put(symbol, lastPrice);

    }

    public BigDecimal getPrice(String symbol) {
        return cryptoPrices.get(symbol.toUpperCase());
    }

    public List<Map<String, Object>> getAllPrices() {

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : cryptoPrices.entrySet()) {

            Map<String, Object> info = new HashMap<>();
            info.put("symbol", entry.getKey());
            info.put("price", entry.getValue());
            result.add(info);

        }

        return result;
    }
}
