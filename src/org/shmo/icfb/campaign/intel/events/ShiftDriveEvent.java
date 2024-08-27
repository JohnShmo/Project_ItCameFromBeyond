package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.impl.campaign.intel.events.EventFactor;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.campaign.abilities.ShiftJump;

public class ShiftDriveEvent extends BaseEventIntel {
    public static final String KEY = "$icfb_ShiftDriveUsageEvent";
    public static final String SHIFT_JUMP_ICON_CATEGORY = "icfb_icons";
    public static final String SHIFT_JUMP_ICON_ID = "shift_drive";

    public enum Stage {
        START,
        MINOR_EVENT,
        FUEL_UPGRADE,
        MAJOR_EVENT,
        RANGE_UPGRADE,
        DEADLY_EVENT
    }

    public enum Factor {
        SHIFT_JUMP_USE
    }

    public static final int MAX_PROGRESS = 800;
    public static final int PROGRESS_MINOR = 50;
    public static final int PROGRESS_FUEL_UPGRADE = 250;
    public static final int PROGRESS_MAJOR = 450;
    public static final int PROGRESS_RANGE_UPGRADE = 550;
    public static final int PROGRESS_DEADLY = MAX_PROGRESS;
    public static final int PROGRESS_TO_SUBTRACT_AFTER_DEADLY = 250;

    private int _checkPoint = 0;
    private int _currentMaximum = PROGRESS_MINOR;
    private boolean _lockedInStage = false;

    private ShiftDriveEvent_MinorStage _minorData;
    private ShiftDriveEvent_MajorStage _majorData;
    private ShiftDriveEvent_DeadlyStage _deadlyData;
    private ShiftDriveEvent_FuelUpgradeStage _fuelData;
    private ShiftDriveEvent_RangeUpgradeStage _rangeData;

    public static ShiftDriveEvent getInstance() {
        return (ShiftDriveEvent) Global.getSector().getMemoryWithoutUpdate().get(KEY);
    }

    public ShiftDriveEvent() {
        super();
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
        setup();
        Global.getSector().getIntelManager().addIntel(this);
    }

    public static void addFactorCreateIfNecessary(Factor factorId, float param, InteractionDialogAPI dialog) {
        ShiftDriveEvent instance = getInstance();
        if (instance == null) {
            instance = new ShiftDriveEvent();
        }
        instance.addFactor(factorId, param, dialog);
    }

    public static void addFactorCreateIfNecessary(Factor factorId, float param) {
        addFactorCreateIfNecessary(factorId, param, null);
    }

    private transient Factor _lastFactorId = null;
    private transient EventFactor _lastFactor = null;
    public void addFactor(Factor factorId, float param, InteractionDialogAPI dialog) {
        if (_lockedInStage)
            return;

        _lastFactorId = factorId;
        if (factorId == Factor.SHIFT_JUMP_USE) {
            _lastFactor = new ShiftDriveEvent_UseFactor(param);
            addFactor(_lastFactor, dialog);
        }
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName(SHIFT_JUMP_ICON_CATEGORY, SHIFT_JUMP_ICON_ID);
    }

    @Override
    protected String getName() {
        return "Shift Drive";
    }

    private void setup() {
        _minorData = new ShiftDriveEvent_MinorStage();
        _majorData = new ShiftDriveEvent_MajorStage();
        _deadlyData = new ShiftDriveEvent_DeadlyStage();
        _fuelData = new ShiftDriveEvent_FuelUpgradeStage();
        _rangeData = new ShiftDriveEvent_RangeUpgradeStage();

        setMaxProgress(MAX_PROGRESS);

        addStage(Stage.START, 0, true, StageIconSize.SMALL);
        addStage(Stage.MINOR_EVENT, PROGRESS_MINOR, true, StageIconSize.MEDIUM);
        addStage(Stage.FUEL_UPGRADE, PROGRESS_FUEL_UPGRADE, true, StageIconSize.SMALL);
        addStage(Stage.MAJOR_EVENT, PROGRESS_MAJOR, true, StageIconSize.MEDIUM);
        addStage(Stage.RANGE_UPGRADE, PROGRESS_RANGE_UPGRADE, true, StageIconSize.SMALL);
        addStage(Stage.DEADLY_EVENT, PROGRESS_DEADLY, false, StageIconSize.LARGE);

        _minorData.setState(StageData.State.INACTIVE);
        _fuelData.setState(StageData.State.INACTIVE);
        _rangeData.setState(StageData.State.INACTIVE);

        addFactor(new ShiftDriveEvent_DecayFactor());
    }

    @Override
    protected String getStageIcon(Object stageId) {
        Stage stage = (Stage)stageId;
        SettingsAPI settings = Global.getSettings();
        switch (stage) {
            case START: return settings.getSpriteName(SHIFT_JUMP_ICON_CATEGORY, SHIFT_JUMP_ICON_ID);
            case MINOR_EVENT: return _minorData.getIcon();
            case FUEL_UPGRADE: return _fuelData.getIcon();
            case MAJOR_EVENT: return _majorData.getIcon();
            case RANGE_UPGRADE: return _rangeData.getIcon();
            case DEADLY_EVENT: return _deadlyData.getIcon();
        }
        return super.getStageIcon(stageId);
    }

