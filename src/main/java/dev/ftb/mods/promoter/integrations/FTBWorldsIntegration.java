//package dev.ftb.mods.promoter.integrations;
//
//import dev.ftb.mods.promoter.api.PromoData;
//import net.minecraft.client.gui.components.AbstractSelectionList;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.client.input.MouseButtonEvent;
//import net.minecraft.client.input.MouseButtonInfo;
//import net.rocketplatform.game.client.mod.ui.FTBWorldsButton;
//import net.rocketplatform.game.client.mod.ui.PromoServerEntry;
//
//public class FTBWorldsIntegration implements Integration {
//    @Override
//    public <E extends AbstractSelectionList.Entry<E>> boolean filterServerListEntry(E entry) {
//        return entry instanceof PromoServerEntry;
//    }
//
//    @Override
//    public boolean clickAction(PromoData data, Screen parent) {
//        if (!data.uuid().equals(Integrations.FTB_WORLD_MOD_UUID)) {
//            return false;
//        }
//
//        var fakeButtonHolder = FTBWorldsButton.createVanilla(0, 0, 0, 0);
//        if (fakeButtonHolder == null) {
//            return false;
//        }
//
//        // Todo: validate.
//        fakeButtonHolder.onPress(new MouseButtonEvent(0, 0, new MouseButtonInfo(0, 0)));
//        return true;
//    }
//}
