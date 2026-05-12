package dev.ftb.mods.promoter.utils.accessors;

import net.minecraft.client.gui.components.AbstractSelectionList;

import java.util.List;

public interface AbstractSelectionAccessor<E extends AbstractSelectionList.Entry<E>> {
    List<E> getChildren();
}
