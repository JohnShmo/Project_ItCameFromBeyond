package org.shmo.icfb.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import java.util.Map;

public class ShiftJumpDestinationPicker implements InteractionDialogPlugin {
    private transient ShiftJump _shiftJump;
    private transient InteractionDialogAPI _dialog;

    public static void execute(ShiftJump shiftJump) {
        CampaignUIAPI ui = Global.getSector().getCampaignUI();
        ShiftJumpDestinationPicker picker = new ShiftJumpDestinationPicker();
        picker.setShiftJump(shiftJump);
        ui.showInteractionDialog(picker, null);
    }

    @Override
    public void init(InteractionDialogAPI dialog) {
        setDialog(dialog);
        getDialog().showCampaignEntityPicker("Select destination", "Destination:", "Initiate Shift Jump",
                Global.getSector().getPlayerFaction(),
                getShiftJump().getValidDestinationList(Global.getSector().getPlayerFleet()),
                new ShiftJumpDestinationPickerListener(getDialog(), getShiftJump())
        );
        unsetFields();
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {

    }

    @Override
    public void optionMousedOver(String optionText, Object optionData) {

    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void backFromEngagement(EngagementResultAPI battleResult) {
        // boy I sure hope we don't end up here somehow...
    }

    @Override
    public Object getContext() {
        return null;
    }
    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }

    private InteractionDialogAPI getDialog() { return _dialog; }
    private void setDialog(InteractionDialogAPI dialog) { _dialog = dialog; }
    private ShiftJump getShiftJump() { return _shiftJump; }
    private void setShiftJump(ShiftJump shiftJump) { _shiftJump = shiftJump; }

    // Prevents memory leak
    private void unsetFields() {
        setShiftJump(null);
        setDialog(null);
    }
}
