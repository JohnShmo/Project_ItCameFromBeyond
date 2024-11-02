package org.shmo.icfb;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import org.shmo.icfb.utilities.ShmoMath;

import java.util.List;

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

    public static float computeSupplyCostForCRRecovery(float crLoss, CampaignFleetAPI fleet) {
        List<FleetMemberAPI> members = fleet.getFleetData().getMembersListCopy();
        float totalSupplies = 0;
        for (FleetMemberAPI member : members) {
            final float deploymentCostSupplies = member.getDeploymentCostSupplies();
            final float deploymentCostCR = member.getDeployCost() * 100f;
            final float suppliesPerCR = deploymentCostSupplies / deploymentCostCR;
            totalSupplies += suppliesPerCR * (crLoss * 100f);
        }
        return totalSupplies;
    }
}
