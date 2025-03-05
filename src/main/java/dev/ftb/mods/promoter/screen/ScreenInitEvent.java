package dev.ftb.mods.promoter.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ftb.mods.promoter.api.InfoFetcher;
import dev.ftb.mods.promoter.api.PromoData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.*;

public class ScreenInitEvent {
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
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
            return Component.empty();
        }

        @Override
        public boolean mouseClicked(double p_331676_, double p_330254_, int p_331536_) {
            Minecraft.getInstance().setScreen(new AdScreen(this.parent));
            return false;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            var halfWidth = width / 2;

            if (isMouseOver(mouseX, mouseY)) {
                guiGraphics.fill(left - 2, top, left + width - 4, top + height, 0x30FFFFFF);
            }

            int leftOffset = 0;
            for (EntryOption option : options) {
                option.render(guiGraphics, left + leftOffset, top, mouseX, mouseY, options.size() == 1 ? width : halfWidth, height);
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
                components.add(Component.literal(line));
            }

            return components;
        }

        public void render(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, int width, int height) {
            graphics.drawString(Minecraft.getInstance().font, data.name(), x + 35, y + 2, 0xFFFFFF);

            if (data.lineOneSubtitle() != null && !data.lineOneSubtitle().isEmpty()) {
                var pose = graphics.pose();
                pose.pushPose();
                pose.translate(x + 35, y + 14, 0);
                pose.scale(0.75f, 0.75f, 0.75f);
                graphics.drawString(Minecraft.getInstance().font, data.lineOneSubtitle(), 0, 0, 0xB2FFFFFF);
                if (data.lineTwoSubtitle() != null && !data.lineTwoSubtitle().isEmpty()) {
                    pose.translate(0, 12, 0);
                    graphics.drawString(Minecraft.getInstance().font, data.lineTwoSubtitle(), 0, 0, 0xB2FFFFFF);
                }
                pose.popPose();
            }

            graphics.blit(texture.getTextureLocation(), x, y + 1, 0, 0, 30, 30, 30, 30);

            boolean overAnnouncement = false;
            if (data.announcement() != null && !data.announcement().isEmpty()) {
                overAnnouncement = renderAnnouncement(graphics, x, y, width, data.announcement(), this.announcementTooltipLines, mouseX, mouseY);
            }

            if (!this.entryTooltipLines.isEmpty() && !overAnnouncement) {
                if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                    graphics.renderTooltip(Minecraft.getInstance().font, this.entryTooltipLines, Optional.empty(), mouseX, mouseY + 10);
                }
            }
        }
    }

    public static boolean renderAnnouncement(GuiGraphics graphics, int x, int y, int width, String announcement, @Nullable List<Component> tooltipLines, int mouseX, int mouseY) {
        announcement = announcement.toUpperCase();
        Font font = Minecraft.getInstance().font;
        var textWidth = font.width(announcement);

        var textX = x + width - (textWidth * 0.6F) - 12;
        var textY = y - 1;
        var borderSize = 1;
        var paddingX = 3;
        var paddingY = 2;
        var textHeight = 8;

        PoseStack pose = graphics.pose();
        // Put the text on top of everything
        pose.pushPose();
        pose.translate(textX, textY, 100);
        pose.scale(0.6f, 0.6f, 0.6f);
        graphics.fill(-paddingX - borderSize, -paddingY - borderSize, textWidth + paddingX + borderSize, textHeight + paddingY + borderSize, 0x80965222);
        graphics.fill(-paddingX, -paddingY, textWidth + paddingX, textHeight + paddingY, 0xFFEE883F);

        graphics.drawString(font, announcement, 0, 0, 0xFFFFFF);
        pose.popPose();

        boolean overAnnouncement = false;
        if (tooltipLines != null && !tooltipLines.isEmpty()) {
            if (mouseX >= (textX - paddingX) && mouseX <= textX + ((textWidth + (paddingX * 2)) * 0.6F) && mouseY >= textY && mouseY <= textY + textHeight) {
                overAnnouncement = true;
                graphics.renderTooltip(font, tooltipLines, Optional.empty(), mouseX, mouseY + 10);
            }
        }

        return overAnnouncement;
    }
}
