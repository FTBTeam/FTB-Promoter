package dev.ftb.mods.promoter.mixin;

import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public class OnlineServerEntryMixin {
    @Shadow
    @Final
    private JoinMultiplayerScreen screen;

    @Inject(method = "swap", at = @At("HEAD"), cancellable = true)
    private void swap(int pos1, int pos2, CallbackInfo ci) {
        int size = this.screen.getServers().size();
        if (pos1 > size - 1 || pos2 > size - 1  || pos1 < 0 || pos2 < 0) {
            ci.cancel();
        }
    }
}
