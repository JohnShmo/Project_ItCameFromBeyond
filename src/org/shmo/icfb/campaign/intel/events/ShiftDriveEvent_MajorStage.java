package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.Global;

public class ShiftDriveEvent_MajorStage extends StageData {
    public static final String ICON_CATEGORY = "events";
    public static final String ICON_ID = "stage_unknown_bad";
    public static final String LABEL = "Unwanted Company";

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
        return null;
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
