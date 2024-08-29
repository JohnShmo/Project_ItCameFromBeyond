package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public abstract class StageStatus {
    public static final String UNKNOWN_LABEL = "???";
    public static final String UNKNOWN_ICON_CATEGORY = "events";
    public static final String UNKNOWN_ICON_ID = "stage_unknown_neutral";
    public static final String DEFAULT_TOOLTIP = "Something may occur upon reaching this level in the event...";

    public enum State {
        HIDDEN,
        INACTIVE,
        ACTIVE,
        COMPLETE
    }

    private State _state = State.INACTIVE;
    public State getState() { return _state; }
    public void setState(State state) { _state = state; }

    public String getLabel() {
        switch (_state) {
            case HIDDEN: return getHiddenLabel();
            case INACTIVE: return getInactiveLabel();
            case ACTIVE: return getActiveLabel();
            case COMPLETE: return getCompleteLabel();
        }
        return null;
    }
    protected String getHiddenLabel() { return UNKNOWN_LABEL; }
    protected abstract String getInactiveLabel();
    protected abstract String getActiveLabel();
    protected abstract String getCompleteLabel();

    public String getIcon() {
        switch (_state) {
            case HIDDEN: return getHiddenIcon();
            case INACTIVE: return getInactiveIcon();
            case ACTIVE: return getActiveIcon();
            case COMPLETE: return getCompleteIcon();
        }
        return null;
    }
    protected String getHiddenIcon() { return Global.getSettings().getSpriteName(UNKNOWN_ICON_CATEGORY, UNKNOWN_ICON_ID); }
    protected abstract String getInactiveIcon();
    protected abstract String getActiveIcon();
    protected abstract String getCompleteIcon();

    protected String getTooltipText() { return DEFAULT_TOOLTIP; }
    protected void addDescriptionTextImpl(TooltipMakerAPI info, float width) { }
    protected String getTitle() { return getLabel(); }
    protected abstract int getProgress();

    public TooltipMakerAPI.TooltipCreator getStageTooltip() {
        return new BaseFactorTooltip() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addTitle(getTitle());
                tooltip.addPara(getTooltipText(), 10);
            }
        };
    }

    public void addDescriptionText(TooltipMakerAPI info, float width) {
        if (getState() == State.INACTIVE || getState() == State.HIDDEN)
            return;
        info.addTitle(getTitle());
        addDescriptionTextImpl(info, width);
    }

}
