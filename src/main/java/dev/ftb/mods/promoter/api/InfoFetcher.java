package dev.ftb.mods.promoter.api;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class InfoFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfoFetcher.class);

    private static final InfoFetcher INSTANCE = new InfoFetcher();
    private static final String API_URL = "https://api.feed-the-beast.com/v1/meta/promotions";

    private final List<PromoData> promotions = Collections.synchronizedList(new ArrayList<>());

    private InfoFetcher() {
    }

    public static InfoFetcher get() {
        return INSTANCE;
    }

    public void load() {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        // Send of the request asynchronously (don't block the main thread)
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(data -> {
                    // Do something with our data
                    try {
                        var gson = new Gson();
                        var response = gson.fromJson(data, PromoResponse.class);

                        promotions.addAll(response.promotions());
                    } catch (Exception e) {
                        LOGGER.error("Failed to parse response", e);
                    }
                })
                .exceptionally(e -> {
                    LOGGER.error("Failed to fetch promotions", e);
                    return null;
                });
    }

    public List<PromoData> getPromotions() {
        return promotions;
    }
}
