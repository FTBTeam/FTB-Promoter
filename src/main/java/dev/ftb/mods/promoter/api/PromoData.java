package dev.ftb.mods.promoter.api;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * Promotions TS data
 * uuid: string;
 * logo: string;
 * name: string;
 * tooltip: string | null;
 * lineOneSubtitle: string | null;
 * lineTwoSubtitle: string | null;
 * description: string;
 * announcement: string | null;
 * announcementTooltip: string | null;
 * buttonText: string | null;
 * buttonTooltip: string | null;
 * url: string | null;
 */

public final class PromoData {
    private UUID uuid;
    private String name;
    private String logo;
    private int logoVersion;
    @Nullable
    private String tooltip;
    @Nullable
    private String lineOneSubtitle;
    @Nullable
    private String lineTwoSubtitle;
    private String description;
    @Nullable
    private String announcement;
    @Nullable
    private String announcementTooltip;
    private String buttonText;
    @Nullable
    private String buttonTooltip;
    @Nullable
    private String url;

    public PromoData(
            UUID uuid,
            String name,
            String logo,
            int logoVersion,
            @Nullable String tooltip,
            @Nullable String lineOneSubtitle,
            @Nullable String lineTwoSubtitle,
            String description,
            @Nullable String announcement,
            @Nullable String announcementTooltip,
            String buttonText,
            @Nullable String buttonTooltip,
            @Nullable String url
    ) {
        this.uuid = uuid;
        this.name = name;
        this.logo = logo;
        this.logoVersion = logoVersion;
        this.tooltip = tooltip;
        this.lineOneSubtitle = lineOneSubtitle;
        this.lineTwoSubtitle = lineTwoSubtitle;
        this.description = description;
        this.announcement = announcement;
        this.announcementTooltip = announcementTooltip;
        this.buttonText = buttonText;
        this.buttonTooltip = buttonTooltip;
        this.url = url;
    }

    public UUID uuid() {
        return uuid;
    }

    public String name() {
        return name;
    }

    public String logo() {
        return logo;
    }

    public int logoVersion() {
        return logoVersion;
    }

    @Nullable
    public String tooltip() {
        return tooltip;
    }

    @Nullable
    public String lineOneSubtitle() {
        return lineOneSubtitle;
    }

    @Nullable
    public String lineTwoSubtitle() {
        return lineTwoSubtitle;
    }

    public String description() {
        return description;
    }

    @Nullable
    public String announcement() {
        return announcement;
    }

    @Nullable
    public String announcementTooltip() {
        return announcementTooltip;
    }

    public String buttonText() {
        return buttonText;
    }

    @Nullable
    public String buttonTooltip() {
        return buttonTooltip;
    }

    @Nullable
    public String url() {
        return url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PromoData) obj;
        return Objects.equals(this.uuid, that.uuid) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.logo, that.logo) &&
                this.logoVersion == that.logoVersion &&
                Objects.equals(this.tooltip, that.tooltip) &&
                Objects.equals(this.lineOneSubtitle, that.lineOneSubtitle) &&
                Objects.equals(this.lineTwoSubtitle, that.lineTwoSubtitle) &&
                Objects.equals(this.description, that.description) &&
                Objects.equals(this.announcement, that.announcement) &&
                Objects.equals(this.announcementTooltip, that.announcementTooltip) &&
                Objects.equals(this.buttonText, that.buttonText) &&
                Objects.equals(this.buttonTooltip, that.buttonTooltip) &&
                Objects.equals(this.url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, logo, logoVersion, tooltip, lineOneSubtitle, lineTwoSubtitle, description, announcement, announcementTooltip, buttonText, buttonTooltip, url);
    }

    @Override
    public String toString() {
        return "PromoData[" +
                "uuid=" + uuid + ", " +
                "name=" + name + ", " +
                "logo=" + logo + ", " +
                "logoVersion=" + logoVersion + ", " +
                "tooltip=" + tooltip + ", " +
                "lineOneSubtitle=" + lineOneSubtitle + ", " +
                "lineTwoSubtitle=" + lineTwoSubtitle + ", " +
                "description=" + description + ", " +
                "announcement=" + announcement + ", " +
                "announcementTooltip=" + announcementTooltip + ", " +
                "buttonText=" + buttonText + ", " +
                "buttonTooltip=" + buttonTooltip + ", " +
                "url=" + url + ']';
    }

}
