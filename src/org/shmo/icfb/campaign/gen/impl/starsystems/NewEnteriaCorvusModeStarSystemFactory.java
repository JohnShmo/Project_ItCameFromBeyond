package org.shmo.icfb.campaign.gen.impl.starsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.BaseTiledTerrain;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin;
import org.shmo.icfb.campaign.IcfbFactions;
import org.shmo.icfb.campaign.IcfbPlanets;
import org.shmo.icfb.campaign.gen.StarSystemFactory;
import org.shmo.icfb.utilities.ShmoGenUtils;

import java.awt.*;

public class NewEnteriaCorvusModeStarSystemFactory implements StarSystemFactory {
    public StarSystemAPI createStarSystem(SectorAPI sector, String name, float x, float y) {
        final Color systemLightColor = new Color(255,230,230);

        // Star System
        StarSystemAPI system = sector.createStarSystem(name);
        system.setType(StarSystemGenerator.StarSystemType.SINGLE);
        system.getLocation().set(x, y);
        system.setEnteredByPlayer(true);
        system.setLightColor(systemLightColor);
        system.setProcgen(false);
        createStar(system);
        createStableLocations(system);

        // Planets
        createLuminaru(system);
        createLorelai(system);
        createHeidi(system);

        // Misc
        createNebula(system);
        createJumpPoints(system);
        ShmoGenUtils.generateHyperspace(system);

        return system;
    }

    private static void createStableLocations(StarSystemAPI system) {
        PlanetAPI newEnteriaStar = system.getStar();
        SectorEntityToken stableLocation1 = system.addCustomEntity(
                null,
                null,
                "sensor_array_makeshift",
                IcfbFactions.BOUNDLESS.getId()
        );
        stableLocation1.setCircularOrbitPointingDown(newEnteriaStar, 55 + 60, 2800, 80);
        SectorEntityToken stableLocation2 = system.addCustomEntity(
                null,
                null,
                "comm_relay",
                IcfbFactions.BOUNDLESS.getId()
        );
        stableLocation2.setCircularOrbitPointingDown(newEnteriaStar, 245-60, 3800, 120);
    }

    private static void createStar(StarSystemAPI system) {
        IcfbPlanets.NEW_ENTERIA.STAR.registerPlanet(system);

        system.initStar(
                IcfbPlanets.NEW_ENTERIA.STAR.getId(),
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
                IcfbPlanets.NEW_ENTERIA.HEIDI.getId() + "_jump",
                "Heidi Jump-point"
        );
        SectorEntityToken heidi = IcfbPlanets.NEW_ENTERIA.HEIDI.getPlanet();
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
        IcfbPlanets.NEW_ENTERIA.LUMINARU.registerPlanet(system);
        IcfbPlanets.NEW_ENTERIA.LUMINARU_MAJOR.registerPlanet(system);
        IcfbPlanets.NEW_ENTERIA.LUMINARU_MINOR.registerPlanet(system);

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
                IcfbPlanets.NEW_ENTERIA.LUMINARU.getId(),
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
                IcfbPlanets.NEW_ENTERIA.LUMINARU_MAJOR.getId(),
                luminaru,
                "Luminaru Major",
                "lava",
                angle + 200f,
                luminaruMajorRadius,
                luminaruMajorOrbitDistance,
                luminaruMajorOrbitDays
        );

        system.addPlanet(
                IcfbPlanets.NEW_ENTERIA.LUMINARU_MINOR.getId(),
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

    private static void createLorelai(StarSystemAPI system) {
        IcfbPlanets.NEW_ENTERIA.LORELAI.registerPlanet(system);

        final PlanetAPI star = system.getStar();
        final float orbitDistance = 5600;
        final float orbitDays = 200;
        final float angle = 900;
        final float radius = 200;

        PlanetAPI planet = system.addPlanet(
                IcfbPlanets.NEW_ENTERIA.LORELAI.getId(),
                star,
                "Lorelai",
                "terran-eccentric",
                angle,
                radius,
                orbitDistance,
                orbitDays
        );
        planet.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "volturn"));
        planet.getSpec().setGlowColor(Color.WHITE);
        planet.getSpec().setUseReverseLightForGlow(true);
        planet.applySpecChanges();

        SectorEntityToken magField = system.addTerrain(
                Terrain.MAGNETIC_FIELD,
                new MagneticFieldTerrainPlugin.MagneticFieldParams(
                        planet.getRadius() + 300f,
                        (planet.getRadius() + 300f) / 2f,
                        planet,
                        planet.getRadius() + 50f,
                        planet.getRadius() + 50f + 350f,
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
        magField.setCircularOrbit(planet,0,0,100);
    }

    private static void createHeidi(StarSystemAPI system) {
        IcfbPlanets.NEW_ENTERIA.HEIDI.registerPlanet(system);

        final PlanetAPI star = system.getStar();
        final float orbitDistance = 2800;
        final float orbitDays = 80;
        final float angle = 245+60;
        final float radius = 140;

        PlanetAPI heidi = system.addPlanet(
                IcfbPlanets.NEW_ENTERIA.HEIDI.getId(),
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
        final PlanetAPI luminaru = IcfbPlanets.NEW_ENTERIA.LUMINARU.getPlanet();
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
