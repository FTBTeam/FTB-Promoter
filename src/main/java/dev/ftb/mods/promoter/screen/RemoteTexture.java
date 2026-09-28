package dev.ftb.mods.promoter.screen;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FileUtil;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class RemoteTexture {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteTexture.class);
    public static final Set<Identifier> LOADED_TEXTURES = new HashSet<>();

    private static final Identifier MISSING_LOCATION = Identifier.withDefaultNamespace("textures/misc/unknown_server.png");

    private final TextureManager textureManager;
    private final Identifier textureLocation;
    private final String textureLocalName;

    private boolean ready = false;

    private final URI url;

    public RemoteTexture(URI uri, String textureLocalName, TextureManager textureManager) {
        this.url = uri;
        this.textureLocalName = textureLocalName;

        this.textureManager = textureManager;
        this.textureLocation = Identifier.fromNamespaceAndPath("ftbpromoter", "promos/" + textureLocalName);

        Minecraft.getInstance().schedule(this::downloadTexture);
    }

    public void downloadTexture() {
        if (!this.url.getHost().equals("cdn.feed-the-beast.com")) {
            return;
        }

        if (!this.url.getScheme().equals("https")) {
            return;
        }

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) this.url.toURL().openConnection(Minecraft.getInstance().getProxy());
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode / 100 != 2) {
                String localUrl = String.valueOf(this.url);
                throw new IOException("Failed to open " + localUrl + ", HTTP error code: " + responseCode);
            }

            var root = FMLPaths.GAMEDIR.get().resolve("downloads/promos");
            var file = new File(root.toFile(), this.textureLocalName);

            byte[] imageContents = connection.getInputStream().readAllBytes();

            try {
                FileUtil.createDirectoriesSafe(file.getParentFile().toPath());
                Files.write(file.toPath(), imageContents);
            } catch (IOException error) {
                LOGGER.warn("Failed to cache texture {} in {}", url, file.getAbsolutePath(), error);
            }

            try (var nativeImage = NativeImage.read(imageContents)) {
                this.textureManager.register(this.textureLocation, new DynamicTexture(() -> this.textureLocalName, nativeImage));
                this.ready = true;
                LOADED_TEXTURES.add(this.textureLocation);
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to download texture from {}", url, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public Identifier getTextureLocation() {
        return this.ready ? this.textureLocation : MISSING_LOCATION;
    }
}
