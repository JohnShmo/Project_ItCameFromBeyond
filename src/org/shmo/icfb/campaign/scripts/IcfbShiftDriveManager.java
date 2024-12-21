package org.shmo.icfb.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import org.shmo.icfb.IcfbGlobal;
import org.shmo.icfb.campaign.abilities.ShiftJump;
import org.shmo.icfb.factories.ScriptFactory;
import org.shmo.icfb.campaign.listeners.ShiftDriveListener;
import org.shmo.icfb.utilities.MemoryHelper;

import java.util.HashSet;
import java.util.Set;

public class IcfbShiftDriveManager implements EveryFrameScript {
    public static final String KEY = "$IcfbShiftDriveManager";
    public static final String LISTENERS_KEY = KEY + ":listeners";
    public static final String SHIFT_JUMP_UNLOCKED_KEY = KEY + ":shiftJumpUnlocked";
    public static final String RANGE_UPGRADE_UNLOCKED_KEY = KEY + ":rangeUpgradeUnlocked";
    public static final String FUEL_UPGRADE_UNLOCKED_KEY = KEY + ":fuelUpgradeUnlocked";

    public static class Factory implements ScriptFactory {
        @Override
        public EveryFrameScript createOrGetInstance() {
            EveryFrameScript script = IcfbShiftDriveManager.getInstance();
            if (script == null)
                script = new IcfbShiftDriveManager();
            return script;
        }
    }

    public static IcfbShiftDriveManager getInstance() {
        return (IcfbShiftDriveManager)Global.getSector().getMemoryWithoutUpdate().get(KEY);
    }

    public IcfbShiftDriveManager() {
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
    }

    private Set<ShiftDriveListener> getListeners() {
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        Set<ShiftDriveListener> result = MemoryHelper.get(memory, LISTENERS_KEY);
        if (result == null)
            result = MemoryHelper.set(memory, LISTENERS_KEY, new HashSet<ShiftDriveListener>());
        return result;
    }

    public void addListener(ShiftDriveListener listener) {
        getListeners().add(listener);
    }

    public void removeListener(ShiftDriveListener listener) {
        getListeners().remove(listener);
    }

    public boolean isShiftJumpUnlocked() {
       return Boolean.TRUE.equals(MemoryHelper.get(Global.getSector().getMemoryWithoutUpdate(), SHIFT_JUMP_UNLOCKED_KEY));
    }

    public void setShiftJumpUnlocked(boolean unlocked) {
        MemoryHelper.set(Global.getSector().getMemoryWithoutUpdate(), SHIFT_JUMP_UNLOCKED_KEY, unlocked);
    }

    public void notifyShiftJumpUsed(CampaignFleetAPI fleet, float distanceLY) {
        final Set<ShiftDriveListener> listeners = getListeners();
        for (ShiftDriveListener listener : listeners) {
            listener.notifyShiftJumpUsed(fleet, distanceLY);
        }
    }

    public boolean hasShiftJumpRangeUpgrade() {
        return Boolean.TRUE.equals(MemoryHelper.get(Global.getSector().getMemoryWithoutUpdate(), RANGE_UPGRADE_UNLOCKED_KEY));
    }

    public void setShiftJumpRangeUpgrade(boolean rangeUpgradeStatus) {
        MemoryHelper.set(Global.getSector().getMemoryWithoutUpdate(), RANGE_UPGRADE_UNLOCKED_KEY, rangeUpgradeStatus);
    }

    public boolean hasShiftJumpFuelUpgrade() {
        return Boolean.TRUE.equals(MemoryHelper.get(Global.getSector().getMemoryWithoutUpdate(), FUEL_UPGRADE_UNLOCKED_KEY));
    }

    public void setShiftJumpFuelUpgrade(boolean fuelUpgradeStatus) {
        MemoryHelper.set(Global.getSector().getMemoryWithoutUpdate(), FUEL_UPGRADE_UNLOCKED_KEY, fuelUpgradeStatus);
    }

    private void applyUpgradesAndUnlock() {
        ShiftJump shiftJump = IcfbGlobal.getPlayerShiftJump();
        if (shiftJump == null)
            return;
        if (!isShiftJumpUnlocked())
            setShiftJumpUnlocked(true);
        if (hasShiftJumpFuelUpgrade())
            shiftJump.setFuelCostMultiplier(IcfbGlobal.getSettings().shiftJump.fuelUpgradeMultiplier.get());
        if (hasShiftJumpRangeUpgrade())
            shiftJump.setMaxRangeMultiplier(IcfbGlobal.getSettings().shiftJump.rangeUpgradeMultiplier.get());
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
