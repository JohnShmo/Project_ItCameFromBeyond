package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class ShiftDriveEvent_MinorStage extends StageStatus {
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
    protected String getTooltipText() {
        if (getState() == State.HIDDEN)
            return super.getTooltipText();
        if (getState() == State.INACTIVE)
            return "Bizarre rumors grow more and more common as you use your Shift Drive.";
        if (getState() == State.ACTIVE)
            return "--TODO--";
        if (getState() == State.COMPLETE)
            return "--TODO--";
        return super.getTooltipText();
    }

    @Override
    protected int getProgress() {
        return ShiftDriveEvent.PROGRESS_MINOR;
    }

    @Override
    protected void addDescriptionTextImpl(TooltipMakerAPI info, float width) {
        final Color highlight = Misc.getHighlightColor();
        final Color negative = Misc.getNegativeHighlightColor();
        final float pad = 10;

        if (getState() == State.ACTIVE) {
            info.addPara("Progress is locked due to reaching this stage in the event. " +
                            "Resolve the associated affair to continue.",
                    pad
            );
        } else if (getState() == State.COMPLETE) {
            info.addPara(
                    "Fleets of unknown origin started to appear after resolving this stage. " +
                            "Their activity seems to increase according to the progress level of the event.",
                    pad
            );
        }
    }
}
