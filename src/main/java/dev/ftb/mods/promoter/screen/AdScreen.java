package dev.ftb.mods.promoter.screen;

import dev.ftb.mods.promoter.api.InfoFetcher;
import dev.ftb.mods.promoter.api.PromoData;
import dev.ftb.mods.promoter.integrations.Integrations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AdScreen extends Screen {
    private final List<PromoDataHolder> promos = new ArrayList<>();
    private Screen parent;

    public AdScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;

        for (PromoData promo : InfoFetcher.get().getPromotions()) {
            promos.add(new PromoDataHolder(promo));
        }
    }

    @Override
    protected void init() {
        super.init();

        var middle = this.width / 2;
        var sectionOneLeft = middle - 154 - 2;

        addRenderableWidget(Button.builder(Component.literal("X"), (b) -> onClose())
                .pos(sectionOneLeft + 308 - 18, 3)
                .size(14, 14)
                .build()
        );

        var buttonWidth = 148;

        var horizontalShift = promos.size() == 1 ? middle - (154/2) : sectionOneLeft;
        for (PromoDataHolder promotion : promos) {
            Button.Builder button = Button.builder(Component.literal(promotion.getData().buttonText()), (b) -> {
                        var result = Integrations.clickAction(promotion.getData(), this);
                        String url = promotion.getData().url();
                        if (!result && url != null && !url.isEmpty()) {
                            // Open the URL
                            var press = ConfirmLinkScreen.confirmLink(this, url);
                            press.onPress(b);
                        }
                    })
                    .pos(horizontalShift + 3, this.height - 30)
                    .width(buttonWidth);

            var buttonTooltip = promotion.data.buttonTooltip();
            if (buttonTooltip != null && !buttonTooltip.isEmpty()) {
                button.tooltip(Tooltip.create(Component.literal(buttonTooltip)));
            }

            addRenderableWidget(button.build());

            horizontalShift += 154 + 2;
        }
    }

    @Override
    public void onClose() {
        if (this.parent != null) {
            Minecraft.getInstance().setScreen(this.parent);
            return;
        }

        super.onClose();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        // TOD Render backgrounds
        var availableWidth = getAvailableWidth(promos);
        var middle = this.width / 2;

        var horizontalShift = (middle - 2) - (availableWidth * promos.size()) / 2;
        for (PromoDataHolder promotion : promos) {
            renderSectionBackground(promotion, guiGraphics, horizontalShift, availableWidth);
            horizontalShift += availableWidth + 2;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        var availableWidth = getAvailableWidth(promos);
        var middle = this.width / 2 - 2;

        var horizontalShift = middle - (availableWidth * promos.size()) / 2;
        for (PromoDataHolder promotion : promos) {
            renderSection(promotion, guiGraphics, horizontalShift, availableWidth, mouseX, mouseY);
            horizontalShift += availableWidth + 2;
        }
    }

    private static int getAvailableWidth(List<PromoDataHolder> promos) {
        return promos.size() > 1 ? 154 : 308;
    }

    private void renderSection(PromoDataHolder holder, GuiGraphics guiGraphics, int x, int width, int mouseX, int mouseY) {
        var data = holder.getData();

        guiGraphics.blit(holder.getLogo().getTextureLocation(), x + width / 2 - 15, 10, 0, 0, 30, 30, 30, 30);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, data.name(), x + width / 2, 50, 0xFFFFFF);

        var description = holder.getDescription();
        description.get(width - 4).renderLeftAligned(guiGraphics, x + 8, 70, 10, 0xFFFFFF);

        String announcement = data.announcement();
        if (announcement != null && !announcement.isEmpty()) {
            ScreenInitEvent.renderAnnouncement(guiGraphics, x, this.height - 30, width, announcement, null, mouseX, mouseY, true);
        }
    }

    private void renderSectionBackground(PromoDataHolder holder, GuiGraphics guiGraphics, int x, int width) {
        guiGraphics.fill(x, 0, x + width, this.height, 0x09FFFFFF);
        guiGraphics.fill(x + 2, 0, x + width - 2, this.height, 0x80000000);
    }

    private static class PromoDataHolder {
        private final PromoData data;
        private final RemoteTexture logo;
        private final KeyBasedValueCache<Integer, MultiLineLabel> description;

        public PromoDataHolder(PromoData data) {
            this.data = data;
            this.logo = new RemoteTexture(URI.create(data.logo()), data.uuid().toString() + data.logoVersion(), Minecraft.getInstance().getTextureManager());
            this.description = new KeyBasedValueCache<>((width) -> MultiLineLabel.create(Minecraft.getInstance().font, Component.literal(data.description()), width));
        }

        public PromoData getData() {
            return data;
        }

        public RemoteTexture getLogo() {
            return logo;
        }

        public KeyBasedValueCache<Integer, MultiLineLabel> getDescription() {
            return description;
        }
    }

    private static class KeyBasedValueCache<K, T> {
        private K key;

        private T value;
        private final Function<K, T> creator;

        public KeyBasedValueCache(Function<K, T> creator) {
            this.creator = creator;
        }

        public T get(K key) {
            if (this.key == null || !this.key.equals(key)) {
                this.key = key;
                this.value = creator.apply(key);
            }

            return value;
        }
    }
}
