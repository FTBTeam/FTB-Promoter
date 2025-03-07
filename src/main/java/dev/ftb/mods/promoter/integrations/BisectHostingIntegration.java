package dev.ftb.mods.promoter.integrations;

import com.bisecthosting.mods.bhmenu.modules.servercreatorbanner.screens.BHOrderScreen;
import com.bisecthosting.mods.bhmenu.modules.servercreatorbanner.screens.BannerEntry;
import dev.ftb.mods.promoter.api.PromoData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.list.AbstractList;
import java.util.function.Consumer;

public class BisectHostingIntegration implements Integration {
    public static final Consumer<Screen> OPEN_BISECT_HOSTING = screen -> Minecraft.getInstance().setScreen(new BHOrderScreen(screen));
    
    @Override
    public <E extends AbstractList.AbstractListEntry<E>> boolean filterServerListEntry(E entry) {
        return entry instanceof BannerEntry;
    }

    @Override
    public boolean clickAction(PromoData data, Screen parent) {
        if (!data.uuid().equals(Integrations.BISECTHOSTING_MOD_UUID)) {
            return false;
        }

        Minecraft.getInstance().setScreen(new BHOrderScreen(parent));
        return true;
    }
}
