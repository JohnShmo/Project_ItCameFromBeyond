package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class ShiftDriveEventDecayFactor extends BaseEventFactor {
    public static final int MONTHLY_DECAY = 5;

    public int getProgress(BaseEventIntel intel) {
        ShiftDriveEvent shiftDriveEvent = (ShiftDriveEvent)intel;
        return shiftDriveEvent.isLockedInStage() ? 0 : -MONTHLY_DECAY;
    }

    @Override
    public boolean shouldShow(BaseEventIntel intel) {
        ShiftDriveEvent shiftDriveEvent = (ShiftDriveEvent)intel;
        return !shiftDriveEvent.isLockedInStage();
    }

    @Override
    public boolean isOneTime() {
        return false;
    }

    @Override
    public String getDesc(BaseEventIntel intel) {
        return "Monthly decay";
    }

    protected String getBulletPointText(BaseEventIntel intel) {
        return null;
    }

    public void addBulletPointForOneTimeFactor(BaseEventIntel intel, TooltipMakerAPI info, Color tc, float initPad) {
        String text = getBulletPointText(intel);
        if (text == null) text = getDesc(intel);
        if (text != null) {
            info.addPara(text + ": %s", initPad, tc, getProgressColor(intel),
                    getProgressStr(intel));
        }
    }

    public TooltipMakerAPI.TooltipCreator getMainRowTooltip(final BaseEventIntel intel) {
        return new BaseFactorTooltip() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                final float pad = 10;
                final Color highlight = Misc.getHighlightColor();
                tooltip.addTitle("Monthly Decay");
                tooltip.addPara("Each month, the usage of your %s becomes less of a factor in this event.",
                        pad, highlight,
                        "Shift Drive"
                );
            }
        };
    }
}
