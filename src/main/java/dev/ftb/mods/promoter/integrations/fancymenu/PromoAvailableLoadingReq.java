package dev.ftb.mods.promoter.integrations.fancymenu;

import de.keksuccino.fancymenu.customization.requirement.Requirement;
import de.keksuccino.fancymenu.util.rendering.ui.screen.texteditor.TextEditorFormattingRule;
import dev.ftb.mods.promoter.FTBPromoter;
import dev.ftb.mods.promoter.api.InfoFetcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class PromoAvailableLoadingReq extends Requirement {
    private static final HashMap<String, Boolean> PROMO_AVAILABLE_CACHE = new HashMap<>();

    public PromoAvailableLoadingReq() {
        // Not a fan that this doesn't just use a resource location, but it's fine...
        super(Identifier.fromNamespaceAndPath(FTBPromoter.MOD_ID, "promo_available").toString().replace(":", "_"));
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    /**
     * Called every renderframe so we'll do a lazy value here as we only need to look it up once.
     */
    @Override
    public boolean isRequirementMet(@Nullable String s) {
        if (s == null) {
            return false;
        }

        return PROMO_AVAILABLE_CACHE.computeIfAbsent(s, (uuid) ->
                InfoFetcher.get().getPromotions().stream().anyMatch(e -> e.uuid().toString().equals(uuid)));
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("ftbpromoter.fancymenu.requirement.name");
    }

    @Override
    public @Nullable Component getDescription() {
        return Component.translatable("ftbpromoter.fancymenu.requirement.description");
    }

    @Override
    public @Nullable String getCategory() {
        return "FTB";
    }

    @Override
    public @Nullable Component getValueDisplayName() {
        return Component.translatable("ftbpromoter.fancymenu.requirement.value.name");
    }

    @Override
    public @Nullable String getValuePreset() {
        return "";
    }

    @Override
    public @Nullable List<TextEditorFormattingRule> getValueFormattingRules() {
        return List.of();
    }
}
