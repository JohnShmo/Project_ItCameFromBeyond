package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.IcfbGlobal;

import java.awt.*;

public class ShiftDriveEventUseFactor extends BaseEventFactor {
    public static final int SHOW_DURATION_DAYS = 30;

    private float _distanceLY = 0;
    private final long _timeStamp;
    private int _progress;

    public ShiftDriveEventUseFactor(float distanceLY) {
        addDistance(distanceLY);
        _timeStamp = Global.getSector().getClock().getTimestamp();
        _progress = IcfbGlobal.getSettings().incursions.pointsPerShiftJumpUse.get();
    }

    @Override
    public boolean shouldShow(BaseEventIntel intel) {
        return true;
    }

    public void addDistance(float distanceLY) {
        setDistance(getDistance() + distanceLY);
    }

    public void setDistance(float distanceLY) {
        _distanceLY = distanceLY;
    }

    public float getDistance() {
        return _distanceLY;
    }

    public int getProgress(BaseEventIntel intel) {
        return _progress;
    }

    public void setProgress(int progress) {
        _progress = progress;
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

    public TooltipMakerAPI.TooltipCreator getMainRowTooltip(final BaseEventIntel intel) {
        return new BaseFactorTooltip() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                final float pad = 10;
                final Color highlight = Misc.getHighlightColor();
                tooltip.addTitle("Shift Jump Use");
                String str1 = "The recent use of your %s increased the progress of this event by %s points.";
                String str2 = "The %s light year"+  ((int)getDistance() >= 2 ? "s" : "") + " traveled with your %s" +
                        " increased the progress of this event by %s points.";
                tooltip.addPara(str1,
                        pad, highlight,
                        "Shift Jump",
                        String.valueOf(IcfbGlobal.getSettings().incursions.pointsPerShiftJumpUse)
                );
                tooltip.addPara(str2,
                        pad, highlight,
                        String.valueOf((int)getDistance()),
                        "Shift Jump"
                );
            }
        };
    }
}
