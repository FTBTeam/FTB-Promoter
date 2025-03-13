package dev.ftb.mods.promoter.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.promoter.api.InfoFetcher;
import dev.ftb.mods.promoter.api.PromoData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.*;

public class ScreenInitEvent {
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.InitScreenEvent event) {
        if (!(event.getScreen() instanceof JoinMultiplayerScreen)) {
            return;
        }

        for (GuiEventListener guiEventListener : event.getListenersList()) {
            if (guiEventListener instanceof ServerSelectionList selectionList) {
                // Skip, we already have it
                for (ServerSelectionList.Entry child : selectionList.children()) {
                    if (child instanceof ServerPromotionEntry) {
                        return;
                    }
                }

                if (InfoFetcher.get().getPromotions().isEmpty()) {
                    return;
                }

                // addFirst is not supported in java 17, we'll need to do it ourselves
                selectionList.children().add(0, new ServerPromotionEntry(event.getScreen()));
            }
        }
    }

    private static class ServerPromotionEntry extends ServerSelectionList.LANHeader {
        final List<EntryOption> options = new ArrayList<>();
        private final Screen parent;

        public ServerPromotionEntry(Screen screen) {
            this.parent = screen;

            List<PromoData> promotions = InfoFetcher.get().getPromotions();
            if (promotions.isEmpty()) {
                return;
            }

            // Only get the first two, if there is more than two, it's not our problem
            for (PromoData promotion : promotions.subList(0, Math.min(2, promotions.size()))) {
                options.add(new EntryOption(this, promotion, new RemoteTexture(URI.create(promotion.logo()), promotion.uuid().toString() + promotion.logoVersion(), Minecraft.getInstance().getTextureManager())));
            }
        }

        @Override
        public Component getNarration() {
            return TextComponent.EMPTY;
        }

        @Override
        public boolean mouseClicked(double p_331676_, double p_330254_, int p_331536_) {
            Minecraft.getInstance().setScreen(new AdScreen(this.parent));
            return false;
        }

        @Override
        public void render(PoseStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            var halfWidth = width / 2;

            if (isMouseOver(mouseX, mouseY)) {
                GuiComponent.fill(stack, left - 2, top, left + width - 4, top + height, 0x30FFFFFF);
            }

            int leftOffset = 0;
            for (EntryOption option : options) {
                option.render(stack, left + leftOffset, top, mouseX, mouseY, options.size() == 1 ? width : halfWidth, height);
                leftOffset += halfWidth;
            }

            leftOffset = 0;
            for (EntryOption option : options) {
                option.renderToolTips(stack, left + leftOffset, top, mouseX, mouseY, options.size() == 1 ? width : halfWidth, height);
                leftOffset += halfWidth;
            }
        }
    }

    private static final class EntryOption {
        private final ServerPromotionEntry parent;
        private final PromoData data;
        private final RemoteTexture texture;

        private final List<Component> entryTooltipLines;
        private final List<Component> announcementTooltipLines;

        boolean isOverAnnouncement = false;

        private EntryOption(
                ServerPromotionEntry parent,
                PromoData data,
                RemoteTexture texture
        ) {
            this.parent = parent;
            this.data = data;
            this.texture = texture;

            this.announcementTooltipLines = this.createTooltipLines(data.announcementTooltip());
            this.entryTooltipLines = this.createTooltipLines(data.tooltip());
        }

        private List<Component> createTooltipLines(@Nullable String tooltip) {
            if (tooltip == null || tooltip.isEmpty()) {
                return Collections.emptyList();
            }

            var lines = tooltip.split("\n");
            List<Component> components = new ArrayList<>();
            for (var line : lines) {
                components.add(new TextComponent(line));
            }

            return components;
        }

        public void render(PoseStack stack, int x, int y, int mouseX, int mouseY, int width, int height) {
            GuiComponent.drawString(stack, Minecraft.getInstance().font, data.name(), x + 35, y + 2, 0xFFFFFF);

            if (data.lineOneSubtitle() != null && !data.lineOneSubtitle().isEmpty()) {
                stack.pushPose();
                stack.translate(x + 35, y + 14, 0);
                stack.scale(0.75f, 0.75f, 0.75f);
                GuiComponent.drawString(stack, Minecraft.getInstance().font, data.lineOneSubtitle(), 0, 0, 0xB2FFFFFF);
                if (data.lineTwoSubtitle() != null && !data.lineTwoSubtitle().isEmpty()) {
                    stack.translate(0, 12, 0);
                    GuiComponent.drawString(stack, Minecraft.getInstance().font, data.lineTwoSubtitle(), 0, 0, 0xB2FFFFFF);
                }
                stack.popPose();
            }

            RenderSystem.setShaderTexture(0, texture.getTextureLocation());
            GuiComponent.blit(stack, x, y + 1, 0, 0, 30, 30, 30, 30);

            if (data.announcement() != null && !data.announcement().isEmpty()) {
                isOverAnnouncement = renderAnnouncement(stack, x, y, width, data.announcement(), this.announcementTooltipLines, mouseX, mouseY, false);
            }
        }

        public void renderToolTips(PoseStack stack, int x, int y, int mouseX, int mouseY, int width, int height) {
            if (!this.entryTooltipLines.isEmpty() && !isOverAnnouncement) {
                if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                    this.parent.parent.renderTooltip(stack, this.entryTooltipLines, Optional.empty(), mouseX, mouseY + 10);
                }
            }

            if (isOverAnnouncement) {
                this.parent.parent.renderTooltip(stack, this.announcementTooltipLines, Optional.empty(), mouseX, mouseY + 10);
            }
        }
    }

    public static boolean renderAnnouncement(PoseStack stack, int x, int y, int width, String announcement, @Nullable List<Component> tooltipLines, int mouseX, int mouseY, boolean offset) {
        boolean isOnlyPromo = InfoFetcher.get().getPromotions().size() == 1;

        if (isOnlyPromo && offset) {
            x -= 74;
        }

        announcement = announcement.toUpperCase();
        Font font = Minecraft.getInstance().font;
        var textWidth = font.width(announcement);

        var textX = x + width - (textWidth * 0.6F) - 12;
        var textY = y - 1;
        var borderSize = 1;
        var paddingX = 3;
        var paddingY = 2;
        var textHeight = 8;

        // Put the text on top of everything
        stack.pushPose();
        stack.translate(textX, textY, 100);
        stack.scale(0.6f, 0.6f, 0.6f);
        GuiComponent.fill(stack, -paddingX - borderSize, -paddingY - borderSize, textWidth + paddingX + borderSize, textHeight + paddingY + borderSize, 0x80965222);
        GuiComponent.fill(stack, -paddingX, -paddingY, textWidth + paddingX, textHeight + paddingY, 0xFFEE883F);

        GuiComponent.drawString(stack, font, announcement, 0, 0, 0xFFFFFF);
        stack.popPose();

        boolean overAnnouncement = false;
        if (tooltipLines != null && !tooltipLines.isEmpty()) {
            if (mouseX >= (textX - paddingX) && mouseX <= textX + ((textWidth + (paddingX * 2)) * 0.6F) && mouseY >= textY && mouseY <= textY + textHeight) {
                overAnnouncement = true;
            }
        }

        return overAnnouncement;
    }
}
