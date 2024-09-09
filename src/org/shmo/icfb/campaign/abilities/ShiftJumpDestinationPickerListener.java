package org.shmo.icfb.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEntityPickerListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class ShiftJumpDestinationPickerListener extends BaseCampaignEntityPickerListener {

    private transient InteractionDialogAPI _dialog;
    private transient ShiftJump _shiftJump;
    private transient CampaignFleetAPI _playerFleet;

    public ShiftJumpDestinationPickerListener(InteractionDialogAPI dialog, ShiftJump shiftJump) {
        _shiftJump = shiftJump;
        _dialog = dialog;
        _playerFleet = Global.getSector().getPlayerFleet();
    }

    @Override
    public String getMenuItemNameOverrideFor(SectorEntityToken entity) {
        return null;
    }

    @Override
    public void pickedEntity(SectorEntityToken entity) {
        _shiftJump.setTarget(entity);
        _dialog.dismiss();
        unsetFields();
        Global.getSector().setPaused(false);
    }

    @Override
    public void cancelledEntityPicking() {
        _dialog.dismiss();
        unsetFields();
        Global.getSector().setPaused(false);
    }

    @Override
    public String getSelectedTextOverrideFor(SectorEntityToken entity) {
        return entity.getName() + " - " + entity.getContainingLocation().getNameWithTypeShort();
    }

    @Override
    public void createInfoText(TooltipMakerAPI info, SectorEntityToken entity) {
        final int cost = _shiftJump.computeFuelCost(_playerFleet, entity);
        final int crPenalty = (int)(_shiftJump.computeCRCost(_playerFleet, entity) * 100f);
        final int available = (int) _playerFleet.getCargo().getFuel();
        final int maxRange = _shiftJump.getMaxRangeLY();
        final int distance = (int)Misc.getDistanceLY(_playerFleet, entity);

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
        info.addGrid(0);
    }

    @Override
    public boolean canConfirmSelection(SectorEntityToken entity) {
        if (_shiftJump == null || _playerFleet == null)
            return false;

        int cost = _shiftJump.computeFuelCost(_playerFleet, entity);
        int available = (int) _playerFleet.getCargo().getFuel();
        return cost <= available;
    }

    @Override
    public float getFuelColorAlphaMult() {
        return 0.5f;
    }

    // Prevents memory leak
    private void unsetFields() {
        _dialog = null;
        _shiftJump = null;
        _playerFleet = null;
    }
}
