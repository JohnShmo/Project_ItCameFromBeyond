package org.shmo.icfb.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import org.shmo.icfb.ItCameFromBeyond;

public class ShiftDriveManager implements EveryFrameScript {
    public static final String ID = "icfb_ShiftDriveManager";
    public static final String USE_COUNT_MEMORY_ID = "$icfb_ShiftDriveManager_useCount";

    public static ShiftDriveManager getInstance() {
        return (ShiftDriveManager)Global.getSettings().getPlugin(ID);
    }

    public void incrementUses() {
        setUses(getUses() + 1);
    }

    public void setUses(int count) {
        MemoryAPI memory = Global.getSector().getMemory();
        memory.set(USE_COUNT_MEMORY_ID, count);
        ItCameFromBeyond.Log.info("Set uses of Shift Drive to: " + getUses());
    }

    public int getUses() {
        MemoryAPI memory = Global.getSector().getMemory();
        if (!memory.contains(USE_COUNT_MEMORY_ID))
            return 0;
        return memory.getInt(USE_COUNT_MEMORY_ID);
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

    }
}
