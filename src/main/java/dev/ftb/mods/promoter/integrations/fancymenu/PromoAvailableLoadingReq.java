//package dev.ftb.mods.promoter.integrations.fancymenu;
//
//import de.keksuccino.fancymenu.customization.loadingrequirement.LoadingRequirement;
//import de.keksuccino.fancymenu.util.rendering.ui.screen.texteditor.TextEditorFormattingRule;
//import dev.ftb.mods.promoter.FTBPromoter;
//import dev.ftb.mods.promoter.api.InfoFetcher;
//import net.minecraft.util.ResourceLocation;
//import org.antlr.v4.runtime.misc.NotNull;
//
//import javax.annotation.Nullable;
//import java.util.HashMap;
//import java.util.List;
//
//public class PromoAvailableLoadingReq extends LoadingRequirement {
//    private static final HashMap<String, Boolean> PROMO_AVAILABLE_CACHE = new HashMap<>();
//
//    public PromoAvailableLoadingReq() {
//        // Not a fan that this doesn't just use a resource location, but it's fine...
//        super(new ResourceLocation(FTBPromoter.MOD_ID, "promo_available").toString().replace(":", "_"));
//    }
//
//    @Override
//    public boolean hasValue() {
//        return true;
//    }
//
//    /**
//     * Called every renderframe so we'll do a lazy value here as we only need to look it up once.
//     */
//    @Override
//    public boolean isRequirementMet(@Nullable String s) {
//        if (s == null) {
//            return false;
//        }
//
//        return PROMO_AVAILABLE_CACHE.computeIfAbsent(s, (uuid) ->
//                InfoFetcher.get().getPromotions().stream().anyMatch(e -> e.uuid().toString().equals(uuid)));
//    }
//
//    @Override
//    public @NotNull String getDisplayName() {
//        return "Is FTB Promotional Available";
//    }
//
//    @Override
//    public @Nullable List<String> getDescription() {
//        return List.of("Checks if a promo is available from the API.");
//    }
//
//    @Override
//    public @Nullable String getCategory() {
//        return null;
//    }
//
//    @Override
//    public @Nullable String getValueDisplayName() {
//        return "Promotion UUID";
//    }
//
//    @Override
//    public @Nullable String getValuePreset() {
//        return "";
//    }
//
//    @Override
//    public @Nullable List<TextEditorFormattingRule> getValueFormattingRules() {
//        return List.of();
//    }
//}
