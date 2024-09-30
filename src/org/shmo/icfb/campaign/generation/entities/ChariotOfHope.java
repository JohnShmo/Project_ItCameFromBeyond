package org.shmo.icfb.campaign.generation.entities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.DefenderDataOverride;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.util.Misc;
import org.magiclib.campaign.MagicCaptainBuilder;
import org.magiclib.campaign.MagicFleetBuilder;
import org.magiclib.util.MagicCampaign;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondEntities;

import java.util.HashMap;
import java.util.Map;

public class ChariotOfHope {
    private static final String CONTAINING_SYSTEM_KEY = "$" + ItCameFromBeyondEntities.CHARIOT_OF_HOPE + ":containingSystem";

    public static StarSystemAPI getContainingSystem() {
        return (StarSystemAPI)Global.getSector().getMemoryWithoutUpdate().get(CONTAINING_SYSTEM_KEY);
    }

    public static SectorEntityToken generate(SectorAPI sector, SectorEntityToken orbitFocus, float angle, float orbitDistance, float orbitDays) {
        StarSystemAPI system = orbitFocus.getStarSystem();

        DerelictShipEntityPlugin.DerelictShipData derelictData = DerelictShipEntityPlugin.createVariant(
                "venture_Exploration", StarSystemGenerator.random, 0
        );
        derelictData.ship.condition = ShipRecoverySpecial.ShipCondition.GOOD;
        derelictData.ship.shipName = "Hope's Chariot";
        derelictData.ship.pruneWeapons = true;
        derelictData.ship.addDmods = true;
        SectorEntityToken derelict = system.addCustomEntity(ItCameFromBeyondEntities.CHARIOT_OF_HOPE, null,"wreck", Factions.NEUTRAL, derelictData);
        derelict.setCircularOrbitPointingDown(orbitFocus, angle, orbitDistance, orbitDays);
        SalvageSpecialAssigner.ShipRecoverySpecialCreator creator = new SalvageSpecialAssigner.ShipRecoverySpecialCreator(null, 0, 0, false, null, null);
        Misc.setSalvageSpecial(derelict, creator.createSpecial(derelict, null));
        derelict.addTag(Tags.NOT_RANDOM_MISSION_TARGET);

        DefenderDataOverride defenderDataOverride = new DefenderDataOverride(
                Factions.OMEGA,
                1f,
                300f,
                300f
        );
        Misc.setDefenderOverride(derelict, defenderDataOverride);

        MagicFleetBuilder defenderBuilder = MagicCampaign.createFleetBuilder();
        MagicCaptainBuilder captainBuilder = MagicCampaign.createCaptainBuilder(Factions.OMEGA);
        captainBuilder.setAICoreType(Commodities.BETA_CORE);
        captainBuilder.setIsAI(true);
        captainBuilder.setPersonality("reckless");
        captainBuilder.setLevel(5);
        defenderBuilder.setFleetFaction(Factions.OMEGA);
        defenderBuilder.setCaptain(captainBuilder.create());
        defenderBuilder.setSpawnLocation(null);
        defenderBuilder.setFleetName("Automated Defenders");
        defenderBuilder.setFlagshipVariant("shard_left_Defense");
        Map<String, Integer> supportFleet = new HashMap<>();
        supportFleet.put("shard_right_Attack", 1);
        supportFleet.put("lumen_Standard", 2);
        supportFleet.put("fulgent_Assault", 1);
        supportFleet.put("fulgent_Support", 1);
        supportFleet.put("brilliant_Standard", 1);
        defenderBuilder.setSupportFleet(supportFleet);

        CampaignFleetAPI defenders = defenderBuilder.create();

        derelict.getMemoryWithoutUpdate().set("$defenderFleet", defenders);
        derelict.getMemoryWithoutUpdate().set("$hasDefenders", true);
        derelict.getMemoryWithoutUpdate().set("$defenderFleetDefeated", false);
        derelict.getMemoryWithoutUpdate().set("$canNotSalvage", true);

        sector.getMemoryWithoutUpdate().set(CONTAINING_SYSTEM_KEY, system);

        return derelict;
    }
}
