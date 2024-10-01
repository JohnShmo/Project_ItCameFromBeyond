package org.shmo.icfb.campaign.generation.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.fleet.FleetAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.HiddenCacheEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.*;
import com.fs.starfarer.api.impl.campaign.procgen.themes.*;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.AICores;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageEntity;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.terrain.BaseRingTerrain;
import com.fs.starfarer.api.impl.campaign.terrain.RingSystemTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.StarCoronaTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.combat.entities.terrain.Planet;
import org.magiclib.campaign.MagicCaptainBuilder;
import org.magiclib.campaign.MagicFleetBuilder;
import org.magiclib.util.MagicCampaign;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.ItCameFromBeyondGen;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondStarSystems;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kato {
    public static StarSystemAPI generate(SectorAPI sector, float x, float y) {
        final Color systemLightColor = new Color(245,200,200);

        ItCameFromBeyond.Log.info("- Generating Kato...");

        // Star System
        StarSystemAPI system = sector.createStarSystem("Kato");
        system.setBackgroundTextureFilename("graphics/backgrounds/background1.jpg");
        system.getLocation().set(x, y);
        system.setEnteredByPlayer(false);
        system.setLightColor(systemLightColor);
        system.setProcgen(false);
        createBlackHole(system);
        createAlice(system);

        // Misc
        createProcGen(system);
        ItCameFromBeyondGen.generateHyperspace(system);

        return system;
    }

    private static void createAlice(StarSystemAPI system) {
        PlanetAPI alice = system.addPlanet(
                ItCameFromBeyondStarSystems.Kato.ALICE,
                system.getStar(),
                "Alice",
                "cryovolcanic",
                180,
                92,
                3500,
                120
        );
        PlanetAPI molly = system.addPlanet(
                ItCameFromBeyondStarSystems.Kato.MOLLY,
                alice,
                "Molly",
                "frozen2",
                212,
                55,
                500,
                20
        );

        SectorEntityToken researchStation = MiscellaneousThemeGenerator.addSalvageEntity(system, "station_research_remnant", Factions.NEUTRAL);
        researchStation.setCircularOrbitPointingDown(molly, 90, 170, 15);

        SectorEntityToken warningBeacon = system.addCustomEntity(
                "icfb_alice_warning_beacon",
                null,
                "warning_beacon",
                Factions.NEUTRAL
        );
        warningBeacon.setCircularOrbit(alice, 80, 160, 13);
    }

    private static void createBlackHole(StarSystemAPI system) {
        PlanetAPI kato = system.initStar(
                ItCameFromBeyondStarSystems.Kato.BLACK_HOLE,
                "black_hole",
                400f,
                0,
                0,
                0,
                1f
        );
        kato.getSpec().setGlowColor(new Color(255, 80, 70));
        kato.getSpec().setCoronaColor(new Color(255, 80, 70));
        kato.getSpec().setAtmosphereColor(new Color(255, 80, 70));
        kato.applySpecChanges();

        StarCoronaTerrainPlugin coronaPlugin = Misc.getCoronaFor(kato);
        if (coronaPlugin != null) {
            system.removeEntity(coronaPlugin.getEntity());
        }

        float corona = kato.getRadius();

        SectorEntityToken eventHorizon = system.addTerrain(Terrain.EVENT_HORIZON,
                new StarCoronaTerrainPlugin.CoronaParams(kato.getRadius() + corona, (kato.getRadius() + corona) / 2f,
                        kato, -10,
                        0.75f,
                        5f));
        eventHorizon.setCircularOrbit(kato, 0, 0, 100);

        float orbitRadius = 3000f;
        float bandWidth = 256f;
        float spiralFactor;
        int numBands = 12;
        for (float i = 0; i < numBands; i++) {

            float radius = orbitRadius - i * bandWidth * 0.25f - i * bandWidth * 0.1f;

            float orbitDays = radius / (30f + 10f * StarSystemGenerator.random.nextFloat());
            Color color = Color.WHITE;
            RingBandAPI visual = system.addRingBand(kato, "misc", "rings_dust0", 256f, 3, color, bandWidth,
                    radius + bandWidth / 2f, -orbitDays);

            spiralFactor = 2f + StarSystemGenerator.random.nextFloat() * 5f;
            visual.setSpiral(true);
            visual.setMinSpiralRadius(0);
            visual.setSpiralFactor(spiralFactor);
        }

        SectorEntityToken ring = system.addTerrain(Terrain.RING, new BaseRingTerrain.RingParams(orbitRadius, orbitRadius / 2f, kato, "Kato Accretion Disk"));
        ring.addTag(Tags.ACCRETION_DISK);
        if (((CampaignTerrainAPI)ring).getPlugin() instanceof RingSystemTerrainPlugin) {
            ((RingSystemTerrainPlugin)((CampaignTerrainAPI)ring).getPlugin()).setNameForTooltip("Accretion Disk");
        }

        ring.setCircularOrbit(kato, 0, 0, -100);
    }

    private static void createProcGen(StarSystemAPI system) {
        StarSystemGenerator.addOrbitingEntities(system, system.getStar(), StarAge.OLD, 3, 6, 8000, 2, false);
        StarSystemGenerator.addStableLocations(system, 2);
        StarSystemGenerator.addSystemwideNebula(system, StarAge.YOUNG);
    }
}
