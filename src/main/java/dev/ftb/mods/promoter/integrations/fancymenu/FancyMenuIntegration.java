package dev.ftb.mods.promoter.integrations.fancymenu;

import de.keksuccino.fancymenu.customization.requirement.RequirementRegistry;

public class FancyMenuIntegration {
    public static void init() {
        RequirementRegistry.register(new PromoAvailableLoadingReq());
    }
}
