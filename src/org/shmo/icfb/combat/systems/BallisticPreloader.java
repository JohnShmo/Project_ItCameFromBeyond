package org.shmo.icfb.combat.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicUI;
import org.shmo.icfb.IcfbGlobal;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.magiclib.util.MagicUI.*;

public class BallisticPreloader extends BaseShipSystemScript {
    public static final String ID = "icfb_ballistic_preloader";
    public static final String DATA_KEY = "$ICFB_BALLISTIC_PRELOADER_DATA";
    public static final String LATCH_KEY = "$ICFB_BALLISTIC_PRELOADER_LATCH";

    public static class Data {
        public static class Entry {

            private final int _maxCharges;
            private int _curCharges;

            public Entry(WeaponAPI weapon) {
                float pool;
                switch (weapon.getSize()) {
                    case SMALL:
                        pool = IcfbGlobal.getSettings().shipSystem.ballisticPreloaderSmallPool.get();
                        break;
                    case MEDIUM:
                        pool = IcfbGlobal.getSettings().shipSystem.ballisticPreloaderMediumPool.get();
                        break;
                    case LARGE:
                        pool = IcfbGlobal.getSettings().shipSystem.ballisticPreloaderLargePool.get();
                        break;
                    default:
                        pool = 0f;
                        break;
                }

                float damagePerShot = weapon.getDamage().getBaseDamage();
                _maxCharges = (int)(pool / damagePerShot) + 1;
                _curCharges = 0;
            }

            public void reset() {
                _curCharges = _maxCharges;
            }

            public void expendCharge() {
                if (_curCharges > 0)
                    _curCharges -= 1;
            }

            public int getCurrentCharges() {
                return _curCharges;
            }

            public void setCurrentCharges(int charges) {
                _curCharges = charges;
            }

            public int getMaxCharges() {
                return _maxCharges;
            }

            public boolean hasCharges() {
                return _curCharges > 0;
            }
        }

        private final Map<WeaponAPI, Entry> _entries = new HashMap<>();

        public void tryRegisterWeapon(WeaponAPI weapon) {
            if (weapon.isBeam())
                return;
            if (!weapon.getType().equals(WeaponAPI.WeaponType.BALLISTIC)
                    && !weapon.getType().equals(WeaponAPI.WeaponType.HYBRID))
                return;
            Entry entry = new Entry(weapon);
            _entries.put(weapon, entry);
        }

        public boolean hasEntryForWeapon(WeaponAPI weapon) {
            return _entries.containsKey(weapon);
        }

        public Entry getEntryForWeapon(WeaponAPI weapon) {
            return _entries.get(weapon);
        }

        public List<WeaponAPI> getWeaponsWithEntries() {
            return new ArrayList<>(_entries.keySet());
        }
    }

    public static Data getDataForShip(ShipAPI ship) {
        Object dataObject = ship.getCustomData().get(DATA_KEY);
        if (dataObject == null) {
            ship.setCustomData(DATA_KEY, new Data());
            dataObject = ship.getCustomData().get(DATA_KEY);
        }
        return (Data)dataObject;
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        final ShipAPI ship = (ShipAPI)stats.getEntity();
        if (ship == null)
            return;
        if (state.equals(State.ACTIVE) || state.equals(State.IN)) {
            if (ship.getCustomData().get(LATCH_KEY) == null) {
                ship.setCustomData(LATCH_KEY, new Object());
                final Data data = getDataForShip(ship);
                final List<WeaponAPI> weapons = data.getWeaponsWithEntries();
                for (WeaponAPI weapon : weapons) {
                    data.getEntryForWeapon(weapon).reset();
                }
            }
        } else if (ship.getCustomData().get(LATCH_KEY) != null) {
            ship.setCustomData(LATCH_KEY, null);
        }
    }

    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        final Data data = getDataForShip(ship);
        final List<WeaponAPI> weapons = data.getWeaponsWithEntries();
        for (WeaponAPI weapon : weapons) {
            if (data.getEntryForWeapon(weapon).getCurrentCharges() < data.getEntryForWeapon(weapon).getMaxCharges())
                return true;
        }
        return false;
    }
}
