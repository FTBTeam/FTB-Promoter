package dev.ftb.mods.promoter.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.ftb.mods.promoter.api.InfoFetcher;
import dev.ftb.mods.promoter.api.PromoData;
import dev.ftb.mods.promoter.integrations.Integrations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ServerSelectionList;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class AdScreen extends Screen {
    private final List<PromoDataHolder> promos = new ArrayList<>();
    private Screen parent;

    private Button buttonLeft = null;
    private Button buttonRight = null;

    public AdScreen(Screen parent) {
        super(StringTextComponent.EMPTY);
        this.parent = parent;

        for (PromoData promo : InfoFetcher.get().getPromotions()) {
            promos.add(new PromoDataHolder(promo));
        }
    }

    @Override
    protected void init() {
        super.init();

        int middle = this.width / 2;
        int sectionOneLeft = middle - 154 - 2;

        this.addButton(new ButtonBuilder(new StringTextComponent("X"), (b) -> onClose())
                .pos(sectionOneLeft + 308 - 18, 3)
                .size(14, 14)
                .build()
        );

        int buttonWidth = 148;

        int horizontalShift = promos.size() == 1 ? middle - (154/2) : sectionOneLeft;
        for (PromoDataHolder promotion : promos) {
            ButtonBuilder button = new ButtonBuilder(new StringTextComponent(promotion.getData().buttonText()), (b) -> {
                        boolean result = Integrations.clickAction(promotion.getData(), this);
                        String url = promotion.getData().url();
                        if (!result && url != null && !url.isEmpty()) {
                            Minecraft.getInstance().setScreen(new ConfirmOpenLinkScreen((success) -> {
                                if (success) {
                                    Util.getPlatform().openUri("https://aka.ms/JavaAccountSettings");
                                }

                                Minecraft.getInstance().setScreen(this);
                            }, url, true));
                        }
                    })
                    .pos(horizontalShift + 3, this.height - 30)
                    .width(buttonWidth);

            String buttonTooltip = promotion.data.buttonTooltip();
            if (buttonTooltip != null && !buttonTooltip.isEmpty()) {
                button.tooltip(this, Collections.singletonList(new StringTextComponent(buttonTooltip)));
            }

            Button btn = button.build();
            if (buttonLeft == null) {
                buttonLeft = btn;
            } else {
                buttonRight = btn;
            }

            addButton(btn);

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
    public void renderBackground(MatrixStack guiGraphics) {
        super.renderBackground(guiGraphics);

        int availableWidth = getAvailableWidth(promos);
        int middle = this.width / 2;

        int horizontalShift = (middle - 2) - (availableWidth * promos.size()) / 2;
        for (PromoDataHolder promotion : promos) {
            renderSectionBackground(promotion, guiGraphics, horizontalShift, availableWidth);
            horizontalShift += availableWidth + 2;
        }
    }

    @Override
    public void render(MatrixStack guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int availableWidth = getAvailableWidth(promos);
        int middle = this.width / 2 - 2;

        int horizontalShift = middle - (availableWidth * promos.size()) / 2;
        for (PromoDataHolder promotion : promos) {
            boolean isLeft = promotion == promos.get(0);
            renderSection(promotion, guiGraphics, horizontalShift, availableWidth, mouseX, mouseY, isLeft ? buttonLeft : buttonRight);
            horizontalShift += availableWidth + 2;
        }
    }

    private static int getAvailableWidth(List<PromoDataHolder> promos) {
        return promos.size() > 1 ? 154 : 308;
    }

    private void renderSection(PromoDataHolder holder, MatrixStack poseStack, int x, int width, int mouseX, int mouseY, Button btn) {
        PromoData data = holder.getData();

        Minecraft.getInstance().getTextureManager().bind(holder.getLogo().getTextureLocation());
        blit(poseStack, x + width / 2 - 15, 10, 0, 0, 30, 30, 30, 30);

        drawCenteredString(poseStack, Minecraft.getInstance().font, data.name(), x + width / 2, 50, 0xFFFFFF);

        List<IReorderingProcessor> description = holder.getDescription().get(width - 4);
        int lineOffset = 0;
        for (IReorderingProcessor processor : description) {
            Minecraft.getInstance().font.draw(poseStack, processor, x + 8f, 70f + lineOffset, 0xFFFFFF);
            lineOffset += Minecraft.getInstance().font.lineHeight;
        }

        String announcement = data.announcement();
        if (announcement != null && !announcement.isEmpty() && !btn.isHovered()) {
            ScreenInitEvent.renderAnnouncement(poseStack, x, this.height - 30, width, announcement, null, mouseX, mouseY, true);
        }
    }

    private void renderSectionBackground(PromoDataHolder holder, MatrixStack stack, int x, int width) {
        fill(stack, x, 0, x + width, this.height, 0x09FFFFFF);
        fill(stack, x + 2, 0, x + width - 2, this.height, 0x80000000);
    }

    private static class PromoDataHolder {
        private final PromoData data;
        private final RemoteTexture logo;
        private final KeyBasedValueCache<Integer, List<IReorderingProcessor>> description;

        public PromoDataHolder(PromoData data) {
            this.data = data;
            this.logo = new RemoteTexture(URI.create(data.logo()), data.uuid().toString() + data.logoVersion(), Minecraft.getInstance().getTextureManager());
            this.description = new KeyBasedValueCache<>((key) -> Minecraft.getInstance().font.split(new StringTextComponent(data.description()), key));
        }

        public PromoData getData() {
            return data;
        }

        public RemoteTexture getLogo() {
            return logo;
        }

        public KeyBasedValueCache<Integer, List<IReorderingProcessor>> getDescription() {
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

    private static class ButtonBuilder {
        private int x = 0;
        private int y = 0;
        private int width = 120;
        private int height = 20;
        private ITextComponent text;
        private Button.IPressable onPress;
        private Button.ITooltip onTooltip = Button.NO_TOOLTIP;

        public ButtonBuilder(ITextComponent text, Button.IPressable onPress) {
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

        public ButtonBuilder tooltip(Screen screen, List<ITextComponent> text) {
            this.onTooltip = (button, stack, x, y) -> screen.renderComponentTooltip(stack, text, x, y);
            return this;
        }

        public Button build() {
            return new Button(this.x, this.y, this.width, this.height, this.text, this.onPress, this.onTooltip);
        }
    }
}
