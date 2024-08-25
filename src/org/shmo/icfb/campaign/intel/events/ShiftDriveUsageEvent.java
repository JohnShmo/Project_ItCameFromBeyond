package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.EventFactor;

import java.util.List;

public class ShiftDriveUsageEvent extends BaseEventIntel {
    public static final String KEY = "$icfb_ShiftDriveUsageEvent";

    public static final String SHIFT_JUMP_ICON_CATEGORY = "icfb_icons";
    public static final String SHIFT_JUMP_ICON_ID = "shift_drive";

    public static final String FUEL_UPGRADE_ICON_CATEGORY = "icfb_icons";
    public static final String FUEL_UPGRADE_ICON_ID = "shift_drive";
    public static final String FUEL_UPGRADE_LABEL = "Upgrade";

    public static final String RANGE_UPGRADE_ICON_CATEGORY = "icfb_icons";
    public static final String RANGE_UPGRADE_ICON_ID = "shift_drive";
    public static final String RANGE_UPGRADE_LABEL = "Upgrade";

    public static final String MINOR_ICON_CATEGORY = "events";
    public static final String MINOR_ICON_ID = "stage_unknown_neutral";
    public static final String MINOR_LABEL = "Odd Occurrences";

    public static final String MAJOR_ICON_CATEGORY = "events";
    public static final String MAJOR_ICON_ID = "stage_unknown_neutral";
    public static final String MAJOR_LABEL = "Unwelcome Visitors";

    public static final String DEADLY_ICON_CATEGORY = "events";
    public static final String DEADLY_ICON_ID = "stage_unknown_bad";
    public static final String DEADLY_LABEL = "The Hunt";

    public static final String UNKNOWN_LABEL = "???";

    public enum Stage {
        START,
        MINOR_EVENT,
        FUEL_UPGRADE,
        MAJOR_EVENT,
        RANGE_UPGRADE,
        DEADLY_EVENT
    }

    public enum Factor {
        SHIFT_JUMP_USE,
        SHIFT_JUMP_DISTANCE
    }

    public enum StageState {
        INACTIVE,
        ACTIVE,
        COMPLETE
    }

    private static class EventData {
        public StageState state = StageState.INACTIVE;
        public String inactiveLabel;
        public String activeLabel;
        public String completeLabel;
    }

    private static class MinorEventData extends EventData {
        public MinorEventData() {
            inactiveLabel = UNKNOWN_LABEL;
            activeLabel = MINOR_LABEL;
            completeLabel = null;
        }
    }

    private static class MajorEventData extends EventData  {
        public MajorEventData() {
            inactiveLabel = UNKNOWN_LABEL;
            activeLabel = MAJOR_LABEL;
            completeLabel = null;
        }
    }

    private static class DeadlyEventData extends EventData  {
        public DeadlyEventData() {
            inactiveLabel = UNKNOWN_LABEL;
            activeLabel = DEADLY_LABEL;
            completeLabel = DEADLY_LABEL;
        }
    }

    private static class FuelUpgradeData extends EventData  {
        public FuelUpgradeData() {
            inactiveLabel = FUEL_UPGRADE_LABEL;
            activeLabel = null;
            completeLabel = null;
        }
    }

    private static class RangeUpgradeData extends EventData  {
        public RangeUpgradeData() {
            inactiveLabel = RANGE_UPGRADE_LABEL;
            activeLabel = null;
            completeLabel = null;
        }
    }

    public static final int MAX_PROGRESS = 800;
    public static final int PROGRESS_MINOR = 150;
    public static final int PROGRESS_FUEL_UPGRADE = 250;
    public static final int PROGRESS_MAJOR = 450;
    public static final int PROGRESS_RANGE_UPGRADE = 550;
    public static final int PROGRESS_DEADLY = MAX_PROGRESS;
    public static final int PROGRESS_TO_SUBTRACT_AFTER_DEADLY = 250;
    public static final int MONTHLY_DECAY = 5;
    public static final int POINT_PER_USE = 20;
    public static final int POINT_PER_LY = 5;

    private int _currentUses = 0;
    private float _currentTotalDistance = 0f;
    private int _checkPoint = 0;

    private MinorEventData _minorData;
    private MajorEventData _majorData;
    private DeadlyEventData _deadlyData;
    private FuelUpgradeData _fuelData;
    private RangeUpgradeData _rangeData;

