package dev.ftb.mods.promoter.integrations;

import dev.ftb.mods.promoter.api.PromoData;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Integrations {
    public static final String BISECTHOSTING_MOD_ID = "bhmenu";
    public static final UUID BISECTHOSTING_MOD_UUID = UUID.fromString("1d866d7a-9e2c-44e2-b655-b9e5d9490f68");

    public static final String FTB_WORLD_MOD_ID = "rgp_client";
    public static final UUID FTB_WORLD_MOD_UUID = UUID.fromString("8f2ad888-00c3-4417-ac62-f0b43d86ccfd");

    public static final String FANCY_MENU_MOD_ID = "fancymenu";

    public static final List<Integration> INTEGRATIONS = new ArrayList<>();

    public static void init() {
        if (ModList.get().isLoaded(BISECTHOSTING_MOD_ID)) {
            INTEGRATIONS.add(new BisectHostingIntegration());
        }

//        if (ModList.get().isLoaded(FTB_WORLD_MOD_ID)) {
//            INTEGRATIONS.add(new FTBWorldsIntegration());
//        }
//
//        if (ModList.get().isLoaded(FANCY_MENU_MOD_ID)) {
//            FancyMenuIntegration.init();
//        }
    }

    public static <E extends AbstractList.AbstractListEntry<E>> boolean denyEntry(E entry) {
        for (Integration integration : INTEGRATIONS) {
            if (integration.filterServerListEntry(entry)) {
                System.out.printf("Integration %s denied entry %s", integration.getClass().getSimpleName(), entry.getClass().getSimpleName());
                return true;
            }
        }

        return false;
    }

    public static boolean clickAction(PromoData data, Screen parent) {
        for (Integration integration : INTEGRATIONS) {
            if (integration.clickAction(data, parent)) {
                return true;
            }
        }

        return false;
    }
}
