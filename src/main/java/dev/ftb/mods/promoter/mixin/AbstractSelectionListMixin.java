package dev.ftb.mods.promoter.mixin;

import dev.ftb.mods.promoter.integrations.Integrations;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin<E extends AbstractSelectionList.Entry<E>> {
    @Inject(method = "addEntry", at = @At("HEAD"), cancellable = true)
    private void addEntry(E entry, CallbackInfoReturnable<Integer> cir) {
        @SuppressWarnings("unchecked")
        var self = (AbstractSelectionList<E>) (Object) this;

        // Don't do anything if this isn't a ServerSelectionList
        if (!(self instanceof ServerSelectionList)) {
            return;
        }

        if (Integrations.denyEntry(entry)) {
            cir.setReturnValue(self.children().size() - 1);
        }
    }

    @Inject(method = "addEntryToTop", at = @At("HEAD"), cancellable = true)
    private void addEntryToTop(E entry, CallbackInfo ci) {
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
