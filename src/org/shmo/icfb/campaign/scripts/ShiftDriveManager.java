package org.shmo.icfb.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.campaign.abilities.ShiftJump;
import org.shmo.icfb.campaign.intel.events.ShiftDriveUsageEvent;
import org.shmo.icfb.utilities.BasicTimer;
import org.shmo.icfb.utilities.ScriptFactory;
import org.shmo.icfb.utilities.Timer;

public class ShiftDriveManager implements EveryFrameScript {
    public static final String KEY = "$icfb_ShiftDriveManager";

    public static final String UNLOCKED_MEMORY_KEY = "$icfb_ShiftDriveManager_unlocked";
    public static final String USE_COUNT_MEMORY_KEY = "$icfb_ShiftDriveManager_useCount";
    public static final String TOTAL_DISTANCE_MEMORY_KEY = "$icfb_ShiftDriveManager_totalDistance";
    public static final String RANGE_UPGRADE_MEMORY_KEY = "$icfb_ShiftDriveManager_hasRangeUpgrade";
    public static final String FUEL_UPGRADE_MEMORY_KEY = "$icfb_ShiftDriveManager_hasFuelUpgrade";

    public static final float RANGE_UPGRADE_MULTIPLIER = 1.0f + (2f/3f);
    public static final float FUEL_UPGRADE_MULTIPLIER = 0.5f;

    public static class Factory implements ScriptFactory {
        @Override
        public EveryFrameScript getInstance() {
            EveryFrameScript script = ShiftDriveManager.getInstance();
            if (script == null)
                script = new ShiftDriveManager();
            return script;
        }
    }

    private final Timer _reportIntelTimer = new BasicTimer(1f);
    private boolean _shiftJumpUsageDirty = false;

    public static ShiftDriveManager getInstance() {
        return (ShiftDriveManager)Global.getSector().getMemoryWithoutUpdate().get(KEY);
    }

    public ShiftDriveManager() {
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
    }

    public boolean isShiftJumpUnlocked() {
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        if (!memory.contains(UNLOCKED_MEMORY_KEY))
            return false;
        return memory.getBoolean(UNLOCKED_MEMORY_KEY);
    }

    public void setShiftJumpUnlocked(boolean unlocked) {
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        memory.set(UNLOCKED_MEMORY_KEY, unlocked);
        ItCameFromBeyond.Log.info("Set Shift Jump unlocked status to: " + unlocked);
    }

    public int getShiftJumpUses() {
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        if (!memory.contains(USE_COUNT_MEMORY_KEY))
            return 0;
        return memory.getInt(USE_COUNT_MEMORY_KEY);
    }

    public void setShiftJumpUses(int count) {
        markShiftJumpUsageDirty();
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        memory.set(USE_COUNT_MEMORY_KEY, count);
        ItCameFromBeyond.Log.info("Set uses of Shift Jump to: " + count);
    }

    public void incrementShiftJumpUses() {
        setShiftJumpUses(getShiftJumpUses() + 1);
    }

    public float getShiftJumpTotalDistance() {
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        if (!memory.contains(TOTAL_DISTANCE_MEMORY_KEY))
            return 0;
        return memory.getFloat(TOTAL_DISTANCE_MEMORY_KEY);
    }

    public void setShiftJumpTotalDistance(float totalDistance) {
        markShiftJumpUsageDirty();
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        memory.set(TOTAL_DISTANCE_MEMORY_KEY, totalDistance);
        ItCameFromBeyond.Log.info("Set total distance traveled with Shift Jump to: " + totalDistance + " light years");
    }

    public void addToShiftJumpTotalDistance(float amount) {
        setShiftJumpTotalDistance(getShiftJumpTotalDistance() + amount);
    }

    public boolean hasShiftJumpRangeUpgrade() {
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        if (!memory.contains(RANGE_UPGRADE_MEMORY_KEY))
            return false;
        return memory.getBoolean(RANGE_UPGRADE_MEMORY_KEY);
    }

    public void setShiftJumpRangeUpgrade(boolean rangeUpgradeStatus) {
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        memory.set(RANGE_UPGRADE_MEMORY_KEY, rangeUpgradeStatus);
        ItCameFromBeyond.Log.info("Set Shift Jump range upgrade status to: " + rangeUpgradeStatus);
    }

    public boolean hasShiftJumpFuelUpgrade() {
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        if (!memory.contains(FUEL_UPGRADE_MEMORY_KEY))
            return false;
        return memory.getBoolean(FUEL_UPGRADE_MEMORY_KEY);
    }

    public void setShiftJumpFuelUpgrade(boolean fuelUpgradeStatus) {
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        memory.set(FUEL_UPGRADE_MEMORY_KEY, fuelUpgradeStatus);
        ItCameFromBeyond.Log.info("Set Shift Jump fuel upgrade status to: " + fuelUpgradeStatus);
    }

    private void applyUpgradesAndUnlock() {
        ShiftJump shiftJump = ItCameFromBeyond.Global.getPlayerShiftJump();
        if (shiftJump == null)
            return;
        if (!isShiftJumpUnlocked())
            setShiftJumpUnlocked(true);
        if (hasShiftJumpFuelUpgrade())
            shiftJump.setFuelCostMultiplier(FUEL_UPGRADE_MULTIPLIER);
        if (hasShiftJumpRangeUpgrade())
            shiftJump.setMaxRangeMultiplier(RANGE_UPGRADE_MULTIPLIER);
    }

    private boolean shouldReportUsesToIntel(float deltaTime) {
        if (!isShiftJumpUnlocked() || !isShiftJumpUsageDirty())
            return false;
        return _reportIntelTimer.advance(deltaTime);
    }

    private void reportShiftJumpUsageToIntel() {
        ItCameFromBeyond.Log.info("Reported Shift Jump usage.");
        markShiftJumpUsageClean();
        ShiftDriveUsageEvent eventIntel = ItCameFromBeyond.Global.getShiftDriveUsageEvent();
        if (eventIntel == null) {
            eventIntel = new ShiftDriveUsageEvent();
        }
        eventIntel.reportUses(getShiftJumpUses());
        eventIntel.reportTotalDistance(getShiftJumpTotalDistance());
    }

    private void markShiftJumpUsageDirty() {
        _shiftJumpUsageDirty = true;
    }

    private void markShiftJumpUsageClean() {
        _shiftJumpUsageDirty = false;
    }

    private boolean isShiftJumpUsageDirty() {
        return _shiftJumpUsageDirty;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        applyUpgradesAndUnlock();
        if (shouldReportUsesToIntel(amount))
            reportShiftJumpUsageToIntel();
    }
}
