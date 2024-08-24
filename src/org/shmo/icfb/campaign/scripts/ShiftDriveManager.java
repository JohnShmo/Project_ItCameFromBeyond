package org.shmo.icfb.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import org.shmo.icfb.ItCameFromBeyond;

public class ShiftDriveManager implements EveryFrameScript {
    public static final String ID = "icfb_ShiftDriveManager";
    public static final String USE_COUNT_MEMORY_ID = "$icfb_ShiftDriveManager_useCount";
    public static final String RANGE_UPGRADE_MEMORY_ID = "$icfb_ShiftDriveManager_hasRangeUpgrade";
    public static final String FUEL_UPGRADE_MEMORY_ID = "$icfb_ShiftDriveManager_hasFuelUpgrade";

    public static final float RANGE_UPGRADE_MULTIPLIER = 1.0f + (2f/3f);
    public static final float FUEL_UPGRADE_MULTIPLIER = 0.5f;

    public static ShiftDriveManager getInstance() {
        return (ShiftDriveManager)Global.getSettings().getPlugin(ID);
    }

    public int getUses() {
        MemoryAPI memory = Global.getSector().getMemory();
        if (!memory.contains(USE_COUNT_MEMORY_ID))
            return 0;
        return memory.getInt(USE_COUNT_MEMORY_ID);
    }

    public void setUses(int count) {
        MemoryAPI memory = Global.getSector().getMemory();
        memory.set(USE_COUNT_MEMORY_ID, count);
        ItCameFromBeyond.Log.info("Set uses of Shift Drive to: " + count);
    }

    public void incrementUses() {
        setUses(getUses() + 1);
    }

    public boolean hasRangeUpgrade() {
        MemoryAPI memory = Global.getSector().getMemory();
        if (!memory.contains(RANGE_UPGRADE_MEMORY_ID))
            return false;
        return memory.getBoolean(RANGE_UPGRADE_MEMORY_ID);
    }

    public void setRangeUpgrade(boolean rangeUpgradeStatus) {
        MemoryAPI memory = Global.getSector().getMemory();
        memory.set(RANGE_UPGRADE_MEMORY_ID, rangeUpgradeStatus);
        ItCameFromBeyond.Log.info("Set Shift Drive range upgrade status to: " + rangeUpgradeStatus);
    }

    public boolean hasFuelUpgrade() {
        MemoryAPI memory = Global.getSector().getMemory();
        if (!memory.contains(FUEL_UPGRADE_MEMORY_ID))
            return false;
        return memory.getBoolean(FUEL_UPGRADE_MEMORY_ID);
    }

    public void setFuelUpgrade(boolean fuelUpgradeStatus) {
        MemoryAPI memory = Global.getSector().getMemory();
        memory.set(FUEL_UPGRADE_MEMORY_ID, fuelUpgradeStatus);
        ItCameFromBeyond.Log.info("Set Shift Drive fuel upgrade status to: " + fuelUpgradeStatus);
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
        if (hasFuelUpgrade() && ItCameFromBeyond.Global.getPlayerShiftJump() != null)
            ItCameFromBeyond.Global.getPlayerShiftJump().setFuelCostMultiplier(FUEL_UPGRADE_MULTIPLIER);
        if (hasRangeUpgrade() && ItCameFromBeyond.Global.getPlayerShiftJump() != null)
            ItCameFromBeyond.Global.getPlayerShiftJump().setMaxRangeMultiplier(RANGE_UPGRADE_MULTIPLIER);
    }
}
