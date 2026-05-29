package dev.ftb.mods.promoter.mixin;

import dev.ftb.mods.promoter.integrations.Integrations;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public class OnlineServerEntryMixin {
    @ModifyArg(method = "swap", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ServerList;swap(II)V"), index = 0, remap = false)
    private int offsetIndexOnSwapServers1(int index) {
        if (Integrations.isZeroIndexAlreadyPatched()) {
            return index;
        }

        return index - 1;
    }

    @ModifyArg(method = "swap", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ServerList;swap(II)V"), index = 1, remap = false)
    private int offsetIndexOnSwapServers2(int index) {
        if (Integrations.isZeroIndexAlreadyPatched()) {
            return index;
        }

        return index > 0 ? index - 1 : index;
    }

    @ModifyConstant(
            method = "render",
            constant = @Constant(intValue = 0, expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO, ordinal = 4)
    )
    private int ftbpromoter$fixMoveUpThreshold(int constant) {
        if (Integrations.isZeroIndexAlreadyPatched()) {
            return constant;
        }

        return 1;
    }

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ServerList;size()I")
    )
    private int ftbpromoter$inflateServerListSize(ServerList serverList) {
        if (Integrations.isZeroIndexAlreadyPatched()) {
            return serverList.size();
        }

        return serverList.size() + 1;
    }

    @Redirect(
            method = "keyPressed",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ServerList;size()I")
    )
    private int ftbpromoter$inflateServerListSizeKeyPressed(ServerList serverList) {
        // Bisect doesn't fix this one.
//        if (Integrations.isZeroIndexAlreadyPatched()) {
//            return serverList.size();
//        }

        return serverList.size() + 1;
    }

    @Redirect(
            method = "mouseClicked",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ServerList;size()I"
            )
    )
    private int ftbpromoter$inflateServerListSizeMouseClicked(ServerList serverList) {
        if (Integrations.isZeroIndexAlreadyPatched()) {
            return serverList.size();
        }

        return serverList.size() + 1;
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Ljava/util/List;indexOf(Ljava/lang/Object;)I", shift = At.Shift.AFTER), cancellable = true)
    private void ftbpromoter$rejectZeroIndexItem(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0, argsOnly = true) int i) {
        if (Integrations.isZeroIndexAlreadyPatched()) {
            return;
        }

        // If shift is used on the first item, reject it.
        if (i == 0) {
            cir.setReturnValue(true);
        }
    }
}
