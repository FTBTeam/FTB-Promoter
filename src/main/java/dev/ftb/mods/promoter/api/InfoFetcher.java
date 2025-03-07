package dev.ftb.mods.promoter.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.ftb.mods.promoter.api.requirements.Requirement;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class InfoFetcher {
    private static final Logger LOGGER = LogManager.getLogger(InfoFetcher.class);

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
        Minecraft.getInstance().submit(() -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }

                        GsonBuilder builder = new GsonBuilder();
                        // Add a custom deserializer for the requirements
                        builder.registerTypeAdapter(Requirement.class, new Requirement.RequirementDeserializer());
                        Gson gson = builder.create();

                        PromoResponse responseObj = gson.fromJson(response.toString(), PromoResponse.class);

                        promotions.addAll(responseObj.promotions());
                    }
                } else {
                    LOGGER.error("Failed to fetch promotions, response code: {}", responseCode);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to fetch promotions", e);
            }
        });
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
