package org.shmo.icfb;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class IcfbSettings {

    public static class Setting<T> {
        private T _value = null;
        private final T _default;

        public Setting(@NotNull final T defaultValue) {
            _default = defaultValue;
        }

        public void set(final T value) {
            _value = value;
        }

        @NotNull
        public T get() {
            if (_value == null)
                return _default;
            return _value;
        }

        @NotNull
        public T getDefault() {
            return _default;
        }
    }

    public static class FloatSetting extends Setting<Float> {
        public FloatSetting(float defaultValue) {
            super(defaultValue);
        }

        public void set(JSONObject json, String key) {
            set((float)json.optDouble(key, getDefault()));
        }
    }

    public static class IntegerSetting extends Setting<Integer> {
        public IntegerSetting(int defaultValue) {
            super(defaultValue);
        }

        public void set(JSONObject json, String key) {
            set(json.optInt(key, getDefault()));
        }
    }

    public static class BooleanSetting extends Setting<Boolean> {
        public BooleanSetting(boolean defaultValue) {
            super(defaultValue);
        }

        public void set(JSONObject json, String key) {
            set(json.optBoolean(key, getDefault()));
        }
    }

    public static class StringSetting extends Setting<String> {
        public StringSetting(@NotNull String defaultValue) {
            super(defaultValue);
        }

        public void set(JSONObject json, String key) {
            set(json.optString(key, getDefault()));
        }
    }

    public static class ShiftJumpSettings {

        public enum CRPenaltyCurve {
            FAST,
            MEDIUM,
            SLOW
        }

        public FloatSetting crPenaltyAtMaxRange = new FloatSetting(0.5f);
        public Setting<CRPenaltyCurve> crPenaltyCurve = new Setting<>(CRPenaltyCurve.FAST);
        public FloatSetting baseExtraFuelPercent = new FloatSetting(0.25f);
        public FloatSetting fuelUpgradeMultiplier = new FloatSetting(0.5f);
        public FloatSetting baseMaxRangeLY = new FloatSetting(10f);
        public FloatSetting rangeUpgradeMultiplier = new FloatSetting(2f);
        public FloatSetting arrivalDistanceFromDestination = new FloatSetting(500f);

        public void loadFromJSON(@NotNull JSONObject json) {
            crPenaltyAtMaxRange.set(json, "crPenaltyAtMaxRange");
            String crPenaltyCurveString = json.optString("crPenaltyCurve", "");
            switch (crPenaltyCurveString) {
                case "FAST": crPenaltyCurve.set(CRPenaltyCurve.FAST); break;
                case "MEDIUM": crPenaltyCurve.set(CRPenaltyCurve.MEDIUM); break;
                case "SLOW": crPenaltyCurve.set(CRPenaltyCurve.SLOW); break;
                default: crPenaltyCurve.set(null); break;
            }
            baseExtraFuelPercent.set(json, "baseExtraFuelPercent");
            fuelUpgradeMultiplier.set(json, "fuelUpgradeMultiplier");
            baseMaxRangeLY.set(json, "baseMaxRangeLY");
            rangeUpgradeMultiplier.set(json, "rangeUpgradeMultiplier");
            arrivalDistanceFromDestination.set(json, "arrivalDistanceFromDestination");
        }
    }

    public static class ShipSystemSettings {

        public FloatSetting thrusterPulseImpulseMagnitude = new FloatSetting(1200f);
        public FloatSetting quantumInversionMaxTimeSpan = new FloatSetting(4.0f);
        public FloatSetting quantumInversionKeyframeInterval = new FloatSetting(0.1f);
        public FloatSetting ballisticPreloaderSmallPool = new FloatSetting(600f);
        public FloatSetting ballisticPreloaderMediumPool = new FloatSetting(1000f);
        public FloatSetting ballisticPreloaderLargePool = new FloatSetting(1600f);
        public FloatSetting ballisticPreloaderBonusDamagePercent = new FloatSetting(70.0f);

        public void loadFromJSON(@NotNull JSONObject json) {
            thrusterPulseImpulseMagnitude.set(json, "thrusterPulseImpulseMagnitude");
            quantumInversionMaxTimeSpan.set(json, "quantumInversionMaxTimeSpan");
            quantumInversionKeyframeInterval.set(json, "quantumInversionKeyframeInterval");
            ballisticPreloaderSmallPool.set(json, "ballisticPreloaderSmallPool");
            ballisticPreloaderMediumPool.set(json, "ballisticPreloaderMediumPool");
            ballisticPreloaderLargePool.set(json, "ballisticPreloaderLargePool");
            ballisticPreloaderBonusDamagePercent.set(json, "ballisticPreloaderBonusDamagePercent");
        }
    }

    public static class IncursionSettings {

        public BooleanSetting isEnabled = new BooleanSetting(true);
        public IntegerSetting pointsPerShiftJumpUse = new IntegerSetting(50);
        public IntegerSetting basePointsPerMonth = new IntegerSetting(40);
        public IntegerSetting maxIncursionContribution = new IntegerSetting(40);
        public IntegerSetting maxTotalIncursionContribution = new IntegerSetting(125);
        public IntegerSetting minDurationDays = new IntegerSetting(125);
        public IntegerSetting maxDurationDays = new IntegerSetting(125);

        public void loadFromJSON(@NotNull JSONObject json) {
            isEnabled.set(json, "isEnabled");
            pointsPerShiftJumpUse.set(json, "pointsPerShiftJumpUse");
            basePointsPerMonth.set(json, "basePointsPerMonth");
            maxIncursionContribution.set(json, "maxIncursionContribution");
            maxTotalIncursionContribution.set(json, "maxTotalIncursionContribution");
            minDurationDays.set(json, "minDurationDays");
            maxDurationDays.set(json, "maxDurationDays");
        }
    }

    public final ShiftJumpSettings shiftJump = new ShiftJumpSettings();
    public final ShipSystemSettings shipSystem = new ShipSystemSettings();
    public final IncursionSettings incursions = new IncursionSettings();

    public void loadFromJSON(@NotNull JSONObject json) {
        JSONObject shiftJumpJSON = json.optJSONObject("shiftJump");
        if (shiftJumpJSON != null)
            shiftJump.loadFromJSON(shiftJumpJSON);

        JSONObject shipSystemJSON = json.optJSONObject("shipSystem");
        if (shipSystemJSON != null)
            shipSystem.loadFromJSON(shipSystemJSON);

        JSONObject shiftDriveEventJSON = json.optJSONObject("incursions");
        if (shiftDriveEventJSON != null)
            incursions.loadFromJSON(shiftDriveEventJSON);
    }
}
