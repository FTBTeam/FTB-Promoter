package dev.ftb.mods.promoter.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.ftb.mods.promoter.api.InfoFetcher;
import dev.ftb.mods.promoter.api.PromoData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ServerSelectionList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.*;

public class ScreenInitEvent {
    @SubscribeEvent
    public static void onScreenInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof MultiplayerScreen)) {
            return;
        }

        for (IGuiEventListener guiEventListener : event.getGui().children()) {
            if (guiEventListener instanceof ServerSelectionList) {
                ServerSelectionList selectionList = (ServerSelectionList) guiEventListener;
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
                selectionList.children().add(0, new ServerPromotionEntry(event.getGui()));
            }
        }
    }

    private static class ServerPromotionEntry extends ServerSelectionList.LanScanEntry {
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
        public boolean mouseClicked(double p_331676_, double p_330254_, int p_331536_) {
            Minecraft.getInstance().setScreen(new AdScreen(this.parent));
            return false;
        }

        @Override
        public void render(MatrixStack stack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            int halfWidth = width / 2;

            if (isMouseOver(mouseX, mouseY)) {
                AbstractGui.fill(stack, left - 2, top, left + width - 4, top + height, 0x30FFFFFF);
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

        private final ITextComponent entryTooltipLines;
        private final ITextComponent announcementTooltipLines;

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

        @Nullable
        private StringTextComponent createTooltipLines(@Nullable String tooltip) {
            if (tooltip == null || tooltip.isEmpty()) {
                return null;
            }

            // This version of MC should handle this for us quite nicely so we'll just remove new lines
            String tooltipText = tooltip.replaceAll("\\n", "");
            return new StringTextComponent(tooltipText);
        }

        public void render(MatrixStack stack, int x, int y, int mouseX, int mouseY, int width, int height) {
            AbstractGui.drawString(stack, Minecraft.getInstance().font, data.name(), x + 35, y + 2, 0xFFFFFF);

            if (data.lineOneSubtitle() != null && !data.lineOneSubtitle().isEmpty()) {
                stack.pushPose();
                stack.translate(x + 35, y + 14, 0);
                stack.scale(0.75f, 0.75f, 0.75f);
                AbstractGui.drawString(stack, Minecraft.getInstance().font, data.lineOneSubtitle(), 0, 0, 0xB2FFFFFF);
                if (data.lineTwoSubtitle() != null && !data.lineTwoSubtitle().isEmpty()) {
                    stack.translate(0, 12, 0);
                    AbstractGui.drawString(stack, Minecraft.getInstance().font, data.lineTwoSubtitle(), 0, 0, 0xB2FFFFFF);
                }
                stack.popPose();
            }

            Minecraft.getInstance().getTextureManager().bind(texture.getTextureLocation());
            AbstractGui.blit(stack, x, y + 1, 0, 0, 30, 30, 30, 30);

            if (data.announcement() != null && !data.announcement().isEmpty()) {
                isOverAnnouncement = renderAnnouncement(stack, x, y, width, data.announcement(), this.announcementTooltipLines, mouseX, mouseY, false);
            }
        }

        public void renderToolTips(MatrixStack stack, int x, int y, int mouseX, int mouseY, int width, int height) {
            if (this.entryTooltipLines != null && !isOverAnnouncement) {
                if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                    this.parent.parent.renderTooltip(stack, this.entryTooltipLines, mouseX, mouseY + 10);
                }
            }

            if (isOverAnnouncement && this.announcementTooltipLines != null) {
                this.parent.parent.renderTooltip(stack, this.announcementTooltipLines, mouseX, mouseY + 10);
            }
        }
    }

    public static boolean renderAnnouncement(MatrixStack stack, int x, int y, int width, String announcement, @Nullable ITextComponent tooltipLines, int mouseX, int mouseY, boolean offset) {
        boolean isOnlyPromo = InfoFetcher.get().getPromotions().size() == 1;

        if (isOnlyPromo && offset) {
            x -= 74;
        }

        announcement = announcement.toUpperCase();
        FontRenderer font = Minecraft.getInstance().font;
        int textWidth = font.width(announcement);

        int textX = (int) (x + width - (textWidth * 0.6F) - 12);
        int textY = y - 1;
        int borderSize = 1;
        int paddingX = 3;
        int paddingY = 2;
        int textHeight = 8;

        // Put the text on top of everything
        stack.pushPose();
        stack.translate(textX, textY, 100);
        stack.scale(0.6f, 0.6f, 0.6f);
        AbstractGui.fill(stack, -paddingX - borderSize, -paddingY - borderSize, textWidth + paddingX + borderSize, textHeight + paddingY + borderSize, 0x80965222);
        AbstractGui.fill(stack, -paddingX, -paddingY, textWidth + paddingX, textHeight + paddingY, 0xFFEE883F);

        AbstractGui.drawString(stack, font, announcement, 0, 0, 0xFFFFFF);
        stack.popPose();

        boolean overAnnouncement = false;
        if (tooltipLines != null) {
            if (mouseX >= (textX - paddingX) && mouseX <= textX + ((textWidth + (paddingX * 2)) * 0.6F) && mouseY >= textY && mouseY <= textY + textHeight) {
                overAnnouncement = true;
            }
        }

        return overAnnouncement;
    }
}
