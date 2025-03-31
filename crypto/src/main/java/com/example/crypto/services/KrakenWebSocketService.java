package com.example.crypto.services;

import jakarta.annotation.PostConstruct;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class KrakenWebSocketService {

    private final PriceService priceService;
    private WebSocketClient client;

    private final String[] subscribedPairs = {

            "XBT/USD",   // Bitcoin
            "ETH/USD",   // Ethereum
            "LTC/USD",   // Litecoin
            "XRP/USD",   // Ripple
            "BCH/USD",   // Bitcoin Cash
            "ADA/USD",   // Cardano
            "DOT/USD",   // Polkadot
            "LINK/USD",  // Chainlink
            "XLM/USD",   // Stellar
            "SOL/USD",   // Solana
            "UNI/USD",   // Uniswap
            "EOS/USD",   // EOS
            "TRX/USD",   // Tron
            "XMR/USD",   // Monero
            "DASH/USD",  // Dash
            "ZEC/USD",   // Zcash
            "ETC/USD",   // Ethereum Classic
            "ATOM/USD",  // Cosmos
            "FIL/USD",   // Filecoin
            "MATIC/USD"  // Polygon
    };


    public KrakenWebSocketService(PriceService priceService) {
        this.priceService = priceService;
    }

    @PostConstruct
    public void init() {
        startKrakenWebSocket();
    }

    private void startKrakenWebSocket() {

        try {

            URI uri = new URI("wss://ws.kraken.com");
            this.client = new WebSocketClient(uri) {

                @Override
                public void onOpen(ServerHandshake handshake) {

                    System.out.println("[Kraken WebSocket connected.");
                    subscribeToPairs();

                }

                @Override
                public void onMessage(String message) {

                    System.out.println("Received message: " + message);
                    // Kraken V2 ticker update се връща като JSON масив: [channelID, {ticker data}, "PAIR"]
                    if (message.startsWith("[")) {

                        String pair = extractPairFromMessage(message);
                        String lastPriceStr = extractLastPriceFromMessage(message);

                        if (pair != null && lastPriceStr != null) {

                            try {

                                BigDecimal lastPrice = new BigDecimal(lastPriceStr);
                                priceService.updatePrice(pair, lastPrice);

                            } catch (NumberFormatException e) {
                                System.out.println("NumberFormatException parsing lastPrice: " + lastPriceStr);
                            }
                        }
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Kraken WebSocket closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("Kraken WebSocket error: " + ex.getMessage());
                }
            };
            this.client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void subscribeToPairs() {

        StringBuilder sb = new StringBuilder();
        sb.append("{\"event\":\"subscribe\",\"pair\":[");

        for (int i = 0; i < subscribedPairs.length; i++) {

            sb.append("\"").append(subscribedPairs[i]).append("\"");

            if (i < subscribedPairs.length - 1)
                sb.append(",");

        }
        sb.append("],\"subscription\":{\"name\":\"ticker\"}}");

        if (this.client != null && this.client.isOpen()) {

            this.client.send(sb.toString());
            System.out.println("Subscription message sent.");

        } else
            System.out.println("WebSocket client is not connected yet!");
    }

    private String extractPairFromMessage(String message) {

        int lastQuoteIndex = message.lastIndexOf("\"");
        if (lastQuoteIndex < 0) return null;
        int secondLastQuoteIndex = message.lastIndexOf("\"", lastQuoteIndex - 1);
        if (secondLastQuoteIndex < 0) return null;

        String pair = message.substring(secondLastQuoteIndex + 1, lastQuoteIndex);

        return pair;
    }

    private String extractLastPriceFromMessage(String message) {

        int index = message.indexOf("\"c\":");
        if (index < 0) return null;
        int start = message.indexOf("[", index);
        if (start < 0) return null;
        int end = message.indexOf("]", start);
        if (end < 0) return null;

        String cArray = message.substring(start + 1, end);

        String[] parts = cArray.split(",");
        if (parts.length < 1) return null;

        String lastPriceStr = parts[0].replaceAll("\"", "").trim();

        return lastPriceStr;

    }
}
