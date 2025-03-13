package dev.ftb.mods.promoter.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.ftb.mods.promoter.api.requirements.Requirement;
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
    private boolean isFirstGet = true;

    private InfoFetcher() {
    }

    public static InfoFetcher get() {
        return INSTANCE;
    }

    public void load() {
        try {
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
                            GsonBuilder builder = new GsonBuilder();
                            // Add a custom deserializer for the requirements
                            builder.registerTypeAdapter(Requirement.class, new Requirement.RequirementDeserializer());
                            Gson gson = builder.create();

                            PromoResponse responseObj = gson.fromJson(data, PromoResponse.class);
                            promotions.addAll(responseObj.promotions());
                        } catch (Exception e) {
                            LOGGER.error("Failed to parse response", e);
                        }
                    })
                    .exceptionally(e -> {
                        LOGGER.error("Failed to fetch promotions", e);
                        return null;
                    });
        } catch (Exception e) {
            LOGGER.error("Failed to fetch promotions", e);
        }
    }

    public List<PromoData> getPromotions() {
        if (isFirstGet) {
            isFirstGet = false;
            refineList();
        }

        return promotions;
    }

    public void refineList() {
        // We want to go over the list and remove and promotions that are not valid for this instance.
        if (this.promotions.isEmpty()) {
            return;
        }

        List<PromoData> cloneData = new ArrayList<>(this.promotions);
        for (PromoData data : cloneData) {
            List<Requirement> requirements = data.requirements();

            if (requirements == null || data.requirements().isEmpty()) {
                continue;
            }

            boolean passes = true;
            for (Requirement requirement : requirements) {
                if (!requirement.test(data)) {
                    passes = false;
                    break;
                }
            }

            if (!passes) {
                this.promotions.remove(data);
            }
        }
    }
}
