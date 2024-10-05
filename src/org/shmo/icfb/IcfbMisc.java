package org.shmo.icfb;

import org.shmo.icfb.utilities.ShmoMath;

public class IcfbMisc {
    public static float computeShiftJumpCRPenalty(
            IcfbSettings.ShiftJumpSettings.CRPenaltyCurve curve,
            float t
    ) {
        if (curve == null)
            return t;
        switch (curve) {
            case FAST: return ShmoMath.easeInQuad(t);
            case MEDIUM: return ShmoMath.easeInQuart(t);
            case SLOW: return ShmoMath.easeInExpo(t);
            default: return t;
        }
    }

    public static String getQuestIntelString(String id) {
        return com.fs.starfarer.api.Global.getSettings().getString(
                "icfb_questIntel",
                id
        );
    }
}