    @Override
    protected String getStageLabel(Object stageId) {
        Stage stage = (Stage)stageId;
        switch (stage) {
            case START: return null;
            case MINOR_EVENT: return _minorData.getLabel();
            case FUEL_UPGRADE: return _fuelData.getLabel();
            case MAJOR_EVENT: return _majorData.getLabel();
            case RANGE_UPGRADE: return _rangeData.getLabel();
            case DEADLY_EVENT: return _deadlyData.getLabel();
        }
        return super.getStageLabel(stageId);
    }

    @Override
    public TooltipMakerAPI.TooltipCreator getStageTooltip(Object stageId) {
        Stage stage = (Stage)stageId;
        switch (stage) {
            case MINOR_EVENT:
                return new BaseFactorTooltip() {
                    @Override
                    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                        tooltip.addTitle(_minorData.getTitle());
                        tooltip.addPara(_minorData.getDescription(), 10);
                    }
                };
            case FUEL_UPGRADE:
                return new BaseFactorTooltip() {
                    @Override
                    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                        tooltip.addTitle(_fuelData.getTitle());
                        tooltip.addPara(_fuelData.getDescription(), 10);
                    }
                };
            case MAJOR_EVENT:
                return new BaseFactorTooltip() {
                    @Override
                    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                        tooltip.addTitle(_majorData.getTitle());
                        tooltip.addPara(_majorData.getDescription(), 10);
                    }
                };
            case RANGE_UPGRADE:
                return new BaseFactorTooltip() {
                    @Override
                    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                        tooltip.addTitle(_rangeData.getTitle());
                        tooltip.addPara(_rangeData.getDescription(), 10);
                    }
                };
            case DEADLY_EVENT:
                return new BaseFactorTooltip() {
                    @Override
                    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                        tooltip.addTitle(_deadlyData.getTitle());
                        tooltip.addPara(_deadlyData.getDescription(), 10);
                    }
                };
        }
        return null;
    }

    @Override
    public void addFactor(EventFactor factor, InteractionDialogAPI dialog) {
        if (_lockedInStage)
            return;
        super.addFactor(factor, dialog);
    }

    @Override
    public void setProgress(int progress) {
        if (_lockedInStage)
            return;

        if (progress < _checkPoint)
            progress = _checkPoint;
        if (progress > _currentMaximum)
            progress = _currentMaximum;

        if (this.progress == progress) return;

        if (progress < 0) progress = 0;
        if (progress > maxProgress) progress = maxProgress;

        EventStageData prev = getLastActiveStage(true);
        prevProgressDeltaWasPositive = this.progress < progress;

        if (progress < 0) {
            progress = 0;
        }

        if (progress > getMaxProgress()) {
            progress = getMaxProgress();
        }

        this.progress = progress;

        for (EventStageData curr : getStages()) {
            if (curr.progress <= prev.progress && !prev.wasEverReached &&
                    (prev.rollData == null || prev.rollData.equals(RANDOM_EVENT_NONE))) continue;

            if (curr.progress <= progress) {
                boolean laterThanPrev = ((Enum)prev.id).ordinal() < ((Enum)curr.id).ordinal();
                if (laterThanPrev || !prev.wasEverReached) {
                    notifyStageReached(curr);
                    if (curr.sendIntelUpdateOnReaching && curr.progress > 0 && prev.progress < curr.progress) {
                        sendUpdateIfPlayerHasIntel(curr, getTextPanelForStageChange());
                    }
                    curr.rollData = null;
                    curr.wasEverReached = true;

                    progress = getProgress();
                }
            }
        }
    }

    private void createFactorIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        info.setParaFontDefault();
        info.setParaFontColor(getTitleColor(mode));
        info.addPara(getName() + ": %s",0, Misc.getHighlightColor(), _lastFactor.getProgressStr(this));
        info.setParaFontColor(Misc.getTextColor());
        if (_lastFactorId == Factor.SHIFT_JUMP_USE) {
            ShiftDriveEvent_UseFactor factor = (ShiftDriveEvent_UseFactor)_lastFactor;
            ShiftJump shiftJump = ItCameFromBeyond.Global.getPlayerShiftJump();
            int fuelCost = 0;
            int crPercent = 0;
            if (shiftJump != null) {
                fuelCost = shiftJump.computeFuelCost(Global.getSector().getPlayerFleet(), factor.getDistance());
                crPercent = (int)(shiftJump.computeCRCost(factor.getDistance()) * 100f);
            }
            info.addPara("    - Distance: %s light years", 0f, Misc.getHighlightColor(),
                    String.valueOf((int)factor.getDistance())
            );
            info.addPara("    - Fuel used: %s", 0f, Misc.getHighlightColor(),
                    String.valueOf(fuelCost)
            );
            info.addPara("    - CR penalty: %s", 0f, Misc.getNegativeHighlightColor(),
                    crPercent + "%"
            );
        }
        _lastFactor = null;
        _lastFactorId = null;
    }

    private void createStageIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        info.setParaFontDefault();
        info.setParaFontColor(getTitleColor(mode));
        info.addPara(getName(), 0);
        info.setParaFontColor(Misc.getTextColor());
        info.addPara("    - Stage Reached: " + getStageLabel(_lastStageId),0);
        _lastStageId = null;
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        if (_lastFactor != null && _lastFactorId != null) {
            createFactorIntelInfo(info, mode);
        } else if (_lastStageId != null) {
            createStageIntelInfo(info, mode);
        } else {
            super.createIntelInfo(info, mode);
        }
    }

    private transient Stage _lastStageId = null;
    @Override
    protected void notifyStageReached(EventStageData stage) {
        _lastStageId = (Stage)stage.id;
    }
}
