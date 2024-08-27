package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.Global;

public class ShiftDriveEvent_MinorStage extends StageData {
    public static final String ICON_CATEGORY = "events";
    public static final String ICON_ID = "stage_unknown_bad";
    public static final String LABEL = "Odd Occurrences";

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

    @Override
    protected String getDescription() {
        if (getState() == State.HIDDEN)
            return super.getDescription();
        if (getState() == State.INACTIVE)
            return "Bizarre rumors grow more and more common as you use your Shift Drive.";
        if (getState() == State.ACTIVE)
            return "--TODO--";
        if (getState() == State.COMPLETE)
            return "--TODO--";
        return super.getDescription();
    }
}
