package dev.ftb.mods.promoter.mixin;

import dev.ftb.mods.promoter.integrations.Integrations;
import net.minecraft.client.gui.screen.ServerSelectionList;
import net.minecraft.client.gui.widget.list.AbstractList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractList.class)
public abstract class AbstractSelectionListMixin<E extends AbstractList.AbstractListEntry<E>> {
    @Inject(method = "addEntry", at = @At("HEAD"), cancellable = true)
    private void addEntry(E entry, CallbackInfoReturnable<Integer> cir) {
        AbstractList<?> self = (AbstractList<?>) (Object) this;

        // Don't do anything if this isn't a ServerSelectionList
        if (!(self instanceof ServerSelectionList)) {
            return;
        }

        if (Integrations.denyEntry(entry)) {
            cir.setReturnValue(self.children().size() - 1);
        }
    }
}
