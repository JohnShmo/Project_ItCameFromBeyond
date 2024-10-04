package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.DefenderDataOverride;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.util.Misc;
import org.magiclib.campaign.MagicCaptainBuilder;
import org.magiclib.campaign.MagicFleetBuilder;
import org.magiclib.util.MagicCampaign;
import org.shmo.icfb.ItCameFromBeyond;

import java.util.*;

public class IcfbEntities {

    private static void setContainingSystem(SectorAPI sector, String containingSystemKey, StarSystemAPI system) {
        sector.getMemoryWithoutUpdate().set(containingSystemKey, system);
    }

    private static StarSystemAPI getContainingSystem(SectorAPI sector, String containingSystemKey) {
        return (StarSystemAPI) sector.getMemoryWithoutUpdate().get(containingSystemKey);
    }

    private static SectorEntityToken getEntity(SectorAPI sector, String containingSystemKey, String entityId) {
        StarSystemAPI system = getContainingSystem(sector, containingSystemKey);
        if (system == null)
            return null;
        return system.getEntityById(entityId);
    }

    public static class WingsOfEnteria {
        public static final String ID = "icfb_wings_of_enteria";
        private static final String CONTAINING_SYSTEM_KEY = "$" + ID + ":containingSystem";

        public static StarSystemAPI getContainingSystem() {
            return IcfbEntities.getContainingSystem(Global.getSector(), CONTAINING_SYSTEM_KEY);
        }

        public static SectorEntityToken getEntity() {
            return IcfbEntities.getEntity(Global.getSector(), CONTAINING_SYSTEM_KEY, ID);
        }

        public static SectorEntityToken createEntity(SectorAPI sector, SectorEntityToken orbitFocus, float orbitDistance, float orbitDays) {

            ItCameFromBeyond.Log.info("- Generating Wings of Enteria...");

            final SectorEntityToken entity = orbitFocus.getContainingLocation().addCustomEntity(
                    ID,
                    "Wings of Enteria",
                    ID,
                    Factions.NEUTRAL
            );
            entity.setCircularOrbit(orbitFocus, 90, orbitDistance, orbitDays);
            entity.setCustomDescriptionId(ID);
            entity.getMemoryWithoutUpdate().set(MemFlags.STORY_CRITICAL, true);
            setContainingSystem(sector, CONTAINING_SYSTEM_KEY, orbitFocus.getStarSystem());

            return entity;
        }
    }

    public static class ChariotOfHope {
        public static final String ID = "icfb_chariot_of_hope";
        private static final String CONTAINING_SYSTEM_KEY = "$" + ID + ":containingSystem";

        public static StarSystemAPI getContainingSystem() {
            return IcfbEntities.getContainingSystem(Global.getSector(), CONTAINING_SYSTEM_KEY);
        }

        public static SectorEntityToken getEntity() {
            return IcfbEntities.getEntity(Global.getSector(), CONTAINING_SYSTEM_KEY, ID);
        }

        public static SectorEntityToken createEntity(SectorAPI sector, SectorEntityToken orbitFocus, float angle, float orbitDistance, float orbitDays) {

            ItCameFromBeyond.Log.info("- Generating Chariot of Hope...");

            StarSystemAPI system = orbitFocus.getStarSystem();

            DerelictShipEntityPlugin.DerelictShipData derelictData = DerelictShipEntityPlugin.createVariant(
                    "venture_Exploration", StarSystemGenerator.random, 0
            );
            derelictData.ship.condition = ShipRecoverySpecial.ShipCondition.GOOD;
            derelictData.ship.shipName = "Chariot of Hope";
            derelictData.ship.pruneWeapons = true;
            derelictData.ship.addDmods = true;
            derelictData.ship.nameAlwaysKnown = true;
            SectorEntityToken entity = system.addCustomEntity(
                    ID,
                    null,
                    "wreck",
                    Factions.NEUTRAL,
                    derelictData
            );
            entity.setCircularOrbitPointingDown(orbitFocus, angle, orbitDistance, orbitDays);
            entity.setDiscoverable(true);
            entity.setDiscoveryXP(2500f);

            SalvageSpecialAssigner.ShipRecoverySpecialCreator creator = new SalvageSpecialAssigner.ShipRecoverySpecialCreator(
                    null,
                    0,
                    0,
                    false,
                    null,
                    null
            );
            ShipRecoverySpecial.ShipRecoverySpecialData specialData
                    = (ShipRecoverySpecial.ShipRecoverySpecialData)creator.createSpecial(entity, null);
            specialData.notNowOptionExits = true;
            Misc.setSalvageSpecial(entity, specialData);
            entity.addTag(Tags.NOT_RANDOM_MISSION_TARGET);

            DefenderDataOverride defenderDataOverride = new DefenderDataOverride(
                    Factions.OMEGA,
                    1f,
                    300f,
                    300f
            );
            Misc.setDefenderOverride(entity, defenderDataOverride);

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

            entity.getMemoryWithoutUpdate().set("$defenderFleet", defenders);
            entity.getMemoryWithoutUpdate().set("$hasDefenders", true);
            entity.getMemoryWithoutUpdate().set("$defenderFleetDefeated", false);
            entity.getMemoryWithoutUpdate().set("$canNotSalvage", true);
            entity.getMemoryWithoutUpdate().set(IcfbMemFlags.IS_CHARIOT_OF_HOPE, true);

            setContainingSystem(sector, CONTAINING_SYSTEM_KEY, system);

            return entity;
        }
    }
}
