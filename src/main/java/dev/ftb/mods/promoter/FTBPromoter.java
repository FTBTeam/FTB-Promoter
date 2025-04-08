package dev.ftb.mods.promoter;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;


@Mod(FTBPromoter.MOD_ID)
public class FTBPromoter {
    public static final String MOD_ID = "ftbpromoter";

    private static final Logger LOGGER = LogUtils.getLogger();

    public FTBPromoter() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
    }

    public void clientSetup(FMLClientSetupEvent event) {
        PromoterClientInit.init();
    }
}
