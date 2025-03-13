package dev.ftb.mods.promoter.api.requirements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import dev.ftb.mods.promoter.api.PromoData;
import net.neoforged.fml.ModList;

import java.lang.reflect.Type;

public class ModRequirement extends Requirement {
    public String modId;

    public static  ModRequirement deserialize(String type, JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        String modId = json.getAsJsonObject().get("modId").getAsString();

        ModRequirement modRequirement = new ModRequirement();
        modRequirement.modId = modId;
        modRequirement.type = type;

        return modRequirement;
    }

    @Override
    public boolean test(PromoData data) {
        return ModList.get().isLoaded(modId);
    }
}
