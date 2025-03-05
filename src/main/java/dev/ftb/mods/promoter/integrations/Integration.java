package dev.ftb.mods.promoter.integrations;

import dev.ftb.mods.promoter.api.PromoData;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.list.AbstractList;

public interface Integration {
    <E extends AbstractList.AbstractListEntry<E>> boolean filterServerListEntry(E entry);

    boolean clickAction(PromoData data, Screen parent);
}
