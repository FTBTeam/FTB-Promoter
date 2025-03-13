package dev.ftb.mods.promoter.mixin;

import dev.ftb.mods.promoter.api.InfoFetcher;
import dev.ftb.mods.promoter.screen.ScreenInitEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.ServerSelectionList;
import net.minecraft.client.gui.widget.list.ExtendedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerSelectionList.class)
public abstract class ServerSelectionListMixin extends ExtendedList<ServerSelectionList.Entry> {
    @Shadow @Final private MultiplayerScreen screen;

    // Ignored
    public ServerSelectionListMixin(Minecraft p_i45010_1_, int p_i45010_2_, int p_i45010_3_, int p_i45010_4_, int p_i45010_5_, int p_i45010_6_) {
        super(p_i45010_1_, p_i45010_2_, p_i45010_3_, p_i45010_4_, p_i45010_5_, p_i45010_6_);
    }

    @Inject(method = "refreshEntries", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", ordinal = 0, shift = At.Shift.BEFORE))
    public void onRefresh(CallbackInfo ci) {
        ServerSelectionList selectionList = (ServerSelectionList) (Object) this;
        for (ServerSelectionList.Entry child : selectionList.children()) {
            if (child instanceof ScreenInitEvent.ServerPromotionEntry) {
                return;
            }
        }

        if (InfoFetcher.get().getPromotions().isEmpty()) {
            return;
        }

        this.addEntry(new ScreenInitEvent.ServerPromotionEntry(this.screen));
    }
}
