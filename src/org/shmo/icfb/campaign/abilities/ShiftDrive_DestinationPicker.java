package org.shmo.icfb.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import java.util.Map;

public class ShiftDrive_DestinationPicker implements InteractionDialogPlugin {
    public static final String ID = "icfb_ShiftDrive_DestinationPicker";

    private ShiftDrive _shiftDrive;
    private InteractionDialogAPI _dialog;
    private CampaignFleetAPI _playerFleet;

    @Override
    public void init(InteractionDialogAPI dialog) {
        setDialog(dialog);
        setPlayerFleet(Global.getSector().getPlayerFleet());
        getDialog().showCampaignEntityPicker("Select destination", "Destination:", "Initiate Shift Jump",
                Global.getSector().getPlayerFaction(),
                getShiftDrive().getValidDestinationList(getPlayerFleet()),
                new ShiftDrive_DestinationPicker_Listener(this)
        );
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

    }

    @Override
    public Object getContext() {
        return null;
    }
    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }

    public InteractionDialogAPI getDialog() { return _dialog; }
    private void setDialog(InteractionDialogAPI dialog) { _dialog = dialog; }
    public CampaignFleetAPI getPlayerFleet() { return _playerFleet; }
    private void setPlayerFleet(CampaignFleetAPI fleet) { _playerFleet = fleet; }
    public ShiftDrive getShiftDrive() { return _shiftDrive; }
    public void setShiftDrive(ShiftDrive shiftDrive) { _shiftDrive = shiftDrive; }
}
