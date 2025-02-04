package dev.ftb.mods.promoter.integrations;

import dev.ftb.mods.promoter.api.PromoData;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.Screen;

public interface Integration {
    <E extends AbstractSelectionList.Entry<E>> boolean filterServerListEntry(E entry);

    boolean clickAction(PromoData data, Screen parent);
}
