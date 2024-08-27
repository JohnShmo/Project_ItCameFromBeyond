package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;

public abstract class StageData {
    public static final String UNKNOWN_LABEL = "???";
    public static final String UNKNOWN_ICON_CATEGORY = "events";
    public static final String UNKNOWN_ICON_ID = "stage_unknown_neutral";
    public static final String DEFAULT_DESCRIPTION = "Something may occur upon reaching this level in the event...";

    public enum State {
        HIDDEN,
        INACTIVE,
        ACTIVE,
        COMPLETE
    }

    private State _state = State.HIDDEN;
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

    protected String getDescription() { return DEFAULT_DESCRIPTION; }
    protected String getTitle() { return getLabel(); }
}