    public static ShiftDriveUsageEvent getInstance() {
        return (ShiftDriveUsageEvent) Global.getSector().getMemoryWithoutUpdate().get(KEY);
    }

    public void reportUses(int totalUses) {
        int difference = totalUses - _currentUses;
        if (difference <= 0)
            return;
        applyFactor(Factor.SHIFT_JUMP_USE, difference);
        _currentUses = totalUses;
    }

    public void reportTotalDistance(float totalDistance) {
        float difference = totalDistance - _currentTotalDistance;
        if (difference <= 0)
            return;
        applyFactor(Factor.SHIFT_JUMP_DISTANCE, difference);
        _currentTotalDistance = totalDistance;
    }

    public ShiftDriveUsageEvent() {
        super();
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
        setup();
        Global.getSector().getIntelManager().addIntel(this);
    }

    private void applyFactor(Factor factorId, Object param) {

    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName(SHIFT_JUMP_ICON_CATEGORY, SHIFT_JUMP_ICON_ID);
    }

    @Override
    protected String getName() {
        return "Shift Drive Usage";
    }

    private void setup() {
        _minorData = new MinorEventData();
        _majorData = new MajorEventData();
        _deadlyData = new DeadlyEventData();
        _fuelData = new FuelUpgradeData();
        _rangeData = new RangeUpgradeData();

        setMaxProgress(MAX_PROGRESS);

        addStage(Stage.START, 0, true, StageIconSize.SMALL);
        addStage(Stage.MINOR_EVENT, PROGRESS_MINOR, true, StageIconSize.MEDIUM);
        addStage(Stage.FUEL_UPGRADE, PROGRESS_FUEL_UPGRADE, true, StageIconSize.SMALL);
        addStage(Stage.MAJOR_EVENT, PROGRESS_MAJOR, true, StageIconSize.MEDIUM);
        addStage(Stage.RANGE_UPGRADE, PROGRESS_RANGE_UPGRADE, true, StageIconSize.SMALL);
        addStage(Stage.DEADLY_EVENT, PROGRESS_DEADLY, false, StageIconSize.LARGE);
    }

    @Override
    protected String getStageIcon(Object stageId) {
        Stage stage = (Stage)stageId;
        SettingsAPI settings = Global.getSettings();
        switch (stage) {
            case START: return settings.getSpriteName(SHIFT_JUMP_ICON_CATEGORY, SHIFT_JUMP_ICON_ID);
            case MINOR_EVENT: return settings.getSpriteName(MINOR_ICON_CATEGORY, MINOR_ICON_ID);
            case FUEL_UPGRADE: return settings.getSpriteName(FUEL_UPGRADE_ICON_CATEGORY, FUEL_UPGRADE_ICON_ID);
            case MAJOR_EVENT: return settings.getSpriteName(MAJOR_ICON_CATEGORY, MAJOR_ICON_ID);
            case RANGE_UPGRADE: return settings.getSpriteName(RANGE_UPGRADE_ICON_CATEGORY, RANGE_UPGRADE_ICON_ID);
            case DEADLY_EVENT: return settings.getSpriteName(DEADLY_ICON_CATEGORY, DEADLY_ICON_ID);
        }
        return super.getStageIcon(stageId);
    }

    @Override
    protected String getStageLabel(Object stageId) {
        Stage stage = (Stage)stageId;
        switch (stage) {
            case START: return null;
            case MINOR_EVENT: return getMinorLabel();
            case FUEL_UPGRADE: return getFuelUpgradeLabel();
            case MAJOR_EVENT: return getMajorLabel();
            case RANGE_UPGRADE: return getRangeUpgradeLabel();
            case DEADLY_EVENT: return getDeadlyLabel();
        }
        return super.getStageLabel(stageId);
    }

    private String getEventLabel(EventData data) {
        switch (data.state) {
            case INACTIVE: return data.inactiveLabel;
            case ACTIVE:   return data.activeLabel;
            case COMPLETE: return data.completeLabel;
        }
        return null;
    }

    private String getMinorLabel() {
        return getEventLabel(_minorData);
    }

    private String getMajorLabel() {
        return getEventLabel(_majorData);
    }

    private String getDeadlyLabel() {
        return getEventLabel(_deadlyData);
    }

    private String getFuelUpgradeLabel() {
        return getEventLabel(_fuelData);
    }

    private String getRangeUpgradeLabel() {
        return getEventLabel(_rangeData);
    }
}
