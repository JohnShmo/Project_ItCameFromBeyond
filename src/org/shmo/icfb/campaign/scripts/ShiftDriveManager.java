package org.shmo.icfb.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.campaign.abilities.ShiftJump;
import org.shmo.icfb.campaign.intel.events.ShiftDriveEvent;
import org.shmo.icfb.utilities.ScriptFactory;

public class ShiftDriveManager implements EveryFrameScript {
    public static final String KEY = "$icfb_ShiftDriveManager";

    public static final String UNLOCKED_MEMORY_KEY = "$icfb_ShiftDriveManager_unlocked";
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

    public void addToShiftJumpTotalDistance(float amount) {
        ShiftDriveEvent.addFactorCreateIfNecessary(ShiftDriveEvent.Factor.SHIFT_JUMP_USE, amount);
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
    }
}
