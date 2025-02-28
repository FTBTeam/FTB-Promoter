package dev.ftb.mods.promoter;

import dev.ftb.mods.promoter.api.InfoFetcher;
import dev.ftb.mods.promoter.integrations.Integrations;
import dev.ftb.mods.promoter.screen.ScreenInitEvent;
import net.minecraftforge.common.MinecraftForge;

public class PromoterClientInit {
    public static void init() {
        Integrations.init();
        InfoFetcher.get().load();

        MinecraftForge.EVENT_BUS.register(ScreenInitEvent.class);
    }
}
