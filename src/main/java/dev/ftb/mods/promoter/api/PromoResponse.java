package dev.ftb.mods.promoter.api;

import java.util.List;

public record PromoResponse(
        List<PromoData> promotions
) {}
