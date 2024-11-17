package org.shmo.icfb;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.utilities.ShmoMath;

import java.util.*;

public class IcfbMisc {
    public static Set<String> setOf(String... strings) {
        final Set<String> set = new HashSet<>();
        Collections.addAll(set, strings);
        return set;
    }

    public static float computeShiftJumpCRPenalty(
            IcfbSettings.ShiftJumpSettings.CRPenaltyCurve curve,
            float t
    ) {
        if (curve == null)
            return t;
        switch (curve) {
            case FAST:
                return ShmoMath.easeInQuad(t);
            case MEDIUM:
                return ShmoMath.easeInQuart(t);
            case SLOW:
                return ShmoMath.easeInExpo(t);
            default:
                return t;
        }
    }

    public static String getQuestIntelString(String id) {
        return com.fs.starfarer.api.Global.getSettings().getString(
                "icfb_questIntel",
                id
        );
    }

    public static float computeSupplyCostForCRRecovery(CampaignFleetAPI fleet, float crLoss) {
        List<FleetMemberAPI> members = fleet.getFleetData().getMembersListCopy();
        float totalSupplies = 0;
        for (FleetMemberAPI member : members) {
            totalSupplies += computeSupplyCostForCRRecovery(member, crLoss);
        }
        return totalSupplies;
    }

    public static float computeSupplyCostForCRRecovery(FleetMemberAPI fleetMember, float crLoss) {
        final float deploymentCostSupplies = fleetMember.getDeploymentCostSupplies();
        final float deploymentCostCR = fleetMember.getDeployCost() * 100f;
        final float suppliesPerCR = deploymentCostSupplies / deploymentCostCR;
        return suppliesPerCR * (crLoss * 100f);
    }

    public interface SystemPickerPredicate {
        boolean isValid(StarSystemAPI starSystem);
    }

    public static StarSystemAPI pickSystem(SystemPickerPredicate predicate) {
        Random random = Misc.random;
        final List<StarSystemAPI> allSystems = Global.getSector().getStarSystems();
        final List<StarSystemAPI> starSystems = new ArrayList<>();

        for (StarSystemAPI starSystem : allSystems) {
            if (predicate.isValid(starSystem) && starSystem.getStar() != null)
                starSystems.add(starSystem);
        }

        StarSystemAPI pickedSystem = null;
        if (!starSystems.isEmpty()) {
            final int pickedIndex = random.nextInt(starSystems.size());
            pickedSystem = starSystems.get(pickedIndex);
        }

        return pickedSystem;
    }

    public static StarSystemAPI pickSystem(final boolean procGenOnly) {
        return pickSystem(new SystemPickerPredicate() {
            @Override
            public boolean isValid(StarSystemAPI starSystem) {
                return !(procGenOnly && !starSystem.isProcgen());
            }
        });
    }

    public static StarSystemAPI pickSystem() {
        return pickSystem(false);
    }

    public interface MarketPickerPredicate {
        boolean isValid(MarketAPI market);
    }

    public static MarketAPI pickMarket(MarketPickerPredicate predicate) {
        Random random = Misc.random;

        final List<MarketAPI> allMarkets = Global.getSector().getEconomy().getMarketsCopy();
        final List<MarketAPI> markets = new ArrayList<>();
        for (MarketAPI market : allMarkets) {
            if (predicate.isValid(market))
                markets.add(market);
        }

        MarketAPI pickedMarket = null;
        if (!markets.isEmpty()) {
            final int pickedIndex = random.nextInt(markets.size());
            pickedMarket = markets.get(pickedIndex);
        }

        return pickedMarket;
    }

    public static MarketAPI pickMarket(final int minSize, final Set<String> factionIdSet) {
        return pickMarket(new MarketPickerPredicate() {
            @Override
            public boolean isValid(MarketAPI market) {
                return !market.isHidden()
                        && market.getSize() >= minSize
                        && factionIdSet.contains(market.getFactionId());
            }
        });
    }

    public static MarketAPI pickMarket(int minSize, String... factionIds) {
        final Set<String> factionIdSet = new HashSet<>();
        Collections.addAll(factionIdSet, factionIds);
        return pickMarket(minSize, factionIdSet);
    }

    public static MarketAPI pickMarket(final int minSize) {
        return pickMarket(new MarketPickerPredicate() {
            @Override
            public boolean isValid(MarketAPI market) {
                return !market.isHidden()
                        && market.getSize() >= minSize;
            }
        });
    }

    public static MarketAPI pickMarket() {
        return pickMarket(0);
    }

    public static PlanetAPI pickPlanet(StarSystemAPI system) {
        final List<PlanetAPI> allPlanets = system.getPlanets();
        final List<PlanetAPI> planets = new ArrayList<>();
        for (PlanetAPI planet : allPlanets) {
            if (planet.isStar() || planet.getTypeId().equals("black_hole"))
                continue;
            planets.add(planet);
        }

        PlanetAPI picked = null;
        if (!planets.isEmpty()) {
            final int index = Misc.random.nextInt(planets.size());
            picked = planets.get(index);
        }
        return picked;
    }
}
