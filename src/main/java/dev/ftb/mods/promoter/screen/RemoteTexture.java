package dev.ftb.mods.promoter.screen;

import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class RemoteTexture {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteTexture.class);
    public static final Set<ResourceLocation> LOADED_TEXTURES = new HashSet<>();

    private static final ResourceLocation MISSING_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/unknown_server.png");

    private final TextureManager textureManager;
    private final ResourceLocation textureLocation;
    private final String textureLocalName;

    private boolean ready = false;

    private final URI url;

    public RemoteTexture(URI uri, String textureLocalName, TextureManager textureManager) {
        this.url = uri;
        this.textureLocalName = textureLocalName;

        this.textureManager = textureManager;
        this.textureLocation = ResourceLocation.fromNamespaceAndPath("ftbpromoter", "promos/" + textureLocalName);

        this.uploadTexture();
    }

    public void uploadTexture() {
        if (LOADED_TEXTURES.contains(this.textureLocation)) {
            this.ready = true;
            return;
        }

        // Download the image
        if (!this.url.getHost().equals("cdn.feed-the-beast.com")) {
            return;
        }

        if (!this.url.getScheme().equals("https")) {
            return;
        }

        // Download the image
        var root = FMLPaths.GAMEDIR.get().resolve("downloads/promos");
        var file = new File(root.toFile(), this.textureLocalName);

        this.textureManager.register(this.textureLocation, new HttpTexture(file, this.url.toString(), MISSING_LOCATION, false, () -> {
            this.ready = true;
            LOADED_TEXTURES.add(this.textureLocation);
        }));
    }

    public ResourceLocation getTextureLocation() {
        return this.ready ? this.textureLocation : MISSING_LOCATION;
    }
}
