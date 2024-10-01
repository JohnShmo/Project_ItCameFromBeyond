package org.shmo.icfb.campaign.generation.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.BaseTiledTerrain;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin;
import org.magiclib.util.MagicCampaign;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.ItCameFromBeyondGen;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondFactions;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondMarkets;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondStarSystems;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class NewEnteria {

    public static StarSystemAPI generate(SectorAPI sector, float x, float y) {
        final Color systemLightColor = new Color(255,230,230);

        ItCameFromBeyond.Log.info("- Generating New Enteria...");

        // Star System
        StarSystemAPI system = sector.createStarSystem("New Enteria");
        system.getLocation().set(x, y);
        system.setEnteredByPlayer(true);
        system.setLightColor(systemLightColor);
        system.setProcgen(false);
        createStar(system);
        createStableLocations(system);

        // Planets
        createLuminaru(system);
        createLorelai(sector, system);
        createHeidi(system);

        // Misc
        createNebula(system);
        createJumpPoints(system);
        ItCameFromBeyondGen.generateHyperspace(system);

        return system;
    }

    private static void createStableLocations(StarSystemAPI system) {
        PlanetAPI newEnteriaStar = system.getStar();
        SectorEntityToken stableLocation1 = system.addCustomEntity(
                null,
                null,
                "sensor_array_makeshift",
                ItCameFromBeyondFactions.BOUNDLESS
        );
        stableLocation1.setCircularOrbitPointingDown(newEnteriaStar, 55 + 60, 2800, 80);
        SectorEntityToken stableLocation2 = system.addCustomEntity(
                null,
                null,
                "comm_relay",
                ItCameFromBeyondFactions.BOUNDLESS
        );
        stableLocation2.setCircularOrbitPointingDown(newEnteriaStar, 245-60, 3800, 120);
    }

    private static void createStar(StarSystemAPI system) {
        system.initStar(
                ItCameFromBeyondStarSystems.NewEnteria.STAR,
                "star_red_dwarf",
                600f,
                300f,
                10f,
                1f,
                3f
        );
    }

    private static void createJumpPoints(StarSystemAPI system) {
        JumpPointAPI heidiJumpPoint = Global.getFactory().createJumpPoint(
                ItCameFromBeyondStarSystems.NewEnteria.HEIDI + "_jump",
                "Heidi Jump-point"
        );
        SectorEntityToken heidi = system.getEntityById(ItCameFromBeyondStarSystems.NewEnteria.HEIDI);
        heidiJumpPoint.setCircularOrbit(
                system.getStar(),
                245+60,
                heidi.getCircularOrbitRadius() + heidi.getRadius() + 300f,
                heidi.getCircularOrbitPeriod());
        heidiJumpPoint.setRelatedPlanet(heidi);
        heidiJumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(heidiJumpPoint);
    }

    private static void createLuminaru(StarSystemAPI system) {
        final PlanetAPI star = system.getStar();
        final float angle = 100;
        final float luminaruOrbitDistance = 12000;
        final float luminaruOrbitDays = 500;
        final float luminaruRadius = 490;
        final float luminaruMajorRadius = 100;
        final float luminaruMajorOrbitDistance = 1540;
        final float luminaruMajorOrbitDays = 28;
        final float luminaruMajorRingSize = 600;
        final float luminaruMajorRingOrbitDays = 20;
        final float luminaruMinorRadius = 90;
        final float luminaruMinorOrbitDistance = 1500f + (luminaruMajorRingSize*2) + 100;
        final float luminaruMinorOrbitDays = 60;

        final PlanetAPI luminaru = system.addPlanet(
                ItCameFromBeyondStarSystems.NewEnteria.LUMINARU,
                star,
                "Luminaru",
                "gas_giant",
                angle,
                luminaruRadius,
                luminaruOrbitDistance,
                luminaruOrbitDays
        );
        luminaru.getSpec().setCloudColor(new Color(150, 80, 90));
        luminaru.getSpec().setAtmosphereColor(new Color(150, 100, 50));
        luminaru.applySpecChanges();

        final PlanetAPI luminaruMajor = system.addPlanet(
                ItCameFromBeyondStarSystems.NewEnteria.LUMINARU_MAJOR,
                luminaru,
                "Luminaru Major",
                "lava",
                angle + 200f,
                luminaruMajorRadius,
                luminaruMajorOrbitDistance,
                luminaruMajorOrbitDays
        );

        system.addPlanet(
                ItCameFromBeyondStarSystems.NewEnteria.LUMINARU_MINOR,
                luminaru,
                "Luminaru Minor",
                "barren-bombarded",
                angle,
                luminaruMinorRadius,
                luminaruMinorOrbitDistance,
                luminaruMinorOrbitDays
        );

        system.addAsteroidBelt(
                luminaruMajor,
                50,
                luminaruMajorRingSize,
                luminaruMajorRingSize,
                luminaruMajorRingOrbitDays * 0.75f,
                luminaruMajorRingOrbitDays
        );

        system.addRingBand(
                luminaruMajor,
                "misc",
                "rings_dust0",
                256f,
                3,
                Color.WHITE,
                luminaruMajorRingSize,
                luminaruMajorRingSize,
                luminaruMajorRingOrbitDays,
                null,
                null
        );

        system.addAsteroidBelt(
          luminaru,
          90,
          luminaruMinorOrbitDistance + luminaruMinorRadius + luminaruMajorRingSize + 100,
                luminaruMajorRingSize * 2,
          80f,
          100f
        );
        system.addRingBand(
                luminaru,
                "misc",
                "rings_dust0",
                256f,
                3,
                Color.WHITE,
                luminaruMajorRingSize * 2,
                luminaruMinorOrbitDistance + luminaruMinorRadius + luminaruMajorRingSize + 100,
                100f,
                null,
                null
        );
    }

    private static void createLorelai(SectorAPI sector, StarSystemAPI system) {
        final PlanetAPI star = system.getStar();
        final float orbitDistance = 5600;
        final float orbitDays = 200;
        final float angle = 900;
        final float radius = 200;

        PlanetAPI huxley = system.addPlanet(
                ItCameFromBeyondStarSystems.NewEnteria.LORELAI,
                star,
                "Lorelai",
                "terran-eccentric",
                angle,
                radius,
                orbitDistance,
                orbitDays
        );
        huxley.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "volturn"));
        huxley.getSpec().setGlowColor(Color.WHITE);
        huxley.getSpec().setUseReverseLightForGlow(true);
        huxley.applySpecChanges();
        huxley.setFaction(ItCameFromBeyondFactions.BOUNDLESS);

        SectorEntityToken huxleyField = system.addTerrain(
                Terrain.MAGNETIC_FIELD,
                new MagneticFieldTerrainPlugin.MagneticFieldParams(
                        huxley.getRadius() + 300f,
                        (huxley.getRadius() + 300f) / 2f,
                        huxley,
                        huxley.getRadius() + 50f,
                        huxley.getRadius() + 50f + 350f,
                        new Color(50, 20, 100, 40),
                        0.5f,
                        new Color(140, 100, 235),
                        new Color(180, 110, 210),
                        new Color(150, 140, 190),
                        new Color(140, 190, 210),
                        new Color(90, 200, 170),
                        new Color(65, 230, 160),
                        new Color(20, 220, 70)
                )
        );
        huxleyField.setCircularOrbit(huxley,0,0,100);

        MarketAPI market = MagicCampaign.addSimpleMarket(
                huxley,
                ItCameFromBeyondMarkets.LORELAI,
                "Lorelai",
                5,
                ItCameFromBeyondFactions.BOUNDLESS,
                false,
                false,
                new ArrayList<>(Arrays.asList(
                        Conditions.POPULATION_5,
                        Conditions.FARMLAND_RICH,
                        Conditions.EXTREME_WEATHER,
                        Conditions.TECTONIC_ACTIVITY,
                        Conditions.HABITABLE,
                        Conditions.ORGANICS_COMMON,
                        Conditions.ORE_MODERATE,
                        Conditions.RARE_ORE_MODERATE
                )),
                new ArrayList<>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
                        Industries.WAYSTATION,
                        Industries.PATROLHQ,
                        Industries.GROUNDDEFENSES,
                        Industries.FARMING,
                        Industries.FUELPROD,
                        Industries.MINING,
                        Industries.BATTLESTATION_MID
                )),
                true,
                true,
                true,
                true,
                false,
                false
        );
        market.setHasSpaceport(true);
        market.reapplyIndustries();
        sector.getEconomy().addMarket(market, true);
    }

    private static void createHeidi(StarSystemAPI system) {
        final PlanetAPI star = system.getStar();
        final float orbitDistance = 2800;
        final float orbitDays = 80;
        final float angle = 245+60;
        final float radius = 140;

        PlanetAPI heidi = system.addPlanet(
                ItCameFromBeyondStarSystems.NewEnteria.HEIDI,
                star,
                "Heidi",
                "irradiated",
                angle,
                radius,
                orbitDistance,
                orbitDays
        );
        heidi.setSkipForJumpPointAutoGen(true);
    }

    private static void createNebula(StarSystemAPI system) {
        final PlanetAPI star = system.getStar();
        final PlanetAPI luminaru = (PlanetAPI)system.getEntityById(ItCameFromBeyondStarSystems.NewEnteria.LUMINARU);
        SectorEntityToken nebula = system.addTerrain(Terrain.NEBULA, new BaseTiledTerrain.TileParams(
                "   xx " +
                        "  xx x" +
                        " xxxx " +
                        "xxxxxx" +
                        "  xx  " +
                        "    x ",
                6, 6, // size of the nebula grid, should match above string
                "terrain", "nebula_amber", 4, 4, null));
        nebula.getLocation().set(luminaru.getLocation().x + 1000f, luminaru.getLocation().y);
        nebula.setCircularOrbit(star, 140f, 15000, 800);

        StarSystemGenerator.addSystemwideNebula(system, StarAge.OLD);
    }
}
