package dev.ftb.mods.promoter.mixin;

import dev.ftb.mods.promoter.screen.ScreenInitEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerSelectionList.class)
public class ServerSelectionListMixin extends ObjectSelectionList<ServerSelectionList.Entry> {
    @Shadow
    @Final
    public JoinMultiplayerScreen screen;

    public ServerSelectionListMixin(Minecraft p_94442_, int p_94443_, int p_94444_, int p_94445_, int p_94446_) {
        super(p_94442_, p_94443_, p_94444_, p_94445_, p_94446_);
    }

    @Inject(method = "refreshEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/multiplayer/ServerSelectionList;clearEntries()V", shift = At.Shift.AFTER))
    private void refreshEntries(CallbackInfo ci) {
        this.addEntry(new ScreenInitEvent.ServerPromotionEntry(this.screen));
    }
}
