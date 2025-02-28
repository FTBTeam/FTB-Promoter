package dev.ftb.mods.promoter.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.promoter.api.InfoFetcher;
import dev.ftb.mods.promoter.api.PromoData;
import dev.ftb.mods.promoter.integrations.Integrations;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdScreen extends Screen {
    private final List<PromoDataHolder> promos = new ArrayList<>();
    private Screen parent;

    private Button buttonLeft = null;
    private Button buttonRight = null;

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

        addRenderableWidget(new ButtonBuilder(Component.literal("X"), (b) -> onClose())
                .pos(sectionOneLeft + 308 - 18, 3)
                .size(14, 14)
                .build()
        );

        var buttonWidth = 148;

        var horizontalShift = promos.size() == 1 ? middle - (154/2) : sectionOneLeft;
        for (PromoDataHolder promotion : promos) {
            ButtonBuilder button = new ButtonBuilder(Component.literal(promotion.getData().buttonText()), (b) -> {
                        var result = Integrations.clickAction(promotion.getData(), this);
                        String url = promotion.getData().url();
                        if (!result && url != null && !url.isEmpty()) {
                            Minecraft.getInstance().setScreen(new ConfirmLinkScreen((success) -> {
                                if (success) {
                                    Util.getPlatform().openUri("https://aka.ms/JavaAccountSettings");
                                }

                                Minecraft.getInstance().setScreen(this);
                            }, Component.literal(promotion.getData().name()), url, true));
                        }
                    })
                    .pos(horizontalShift + 3, this.height - 30)
                    .width(buttonWidth);

            var buttonTooltip = promotion.data.buttonTooltip();
            if (buttonTooltip != null && !buttonTooltip.isEmpty()) {
                button.tooltip(this, Collections.singletonList(Component.literal(buttonTooltip)));
            }

            Button btn = button.build();
            if (buttonLeft == null) {
                buttonLeft = btn;
            } else {
                buttonRight = btn;
            }

            addRenderableWidget(btn);

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
    public void renderBackground(PoseStack guiGraphics) {
        super.renderBackground(guiGraphics);

        var availableWidth = getAvailableWidth(promos);
        var middle = this.width / 2;

        var horizontalShift = (middle - 2) - (availableWidth * promos.size()) / 2;
        for (PromoDataHolder promotion : promos) {
            renderSectionBackground(promotion, guiGraphics, horizontalShift, availableWidth);
            horizontalShift += availableWidth + 2;
        }
    }

    @Override
    public void render(PoseStack guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        var availableWidth = getAvailableWidth(promos);
        var middle = this.width / 2 - 2;

        var horizontalShift = middle - (availableWidth * promos.size()) / 2;
        for (PromoDataHolder promotion : promos) {
            var isLeft = promotion == promos.get(0);
            renderSection(promotion, guiGraphics, horizontalShift, availableWidth, mouseX, mouseY, isLeft ? buttonLeft : buttonRight);
            horizontalShift += availableWidth + 2;
        }
    }

    private static int getAvailableWidth(List<PromoDataHolder> promos) {
        return promos.size() > 1 ? 154 : 308;
    }

    private void renderSection(PromoDataHolder holder, PoseStack poseStack, int x, int width, int mouseX, int mouseY, Button btn) {
        var data = holder.getData();

        RenderSystem.setShaderTexture(0, holder.getLogo().getTextureLocation());
        blit(poseStack, x + width / 2 - 15, 10, 0, 0, 30, 30, 30, 30);

        drawCenteredString(poseStack, Minecraft.getInstance().font, data.name(), x + width / 2, 50, 0xFFFFFF);

        var description = holder.getDescription();
        description.renderLeftAligned(poseStack, x + 8, 70, 10, 0xFFFFFF);

        String announcement = data.announcement();
        if (announcement != null && !announcement.isEmpty() && !btn.isHoveredOrFocused()) {
            ScreenInitEvent.renderAnnouncement(poseStack, x, this.height - 30, width, announcement, null, mouseX, mouseY);
        }
    }

    private void renderSectionBackground(PromoDataHolder holder, PoseStack stack, int x, int width) {
        fill(stack, x, 0, x + width, this.height, 0x09FFFFFF);
        fill(stack, x + 2, 0, x + width - 2, this.height, 0x80000000);
    }

    private static class PromoDataHolder {
        private final PromoData data;
        private final RemoteTexture logo;
        private final MultiLineLabel description;

        public PromoDataHolder(PromoData data) {
            this.data = data;
            this.logo = new RemoteTexture(URI.create(data.logo()), data.uuid().toString() + data.logoVersion(), Minecraft.getInstance().getTextureManager());
            this.description = MultiLineLabel.create(Minecraft.getInstance().font, Component.literal(data.description()), 140);
        }

        public PromoData getData() {
            return data;
        }

        public RemoteTexture getLogo() {
            return logo;
        }

        public MultiLineLabel getDescription() {
            return description;
        }
    }

    private static class ButtonBuilder {
        private int x = 0;
        private int y = 0;
        private int width = Button.DEFAULT_WIDTH;
        private int height = Button.DEFAULT_HEIGHT;
        private Component text;
        private Button.OnPress onPress;
        private Button.OnTooltip onTooltip = Button.NO_TOOLTIP;

        public ButtonBuilder(Component text, Button.OnPress onPress) {
            this.text = text;
            this.onPress = onPress;
        }

        public ButtonBuilder x(int x) {
            this.x = x;
            return this;
        }

        public ButtonBuilder y(int y) {
            this.y = y;
            return this;
        }

        public ButtonBuilder width(int width) {
            this.width = width;
            return this;
        }

        public ButtonBuilder height(int height) {
            this.height = height;
            return this;
        }

        public ButtonBuilder bounds(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            return this;
        }

        public ButtonBuilder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public ButtonBuilder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public ButtonBuilder tooltip(Screen screen, List<Component> text) {
            this.onTooltip = (button, stack, x, y) -> screen.renderComponentTooltip(stack, text, x, y);
            return this;
        }

        public Button build() {
            return new Button(this.x, this.y, this.width, this.height, this.text, this.onPress, this.onTooltip);
        }
    }
}
