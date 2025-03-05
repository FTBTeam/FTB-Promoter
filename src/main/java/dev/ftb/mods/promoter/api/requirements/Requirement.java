package dev.ftb.mods.promoter.api.requirements;

import com.google.gson.*;
import dev.ftb.mods.promoter.api.PromoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public abstract class Requirement {
    public String type;

    public abstract boolean test(PromoData data);

    public static class RequirementDeserializer implements JsonDeserializer<Requirement> {
        private static final Logger LOGGER = LoggerFactory.getLogger(RequirementDeserializer.class);

        @Override
        public Requirement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonObject()) {
                throw new JsonParseException("Requirement must be an object");
            }

            if (!json.getAsJsonObject().has("type")) {
                throw new JsonParseException("Requirement must have a type");
            }


            String type = json.getAsJsonObject().get("type").getAsString();
            try {
                switch (type) {
                    case "minecraft_version":
                        return MinecraftRequirement.deserialize(type, json, typeOfT, context);
                    case "mod_loaded":
                        return ModRequirement.deserialize(type, json, typeOfT, context);
                    default:
                        // Don't fatal if we don't know the requirement type
                        LOGGER.warn("Unsupported requirement type: {}", type);
                        return null;
                }
            } catch (JsonParseException e) {
                LOGGER.error("Failed to parse requirement of type {}", type, e);
                return null;
            }
        }
    }
}
