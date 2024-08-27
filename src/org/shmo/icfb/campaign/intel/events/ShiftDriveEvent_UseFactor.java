package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class ShiftDriveEvent_UseFactor extends BaseEventFactor {
    public static final int POINTS_PER_LY = 2;
    public static final int POINTS_PER_USE = 10;
    public static final int SHOW_DURATION_DAYS = 30;

    private float _distanceLY = 0;
    private final long _timeStamp;

    public ShiftDriveEvent_UseFactor(float distanceLY) {
        addDistance(distanceLY);
        _timeStamp = Global.getSector().getClock().getTimestamp();
    }

    @Override
    public boolean shouldShow(BaseEventIntel intel) {
        return true;
    }

    public void addDistance(float distanceLY) {
        _distanceLY += distanceLY;
    }

    public void setDistance(float distanceLY) {
        _distanceLY = distanceLY;
    }

    public float getDistance() {
        return _distanceLY;
    }

    public int getProgress(BaseEventIntel intel) {
        return (int)(_distanceLY * POINTS_PER_LY) + POINTS_PER_USE;
    }

    public String getDesc(BaseEventIntel intel) {
        return "Shift Jump use";
    }

    public boolean isOneTime() {
        return true;
    }

    public boolean isExpired() {
        return Global.getSector().getClock().getElapsedDaysSince(_timeStamp) > SHOW_DURATION_DAYS;
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
                tooltip.addTitle("Shift Jump Use");
                String str1 = "The recent use of your %s increased the progress of this event by %s points.";
                String str2 = "The %s light year"+  ((int)_distanceLY >= 2 ? "s" : "") + " traveled with your %s " +
                        " increased the progress of this event by %s points.";
                tooltip.addPara(str1,
                        pad, highlight,
                        "Shift Jump",
                        String.valueOf(POINTS_PER_USE)
                );
                tooltip.addPara(str2,
                        pad, highlight,
                        String.valueOf((int)_distanceLY),
                        "Shift Jump",
                        String.valueOf((int)(_distanceLY * POINTS_PER_LY))
                );
            }
        };
    }
}
