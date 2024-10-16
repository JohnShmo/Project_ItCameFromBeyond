package org.shmo.icfb.combat.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;

public class TargetAcquisitionArray extends BaseHullMod {

    public static final String ID = "icfb_target_acquisition_array";
    public static final float BONUS_SMALL = 50f;
    public static final float BONUS_MEDIUM = 33f;
    public static final float BONUS_LARGE = 25f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.addListener(new TargetAcquisitionArrayModifier(BONUS_SMALL, BONUS_MEDIUM, BONUS_LARGE));
    }

    public static class TargetAcquisitionArrayModifier implements WeaponBaseRangeModifier {
        public float small, medium, large;
        public TargetAcquisitionArrayModifier(float small, float medium, float large) {
            this.small = small;
            this.medium = medium;
            this.large = large;
        }

        public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
            if (weapon.getSpec() == null) {
                return 0f;
            }
            if (weapon.getSpec().getMountType() != WeaponAPI.WeaponType.BALLISTIC &&
                    weapon.getSpec().getMountType() != WeaponAPI.WeaponType.HYBRID &&
                    weapon.getSpec().getMountType() != WeaponAPI.WeaponType.ENERGY) {
                return 0f;
            }
            float bonus = 0f;
            switch (weapon.getSpec().getSize()) {
                case SMALL:
                    bonus = small;
                    break;
                case MEDIUM:
                    bonus = medium;
                    break;
                case LARGE:
                    bonus = large;
                    break;
            }
            return bonus;
        }
        public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
            return 1f;
        }
        public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) { return 0f; }
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return "" + (int)BONUS_SMALL + "%";
        if (index == 1) return "" + (int)BONUS_MEDIUM + "%";
        if (index == 2) return "" + (int)BONUS_LARGE + "%";
        return null;
    }
}
