package dev.ftb.mods.promoter;

import dev.ftb.mods.promoter.api.InfoFetcher;
import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(FTBPromoter.MOD_ID)
public class FTBPromoter {
    public static final String MOD_ID = "ftbpromoter";

    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public FTBPromoter() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
    }

    public void clientSetup(FMLClientSetupEvent event) {
        PromoterClientInit.init();
    }
}
