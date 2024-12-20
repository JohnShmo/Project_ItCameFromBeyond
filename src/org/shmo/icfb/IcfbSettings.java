package org.shmo.icfb;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class IcfbSettings {

    public static class ShiftJumpSettings {

        public enum CRPenaltyCurve {
            FAST,
            MEDIUM,
            SLOW
        }

        // DEFAULTS ====================================================================================================

        private static final float DEFAULT_CR_PENALTY_AT_MAX_RANGE = 0.5f;
        private static final CRPenaltyCurve DEFAULT_CR_PENALTY_CURVE = CRPenaltyCurve.MEDIUM;

        private static final float DEFAULT_BASE_EXTRA_FUEL_PERCENT = 0.25f;
        private static final float DEFAULT_FUEL_UPGRADE_MULTIPLIER = 0.5f;

        private static final float DEFAULT_BASE_MAX_RANGE_LY = 15f;
        private static final float DEFAULT_RANGE_UPGRADE_MULTIPLIER = 1.666666f;

        private static final float DEFAULT_ARRIVAL_DISTANCE_FROM_DESTINATION = 400f;

        // FIELDS ======================================================================================================

        public float crPenaltyAtMaxRange = DEFAULT_CR_PENALTY_AT_MAX_RANGE;
        public CRPenaltyCurve crPenaltyCurve = DEFAULT_CR_PENALTY_CURVE;

        public float baseExtraFuelPercent = DEFAULT_BASE_EXTRA_FUEL_PERCENT;
        public float fuelUpgradeMultiplier = DEFAULT_FUEL_UPGRADE_MULTIPLIER;

        public float baseMaxRangeLY = DEFAULT_BASE_MAX_RANGE_LY;
        public float rangeUpgradeMultiplier = DEFAULT_RANGE_UPGRADE_MULTIPLIER;

        public float arrivalDistanceFromDestination = DEFAULT_ARRIVAL_DISTANCE_FROM_DESTINATION;

        // METHODS =====================================================================================================

        public void loadFromJSON(@NotNull JSONObject json) {
            crPenaltyAtMaxRange = (float)json.optDouble("crPenaltyAtMaxRange", DEFAULT_CR_PENALTY_AT_MAX_RANGE);
            String crPenaltyCurveString = json.optString("crPenaltyCurve", "");
            switch (crPenaltyCurveString) {
                case "FAST": crPenaltyCurve = CRPenaltyCurve.FAST; break;
                case "MEDIUM": crPenaltyCurve = CRPenaltyCurve.MEDIUM; break;
                case "SLOW": crPenaltyCurve = CRPenaltyCurve.SLOW; break;
                default: crPenaltyCurve = DEFAULT_CR_PENALTY_CURVE; break;
            }

            baseExtraFuelPercent = (float)json.optDouble("baseExtraFuelPercent", DEFAULT_BASE_EXTRA_FUEL_PERCENT);
            fuelUpgradeMultiplier = (float)json.optDouble("fuelUpgradeMultiplier", DEFAULT_FUEL_UPGRADE_MULTIPLIER);

            baseMaxRangeLY = (float)json.optDouble("baseMaxRangeLY", DEFAULT_BASE_MAX_RANGE_LY);
            rangeUpgradeMultiplier = (float)json.optDouble(
                    "rangeUpgradeMultiplier",
                    DEFAULT_RANGE_UPGRADE_MULTIPLIER
            );

            arrivalDistanceFromDestination = (float)json.optDouble(
                    "arrivalDistanceFromDestination",
                    DEFAULT_ARRIVAL_DISTANCE_FROM_DESTINATION
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
        private static final float DEFAULT_QUANTUM_INVERSION_MAX_TIME_SPAN = 4f;
        private static final float DEFAULT_QUANTUM_INVERSION_KEYFRAME_INTERVAL = 0.1f;
        private static final float DEFAULT_BALLISTIC_PRELOADER_SMALL_POOL = 500f;
        private static final float DEFAULT_BALLISTIC_PRELOADER_MEDIUM_POOL = 1000f;
        private static final float DEFAULT_BALLISTIC_PRELOADER_LARGE_POOL = 1500f;
        private static final float DEFAULT_BALLISTIC_PRELOADER_BONUS_DAMAGE_PERCENT = 50f;

        // FIELDS ======================================================================================================

        public float thrusterPulseImpulseMagnitude = DEFAULT_THRUSTER_PULSE_IMPULSE_MAGNITUDE;
        public float quantumInversionMaxTimeSpan = DEFAULT_QUANTUM_INVERSION_MAX_TIME_SPAN;
        public float quantumInversionKeyframeInterval = DEFAULT_QUANTUM_INVERSION_KEYFRAME_INTERVAL;
        public float ballisticPreloaderSmallPool = DEFAULT_BALLISTIC_PRELOADER_SMALL_POOL;
        public float ballisticPreloaderMediumPool = DEFAULT_BALLISTIC_PRELOADER_MEDIUM_POOL;
        public float ballisticPreloaderLargePool = DEFAULT_BALLISTIC_PRELOADER_LARGE_POOL;
        public float ballisticPreloaderBonusDamagePercent = DEFAULT_BALLISTIC_PRELOADER_BONUS_DAMAGE_PERCENT;

        // METHODS =====================================================================================================
        public void loadFromJSON(@NotNull JSONObject json) {
            thrusterPulseImpulseMagnitude =
                    (float)json.optDouble("thrusterPulseImpulseMagnitude", DEFAULT_THRUSTER_PULSE_IMPULSE_MAGNITUDE);
            quantumInversionMaxTimeSpan =
                    (float)json.optDouble("quantumInversionMaxTimeSpan", DEFAULT_QUANTUM_INVERSION_MAX_TIME_SPAN);
            quantumInversionKeyframeInterval =
                    (float)json.optDouble("quantumInversionKeyframeInterval", DEFAULT_QUANTUM_INVERSION_KEYFRAME_INTERVAL);
            ballisticPreloaderSmallPool =
                    (float)json.optDouble("ballisticPreloaderSmallPool", DEFAULT_BALLISTIC_PRELOADER_SMALL_POOL);
            ballisticPreloaderMediumPool =
                    (float)json.optDouble("ballisticPreloaderMediumPool", DEFAULT_BALLISTIC_PRELOADER_MEDIUM_POOL);
            ballisticPreloaderLargePool =
                    (float)json.optDouble("ballisticPreloaderLargePool", DEFAULT_BALLISTIC_PRELOADER_LARGE_POOL);
            ballisticPreloaderBonusDamagePercent =
                    (float)json.optDouble("ballisticPreloaderBonusDamagePercent", DEFAULT_BALLISTIC_PRELOADER_BONUS_DAMAGE_PERCENT);
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
