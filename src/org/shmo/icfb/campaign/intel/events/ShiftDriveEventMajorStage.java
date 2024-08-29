package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class ShiftDriveEventMajorStage extends StageStatus {
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

    @Override
    protected int getProgress() {
        return ShiftDriveEvent.PROGRESS_MAJOR;
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
                    "Roaming %s fleets became more active and aggressive after resolving this stage.",
                    pad, negative, "Shifter"
            );
        }
    }
}
