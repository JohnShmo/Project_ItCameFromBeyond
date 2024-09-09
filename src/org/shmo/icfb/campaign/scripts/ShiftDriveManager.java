package org.shmo.icfb.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.campaign.abilities.ShiftJump;
import org.shmo.icfb.campaign.intel.events.ShiftDriveEvent;
import org.shmo.icfb.factories.ScriptFactory;
import org.shmo.icfb.campaign.listeners.ShiftDriveListener;

import java.util.HashSet;
import java.util.Set;

public class ShiftDriveManager implements EveryFrameScript {
    public static final String KEY = "$icfb_ShiftDriveManager";

    public static class Factory implements ScriptFactory {
        @Override
        public EveryFrameScript createOrGetInstance() {
            EveryFrameScript script = ShiftDriveManager.getInstance();
            if (script == null)
                script = new ShiftDriveManager();
            return script;
        }
    }

    public static ShiftDriveManager getInstance() {
        return (ShiftDriveManager)Global.getSector().getMemoryWithoutUpdate().get(KEY);
    }

    private final Set<ShiftDriveListener> _listeners;
    private boolean _shiftJumpUnlocked;
    private boolean _rangeUpgradeUnlocked;
    private boolean _fuelUpgradeUnlocked;

    public ShiftDriveManager() {
        _listeners = new HashSet<>();
        _shiftJumpUnlocked = false;
        _rangeUpgradeUnlocked = false;
        _fuelUpgradeUnlocked = false;

        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
    }

    private Set<ShiftDriveListener> getListeners() {
        return _listeners;
    }

    public void addListener(ShiftDriveListener listener) {
        getListeners().add(listener);
    }

    public void removeListener(ShiftDriveListener listener) {
        getListeners().remove(listener);
    }

    public boolean isShiftJumpUnlocked() {
       return _shiftJumpUnlocked;
    }

    public void setShiftJumpUnlocked(boolean unlocked) {
        _shiftJumpUnlocked = unlocked;
    }

    public void notifyShiftJumpUsed(CampaignFleetAPI fleet, float distanceLY) {
        if (ItCameFromBeyond.Global.getSettings().shiftDriveEvent.isEnabled) {
            if (ShiftDriveEvent.getInstance() == null)
                new ShiftDriveEvent();
        }

        final Set<ShiftDriveListener> listeners = getListeners();
        for (ShiftDriveListener listener : listeners) {
            listener.notifyShiftJumpUsed(fleet, distanceLY);
        }
    }

    public boolean hasShiftJumpRangeUpgrade() {
        return _rangeUpgradeUnlocked;
    }

    public void setShiftJumpRangeUpgrade(boolean rangeUpgradeStatus) {
        _rangeUpgradeUnlocked = rangeUpgradeStatus;
    }

    public boolean hasShiftJumpFuelUpgrade() {
        return _fuelUpgradeUnlocked;
    }

    public void setShiftJumpFuelUpgrade(boolean fuelUpgradeStatus) {
        _fuelUpgradeUnlocked = fuelUpgradeStatus;
    }

    private void applyUpgradesAndUnlock() {
        ShiftJump shiftJump = ItCameFromBeyond.Global.getPlayerShiftJump();
        if (shiftJump == null)
            return;
        if (!isShiftJumpUnlocked())
            setShiftJumpUnlocked(true);
        if (hasShiftJumpFuelUpgrade())
            shiftJump.setFuelCostMultiplier(ItCameFromBeyond.Global.getSettings().shiftJump.fuelUpgradeMultiplier);
        if (hasShiftJumpRangeUpgrade())
            shiftJump.setMaxRangeMultiplier(ItCameFromBeyond.Global.getSettings().shiftJump.rangeUpgradeMultiplier);
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
