package dev.ftb.mods.promoter.mixin;

import dev.ftb.mods.promoter.integrations.Integrations;
import dev.ftb.mods.promoter.utils.accessors.AbstractSelectionAccessor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin<E extends AbstractSelectionList.Entry<E>>
    implements AbstractSelectionAccessor<E> {

    @Accessor("children")
    public abstract List<E> getChildren();

    @Shadow public abstract List<E> children();

    @Inject(method = "addEntry(Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;I)I", at = @At("HEAD"), cancellable = true)
    private void addEntry(E entry, int height, CallbackInfoReturnable<Integer> cir) {
        @SuppressWarnings("unchecked")
        var self = (AbstractSelectionList<E>) (Object) this;

        // Don't do anything if this isn't a ServerSelectionList
        if (!(self instanceof ServerSelectionList)) {
            return;
        }

        if (Integrations.denyEntry(entry)) {
            cir.setReturnValue(this.children().size() - 1);
        }
    }

    @Inject(method = "addEntryToTop(Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;I)V", at = @At("HEAD"), cancellable = true)
    private void addEntryToTop(E entry, int height, CallbackInfo ci) {
        @SuppressWarnings("unchecked")
        var self = (AbstractSelectionList<E>) (Object) this;

        // Don't do anything if this isn't a ServerSelectionList
        if (!(self instanceof ServerSelectionList)) {
            return;
        }

        if (Integrations.denyEntry(entry)) {
            ci.cancel();
        }
    }
}
