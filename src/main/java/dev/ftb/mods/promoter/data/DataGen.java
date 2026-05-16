package dev.ftb.mods.promoter.data;

import dev.ftb.mods.promoter.FTBPromoter;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = FTBPromoter.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();

        generator.addProvider(true, new LangGen(generator.getPackOutput()));
    }

    public static class LangGen extends LanguageProvider {
        public LangGen(PackOutput output) {
            super(output, FTBPromoter.MOD_ID, "en_us");
        }

        @Override
        protected void addTranslations() {
            prefixed("fancymenu.requirement.name", "Is FTB Promoter Available");
            prefixed("fancymenu.requirement.description", "Checks if a promo is available from the API.");
            prefixed("fancymenu.requirement.value.name", "Promotion UUID");
        }

        private void prefixed(String key, String value) {
            add(FTBPromoter.MOD_ID + "." + key, value);
        }
    }
}
