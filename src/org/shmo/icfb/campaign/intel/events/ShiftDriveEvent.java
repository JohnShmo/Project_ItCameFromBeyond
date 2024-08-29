package org.shmo.icfb.campaign.intel.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.EventFactor;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.campaign.abilities.ShiftJump;
import org.shmo.icfb.campaign.scripts.ShiftDriveManager;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    public static final int PROGRESS_MINOR = 150;
    public static final int PROGRESS_FUEL_UPGRADE = 250;
    public static final int PROGRESS_MAJOR = 450;
    public static final int PROGRESS_RANGE_UPGRADE = 550;
    public static final int PROGRESS_DEADLY = MAX_PROGRESS;
    public static final int PROGRESS_TO_SUBTRACT_AFTER_DEADLY = 250;

    private int _checkPoint = 0;
    private int _currentMaximum = PROGRESS_MINOR;
    private boolean _lockedInStage = false;

    private Map<Stage, StageStatus> _statusMap;

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

    private static class LastFactorInfo {
        public Factor id = null;
        public EventFactor factor = null;
        public LastFactorInfo(Factor id, EventFactor factor) {
            this.id = id;
            this.factor = factor;
        }
    }
    private transient LastFactorInfo _lastFactorInfo = null;
    private void setLastFactorInfo(Factor lastFactorId, EventFactor factor) {
        _lastFactorInfo = new LastFactorInfo(lastFactorId, factor);
    }
    private void unsetLastFactorInfo() {
        _lastFactorInfo = null;
    }
    private LastFactorInfo getLastFactorInfo() {
        return _lastFactorInfo;
    }
    public void addFactor(Factor factorId, float param, InteractionDialogAPI dialog) {
        if (factorId == Factor.SHIFT_JUMP_USE) {
            ShiftDriveEvent_UseFactor factor = new ShiftDriveEvent_UseFactor(param);
            setLastFactorInfo(factorId, factor);
            if (_lockedInStage) {
                factor.setProgress(0);
                sendUpdateIfPlayerHasIntel(getListInfoParam(), null);
            }
            else {
                addFactor(getLastFactorInfo().factor, dialog);
            }
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
        StageStatus minor = new ShiftDriveEvent_MinorStage();
        StageStatus major = new ShiftDriveEvent_MajorStage();
        StageStatus deadly = new ShiftDriveEvent_DeadlyStage();
        StageStatus fuel = new ShiftDriveEvent_FuelUpgradeStage();
        StageStatus range = new ShiftDriveEvent_RangeUpgradeStage();
        _statusMap = new HashMap<>();
        _statusMap.put(Stage.MINOR_EVENT, minor);
        _statusMap.put(Stage.MAJOR_EVENT, major);
        _statusMap.put(Stage.DEADLY_EVENT, deadly);
        _statusMap.put(Stage.FUEL_UPGRADE, fuel);
        _statusMap.put(Stage.RANGE_UPGRADE, range);

        setMaxProgress(MAX_PROGRESS);

        addStage(Stage.START, 0, true, StageIconSize.SMALL);
        addStage(Stage.MINOR_EVENT, PROGRESS_MINOR, true, StageIconSize.MEDIUM);
        addStage(Stage.FUEL_UPGRADE, PROGRESS_FUEL_UPGRADE, true, StageIconSize.LARGE);
        addStage(Stage.MAJOR_EVENT, PROGRESS_MAJOR, true, StageIconSize.MEDIUM);
        addStage(Stage.RANGE_UPGRADE, PROGRESS_RANGE_UPGRADE, true, StageIconSize.LARGE);
        addStage(Stage.DEADLY_EVENT, PROGRESS_DEADLY, false, StageIconSize.LARGE);

        addFactor(new ShiftDriveEvent_DecayFactor());
    }

    @Override
    protected String getStageIcon(Object stageId) {
        Stage stage = (Stage)stageId;
        SettingsAPI settings = Global.getSettings();
        if (stage == Stage.START) {
            return settings.getSpriteName(SHIFT_JUMP_ICON_CATEGORY, SHIFT_JUMP_ICON_ID);
        }
        return getStageStatus(stage).getIcon();
    }

    @Override
    protected String getStageLabel(Object stageId) {
        Stage stage = (Stage)stageId;
        if (stage == Stage.START) {
            return null;
        }
        return getStageStatus(stage).getLabel();
    }

    private StageStatus getStageStatus(Stage id) {
        return _statusMap.get(id);
    }

    @Override
    protected void addBulletPoints(TooltipMakerAPI info, ListInfoMode mode, boolean isUpdate, Color tc, float initPad) {

    }

    @Override
    public TooltipMakerAPI.TooltipCreator getStageTooltip(Object stageId) {
        Stage stage = (Stage)stageId;
        if (stage == Stage.START)
            return null;
        return getStageStatus(stage).getStageTooltip();
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
        EventFactor factor = getLastFactorInfo().factor;
        Factor factorId = getLastFactorInfo().id;

        info.setParaFontDefault();
        info.setParaFontColor(getTitleColor(mode));
        info.addPara(getName() + (factor.getProgress(this) > 0 ? ": %s" : ""),
                0, Misc.getHighlightColor(), factor.getProgressStr(this));
        info.setParaFontColor(Misc.getTextColor());
        if (factorId == Factor.SHIFT_JUMP_USE) {
            ShiftDriveEvent_UseFactor useFactor = (ShiftDriveEvent_UseFactor)factor;
            ShiftJump shiftJump = ItCameFromBeyond.Global.getPlayerShiftJump();
            int fuelCost = 0;
            int crPercent = 0;
            if (shiftJump != null) {
                fuelCost = shiftJump.computeFuelCost(Global.getSector().getPlayerFleet(), useFactor.getDistance());
                crPercent = (int)(shiftJump.computeCRCost(useFactor.getDistance()) * 100f);
            }
            info.addPara("    - Distance: %s light years", 0f, Misc.getHighlightColor(),
                    String.valueOf((int)useFactor.getDistance())
            );
            info.addPara("    - Fuel used: %s", 0f, Misc.getHighlightColor(),
                    String.valueOf(fuelCost)
            );
            info.addPara("    - CR penalty: %s", 0f, Misc.getNegativeHighlightColor(),
                    crPercent + "%"
            );
        }
        unsetLastFactorInfo();
    }

    private void createStageIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        info.setParaFontDefault();
        info.setParaFontColor(getTitleColor(mode));
        info.addPara(getName(), 0);
        info.setParaFontColor(Misc.getTextColor());
        info.addPara("    - Stage Reached: " + getStageLabel(getLastStageInfo().id),0);
        unsetLastStageInfo();
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        if (getLastFactorInfo() != null) {
            createFactorIntelInfo(info, mode);
        } else if (getLastStageInfo() != null) {
            createStageIntelInfo(info, mode);
        } else {
            super.createIntelInfo(info, mode);
        }
    }

    @Override
    public void addStageDescriptionText(TooltipMakerAPI info, float width, Object stageId) {
        Stage stage = (Stage)stageId;
        StageStatus stageStatus = getStageStatus(stage);
        if (stageStatus == null)
            return;
        stageStatus.addDescriptionText(info, width);
    }

    private void lockStage() {
        _lockedInStage = true;
    }

    private void unlockStage() {
        _lockedInStage = false;
    }

    public boolean isLockedInStage() {
        return _lockedInStage;
    }

    private static class LastStageInfo {
        public Stage id;
        public LastStageInfo(Stage id) {
            this.id = id;
        }
    }
    private transient LastStageInfo _lastStageInfo = null;
    private void setLastStageInfo(Stage lastStageId) {
        _lastStageInfo = new LastStageInfo(lastStageId);
    }
    private void unsetLastStageInfo() {
        _lastStageInfo = null;
    }
    private LastStageInfo getLastStageInfo() {
        return _lastStageInfo;
    }

    @Override
    protected void notifyStageReached(EventStageData eventStageData) {
        final Stage stage = (Stage)eventStageData.id;
        setLastStageInfo(stage);
        getStageStatus(stage).setState(StageStatus.State.ACTIVE);

        switch (stage) {
            case START: break;
            case MINOR_EVENT: {
                lockStage();
                _currentMaximum = PROGRESS_MAJOR;
                _checkPoint = PROGRESS_MINOR;
                break;
            }
            case MAJOR_EVENT: {
                lockStage();
                _currentMaximum = MAX_PROGRESS;
                _checkPoint = PROGRESS_MAJOR;
                break;
            }
            case DEADLY_EVENT: {
                lockStage();
                break;
            }
            case FUEL_UPGRADE: {
                ShiftDriveManager.getInstance().setShiftJumpFuelUpgrade(true);
                getStageStatus(Stage.FUEL_UPGRADE).setState(StageStatus.State.COMPLETE);
                _checkPoint = PROGRESS_FUEL_UPGRADE;
                break;
            }
            case RANGE_UPGRADE: {
                ShiftDriveManager.getInstance().setShiftJumpRangeUpgrade(true);
                getStageStatus(Stage.RANGE_UPGRADE).setState(StageStatus.State.COMPLETE);
                _checkPoint = PROGRESS_RANGE_UPGRADE;
                break;
            }
        }
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> set = super.getIntelTags(map);
        set.add("Shift Drive");
        return set;
    }

    public float getImageSizeForStageDesc(Object stageId) {
        return getStageIconSize(stageId);
    }
}
