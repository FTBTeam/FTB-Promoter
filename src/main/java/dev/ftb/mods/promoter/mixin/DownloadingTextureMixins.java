package dev.ftb.mods.promoter.mixin;

import net.minecraft.client.renderer.texture.DownloadingTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.net.HttpURLConnection;

@Mixin(DownloadingTexture.class)
public class DownloadingTextureMixins {
    @Inject(
            method = "lambda$load$4",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/net/HttpURLConnection;setDoOutput(Z)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void ftbpromoter$injectUserAgent(CallbackInfo ci, HttpURLConnection httpurlconnection) {
        // Inject a user agent
        httpurlconnection.setRequestProperty("User-Agent", "Mozilla/5.0");
    }
}
