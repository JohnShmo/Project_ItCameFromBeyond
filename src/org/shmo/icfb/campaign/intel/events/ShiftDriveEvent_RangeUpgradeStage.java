package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.Global;

public class ShiftDriveEvent_RangeUpgradeStage extends StageData {
    public static final String ICON_CATEGORY = "icfb_icons";
    public static final String ICON_ID = "shift_drive";
    public static final String LABEL = "Upgrade";

    @Override
    protected String getInactiveLabel() {
        return LABEL;
    }

    @Override
    protected String getActiveLabel() {
        return LABEL;
    }

    @Override
    protected String getCompleteLabel() {
        return LABEL;
    }

    @Override
    protected String getInactiveIcon() {
        return Global.getSettings().getSpriteName(ICON_CATEGORY, ICON_ID);
    }

    @Override
    protected String getActiveIcon() {
        return Global.getSettings().getSpriteName(ICON_CATEGORY, ICON_ID);
    }

    @Override
    protected String getCompleteIcon() {
        return Global.getSettings().getSpriteName(ICON_CATEGORY, ICON_ID);
    }
}
