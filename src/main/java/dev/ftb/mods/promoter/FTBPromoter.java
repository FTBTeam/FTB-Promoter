package dev.ftb.mods.promoter;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FTBPromoter.MOD_ID)
public class FTBPromoter {
    public static final String MOD_ID = "ftbpromoter";

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public FTBPromoter() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
    }

    public void clientSetup(FMLClientSetupEvent event) {
        PromoterClientInit.init();
    }
}
