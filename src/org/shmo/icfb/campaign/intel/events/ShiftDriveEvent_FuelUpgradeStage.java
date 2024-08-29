package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class ShiftDriveEvent_FuelUpgradeStage extends StageStatus {
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

    @Override
    protected int getProgress() {
        return ShiftDriveEvent.PROGRESS_FUEL_UPGRADE;
    }

    @Override
    protected void addDescriptionTextImpl(TooltipMakerAPI info, float width) {
        final Color highlight = Misc.getHighlightColor();
        final Color negative = Misc.getNegativeHighlightColor();
        final float pad = 10;

        info.addPara(
                "The fuel efficiency of your %s ability has been improved.",
                pad, highlight, "Shift Jump"
        );
    }
}
