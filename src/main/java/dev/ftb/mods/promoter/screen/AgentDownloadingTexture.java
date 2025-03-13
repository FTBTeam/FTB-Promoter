package dev.ftb.mods.promoter.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class AgentDownloadingTexture extends DownloadingTexture {
    private static final Logger LOGGER = LogManager.getLogger();

    private final SimpleTexture fallbackSimpleTexture;

    public AgentDownloadingTexture(@Nullable File file, String url, ResourceLocation location, @Nullable Runnable onDownloadComplete) {
        super(file, url, location, false, onDownloadComplete);
        this.fallbackSimpleTexture = new SimpleTexture(location);
    }

    /**
     * Essentially the exact same as the super method but includes a user-agent to keep cloudflare happy
     */
    @Override
    public void load(IResourceManager manager) throws IOException {
        Minecraft.getInstance().execute(() -> {
            if (!this.uploaded) {
                try {
                    this.fallbackSimpleTexture.load(manager);
                } catch (IOException e) {
                    LOGGER.error("Failed to load fallback texture", e);
                }

                this.uploaded = true;
            }
        });

        if (this.future == null) {
            NativeImage nativeimage;
            if (this.file != null && this.file.isFile()) {
                LOGGER.debug("Loading http texture from local cache ({})", this.file);
                FileInputStream fileinputstream = new FileInputStream(this.file);
                nativeimage = this.load(fileinputstream);
            } else {
                nativeimage = null;
            }

            if (nativeimage != null) {
                this.loadCallback(nativeimage);
            } else {
                this.future = CompletableFuture.runAsync(() -> {
                    HttpURLConnection httpurlconnection = null;
                    LOGGER.debug("Downloading http texture from {} to {}", this.urlString, this.file);

                    try {
                        httpurlconnection = (HttpURLConnection)(new URL(this.urlString)).openConnection(Minecraft.getInstance().getProxy());
                        httpurlconnection.setDoInput(true);
                        httpurlconnection.setDoOutput(false);
                        httpurlconnection.setRequestProperty("User-Agent", "Mozilla/5.0");
                        httpurlconnection.connect();
                        if (httpurlconnection.getResponseCode() / 100 == 2) {
                            InputStream inputstream;
                            if (this.file != null) {
                                FileUtils.copyInputStreamToFile(httpurlconnection.getInputStream(), this.file);
                                inputstream = new FileInputStream(this.file);
                            } else {
                                inputstream = httpurlconnection.getInputStream();
                            }

                            Minecraft.getInstance().execute(() -> {
                                NativeImage nativeimage1 = this.load(inputstream);
                                if (nativeimage1 != null) {
                                    this.loadCallback(nativeimage1);
                                }

                            });
                        }
                    } catch (Exception exception) {
                        LOGGER.error("Couldn't download http texture", exception);
                    } finally {
                        if (httpurlconnection != null) {
                            httpurlconnection.disconnect();
                        }

                    }

                }, Util.backgroundExecutor());
            }
        }
    }
}
