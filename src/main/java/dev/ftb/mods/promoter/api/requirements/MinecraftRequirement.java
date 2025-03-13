package dev.ftb.mods.promoter.api.requirements;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import dev.ftb.mods.promoter.api.PromoData;
import net.minecraft.SharedConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MinecraftRequirement extends Requirement {
    private List<String> versions;

    /**
     * Inverts the versions list to become an exclusion list instead of an inclusion list
     */
    private boolean invert;

    @Override
    public boolean test(PromoData data) {
        String minecraftVersion = SharedConstants.getCurrentVersion().getId();

        if (this.invert) {
            for (String version : versions) {
                if (minecraftVersion.equals(version)) {
                    return false;
                }
            }
            return true;
        }

        for (String version : versions) {
            if (minecraftVersion.equals(version)) {
                return true;
            }
        }

        return false;
    }

    public static MinecraftRequirement deserialize(String type, JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        List<String> versions = new ArrayList<>();
        JsonArray rawVersions = json.getAsJsonObject().get("versions").getAsJsonArray();
        for (JsonElement version : rawVersions) {
            versions.add(version.getAsString());
        }

        boolean invert = false;
        if (json.getAsJsonObject().has("invert")) {
            invert = json.getAsJsonObject().get("invert").getAsBoolean();
        }

        MinecraftRequirement requirement = new MinecraftRequirement();
        requirement.type = type;
        requirement.versions = versions;
        requirement.invert = invert;

        return requirement;
    }
}
