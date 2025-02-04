package dev.ftb.mods.promoter;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(FTBPromoter.MOD_ID)
public class FTBPromoter {
    public static final String MOD_ID = "ftbpromoter";

    private static final Logger LOGGER = LogUtils.getLogger();

    public FTBPromoter(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::clientSetup);
    }

    public void clientSetup(FMLClientSetupEvent event) {
        PromoterClientInit.init();
    }
}
