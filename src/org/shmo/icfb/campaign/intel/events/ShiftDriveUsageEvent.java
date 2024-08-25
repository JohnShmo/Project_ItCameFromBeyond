package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;

public class ShiftDriveUsageEvent extends BaseEventIntel {
    public static final String KEY = "$icfb_ShiftDriveUsageEvent";

    public static final String ICON_CATEGORY = "icfb_icons";
    public static final String ICON_ID = "shift_drive";

    private int _currentUses = 0;
    private float _currentTotalDistance = 0f;

    public static ShiftDriveUsageEvent getInstance() {
        return (ShiftDriveUsageEvent) Global.getSector().getMemoryWithoutUpdate().get(KEY);
    }

    public void reportUses(int totalUses) {
        int difference = totalUses - _currentUses;
        if (difference <= 0)
            return;
        _currentUses = totalUses;
    }

    public void reportTotalDistance(float totalDistance) {
        float difference = totalDistance - _currentTotalDistance;
        if (difference <= 0)
            return;
        _currentTotalDistance = totalDistance;
    }

    public ShiftDriveUsageEvent() {
        super();
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
        setup();
        Global.getSector().getIntelManager().addIntel(this);
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName(ICON_CATEGORY, ICON_ID);
    }

    @Override
    protected String getName() {
        return "Shift Drive Usage";
    }

    private void setup() {

    }
}
