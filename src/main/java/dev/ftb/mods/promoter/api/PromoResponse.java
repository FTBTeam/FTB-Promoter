package dev.ftb.mods.promoter.api;

import java.util.List;
import java.util.Objects;

public final class PromoResponse {
    private List<PromoData> promotions;

    public PromoResponse(
            List<PromoData> promotions
    ) {
        this.promotions = promotions;
    }

    public List<PromoData> promotions() {
        return promotions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PromoResponse) obj;
        return Objects.equals(this.promotions, that.promotions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(promotions);
    }

    @Override
    public String toString() {
        return "PromoResponse[" +
                "promotions=" + promotions + ']';
    }
}
