package org.shmo.icfb.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEntityPickerListener;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class ShiftJump_DestinationPicker_Listener extends BaseCampaignEntityPickerListener {

    private final ShiftJump_DestinationPicker _picker;

    public ShiftJump_DestinationPicker_Listener(ShiftJump_DestinationPicker picker) {
        _picker = picker;
    }

    @Override
    public String getMenuItemNameOverrideFor(SectorEntityToken entity) {
        return null;
    }

    @Override
    public void pickedEntity(SectorEntityToken entity) {
        _picker.getShiftJump().setTarget(entity);
        _picker.getDialog().dismiss();
        Global.getSector().setPaused(false);
    }

    @Override
    public void cancelledEntityPicking() {
        _picker.getDialog().dismiss();
        Global.getSector().setPaused(false);
    }

    @Override
    public String getSelectedTextOverrideFor(SectorEntityToken entity) {
        return entity.getName() + " - " + entity.getContainingLocation().getNameWithTypeShort();
    }

    @Override
    public void createInfoText(TooltipMakerAPI info, SectorEntityToken entity) {
        final int cost = _picker.getShiftJump().computeFuelCost(_picker.getPlayerFleet(), entity);
        final int crPenalty = (int)(_picker.getShiftJump().computeCRCost(_picker.getPlayerFleet(), entity) * 100f);
        final int available = (int) _picker.getPlayerFleet().getCargo().getFuel();
        final int maxRange = _picker.getShiftJump().getMaxRangeLY();
        final int distance = (int)Misc.getDistanceLY(_picker.getPlayerFleet(), entity);

        Color reqColor = Misc.getHighlightColor();
        Color availableColor = Misc.getHighlightColor();
        if (cost > available) {
            reqColor = Misc.getNegativeHighlightColor();
        }

        info.setParaSmallInsignia();

        info.beginGrid(200f, 3, Misc.getGrayColor());
        info.setGridFontSmallInsignia();
        info.addToGrid(0, 0,"    Maximum range (LY):", String.valueOf(maxRange), availableColor);
        info.addToGrid(1, 0,"    Distance (LY):", String.valueOf(distance), availableColor);
        info.addToGrid(2, 0, "    CR penalty:", crPenalty + "%", Misc.getNegativeHighlightColor());
        info.addGrid(0);

        info.beginGrid(200f, 2, Misc.getGrayColor());
        info.setGridFontSmallInsignia();
        info.addToGrid(0, 0, "    Fuel required:", Misc.getWithDGS(cost), reqColor);
        info.addToGrid(1, 0, "    Fuel available:", Misc.getWithDGS(available), availableColor);
        info.addGrid(0);;
    }

    @Override
    public boolean canConfirmSelection(SectorEntityToken entity) {
        int cost = _picker.getShiftJump().computeFuelCost(_picker.getPlayerFleet(), entity);
        int available = (int) _picker.getPlayerFleet().getCargo().getFuel();
        return cost <= available;
    }

    @Override
    public float getFuelColorAlphaMult() {
        return 0.5f;
    }
}
