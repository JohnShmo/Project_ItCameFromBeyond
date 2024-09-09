package org.shmo.icfb;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class ItCameFromBeyondSettings {

    public static class ShiftJumpSettings {

        public enum CRPenaltyCurve {
            GENTLE,
            MODERATE,
            EXTREME
        }

        // DEFAULTS ====================================================================================================

        private static final float DEFAULT_CR_PENALTY_AT_MAX_RANGE = 0.5f;
        private static final CRPenaltyCurve DEFAULT_CR_PENALTY_CURVE = CRPenaltyCurve.MODERATE;

        private static final float DEFAULT_BASE_EXTRA_FUEL_PERCENT = 0.25f;
        private static final float DEFAULT_FUEL_UPGRADE_MULTIPLIER = 0.5f;

        private static final float DEFAULT_BASE_MAX_RANGE_LY = 15f;
        private static final float DEFAULT_RANGE_UPGRADE_MULTIPLIER = 1.666666f;

        // FIELDS ======================================================================================================

        public float crPenaltyAtMaxRange = DEFAULT_CR_PENALTY_AT_MAX_RANGE;
        public CRPenaltyCurve crPenaltyCurve = DEFAULT_CR_PENALTY_CURVE;

        public float baseExtraFuelPercent = DEFAULT_BASE_EXTRA_FUEL_PERCENT;
        public float fuelUpgradeMultiplier = DEFAULT_FUEL_UPGRADE_MULTIPLIER;

        public float baseMaxRangeLY = DEFAULT_BASE_MAX_RANGE_LY;
        public float rangeUpgradeMultiplier = DEFAULT_RANGE_UPGRADE_MULTIPLIER;

        // METHODS =====================================================================================================

        public void loadFromJSON(@NotNull JSONObject json) {
            crPenaltyAtMaxRange = (float)json.optDouble("crPenaltyAtMaxRange", DEFAULT_CR_PENALTY_AT_MAX_RANGE);
            String crPenaltyCurveString = json.optString("crPenaltyCurve", "");
            switch (crPenaltyCurveString) {
                case "GENTLE": crPenaltyCurve = CRPenaltyCurve.GENTLE; break;
                case "MODERATE": crPenaltyCurve = CRPenaltyCurve.MODERATE; break;
                case "EXTREME": crPenaltyCurve = CRPenaltyCurve.EXTREME; break;
                default: crPenaltyCurve = DEFAULT_CR_PENALTY_CURVE; break;
            }

            baseExtraFuelPercent = (float)json.optDouble("baseExtraFuelPercent", DEFAULT_BASE_EXTRA_FUEL_PERCENT);
            fuelUpgradeMultiplier = (float)json.optDouble("fuelUpgradeMultiplier", DEFAULT_FUEL_UPGRADE_MULTIPLIER);

            baseMaxRangeLY = (float)json.optDouble("baseMaxRangeLY", DEFAULT_BASE_MAX_RANGE_LY);
            rangeUpgradeMultiplier = (float)json.optDouble(
                    "rangeUpgradeMultiplier",
                    DEFAULT_RANGE_UPGRADE_MULTIPLIER
            );
        }
    }

    public static class ShiftDriveEventSettings {

        // DEFAULTS ====================================================================================================

        private static final boolean DEFAULT_IS_ENABLED = true;

        private static final int DEFAULT_POINTS_PER_LY_TRAVELED_WITH_SHIFT_JUMP = 1;
        private static final int DEFAULT_POINTS_PER_SHIFT_JUMP_USE = 5;

        private static final boolean DEFAULT_IS_THE_HUNT_REPEATABLE = true;

        // FIELDS ======================================================================================================

        public boolean isEnabled = DEFAULT_IS_ENABLED;

        public int pointsPerLYTraveledWithShiftJump = DEFAULT_POINTS_PER_LY_TRAVELED_WITH_SHIFT_JUMP;
        public int pointsPerShiftJumpUse = DEFAULT_POINTS_PER_SHIFT_JUMP_USE;

        public boolean isTheHuntRepeatable = DEFAULT_IS_THE_HUNT_REPEATABLE;

        // METHODS =====================================================================================================

        public void loadFromJSON(@NotNull JSONObject json) {
            isEnabled = json.optBoolean("isEnabled", DEFAULT_IS_ENABLED);

            pointsPerLYTraveledWithShiftJump = json.optInt(
                    "pointsPerLYTraveledWithShiftJump",
                    DEFAULT_POINTS_PER_LY_TRAVELED_WITH_SHIFT_JUMP
            );
            pointsPerShiftJumpUse = json.optInt("pointsPerShiftJumpUse", DEFAULT_POINTS_PER_SHIFT_JUMP_USE);

            isTheHuntRepeatable = json.optBoolean("isTheHuntRepeatable", DEFAULT_IS_THE_HUNT_REPEATABLE);
        }
    }

    public static class ShipSystemSettings {

        // DEFAULTS ====================================================================================================

        private static final float DEFAULT_THRUSTER_PULSE_IMPULSE_MAGNITUDE = 1000f;

        // FIELDS ======================================================================================================

        public float thrusterPulseImpulseMagnitude = DEFAULT_THRUSTER_PULSE_IMPULSE_MAGNITUDE;

        // METHODS =====================================================================================================
        public void loadFromJSON(@NotNull JSONObject json) {
            thrusterPulseImpulseMagnitude =
                    (float)json.optDouble("thrusterPulseImpulseMagnitude", DEFAULT_THRUSTER_PULSE_IMPULSE_MAGNITUDE);
        }
    }

    public final ShiftJumpSettings shiftJump = new ShiftJumpSettings();
    public final ShiftDriveEventSettings shiftDriveEvent = new ShiftDriveEventSettings();
    public final ShipSystemSettings shipSystem = new ShipSystemSettings();

    public void loadFromJSON(@NotNull JSONObject json) {
        JSONObject shiftJumpJSON = json.optJSONObject("shiftJump");
        if (shiftJumpJSON != null)
            shiftJump.loadFromJSON(shiftJumpJSON);

        JSONObject shiftDriveEventJSON = json.optJSONObject("shiftDriveEvent");
        if (shiftDriveEventJSON != null)
            shiftDriveEvent.loadFromJSON(shiftDriveEventJSON);

        JSONObject shipSystemJSON = json.optJSONObject("shipSystem");
        if (shipSystemJSON != null)
            shipSystem.loadFromJSON(shipSystemJSON);
    }
}
