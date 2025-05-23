package dev.ftb.mods.promoter;

import dev.ftb.mods.promoter.api.InfoFetcher;
import dev.ftb.mods.promoter.integrations.Integrations;
import dev.ftb.mods.promoter.screen.ScreenInitEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class PromoterClientInit {
    public static void init(FMLClientSetupEvent event) {
        Integrations.init();

        event.enqueueWork(() -> InfoFetcher.get().load());

        MinecraftForge.EVENT_BUS.register(ScreenInitEvent.class);
    }
}
