package dev.ftb.mods.promoter.mixin;

import dev.ftb.mods.promoter.screen.ScreenInitEvent;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerSelectionList.class)
public class ServerSelectionListMixin {
    @Inject(method = "refreshEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/multiplayer/ServerSelectionList;clearEntries()V", shift = At.Shift.AFTER))
    private void refreshEntries(CallbackInfo ci) {
        ServerSelectionList list = ((ServerSelectionList) (Object) this);
        list.children().addFirst(new ScreenInitEvent.ServerPromotionEntry(list.screen));
    }
}
