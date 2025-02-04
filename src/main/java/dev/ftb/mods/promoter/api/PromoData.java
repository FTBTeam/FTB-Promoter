package dev.ftb.mods.promoter.api;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Promotions TS data
 *   uuid: string;
 *   logo: string;
 *   name: string;
 *   tooltip: string | null;
 *   lineOneSubtitle: string | null;
 *   lineTwoSubtitle: string | null;
 *   description: string;
 *   announcement: string | null;
 *   announcementTooltip: string | null;
 *   buttonText: string | null;
 *   buttonTooltip: string | null;
 *   url: string | null;
 */

public record PromoData(
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
}
