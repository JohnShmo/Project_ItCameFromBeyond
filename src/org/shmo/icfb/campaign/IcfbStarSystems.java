package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.*;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.utilities.ShmoGenUtils;

import java.awt.*;

public class IcfbStarSystems {

    private static StarSystemAPI getSystem(SectorAPI sector, String name) {
        return sector.getStarSystem(name);
    }

    private static SectorEntityToken getSystemEntity(SectorAPI sector, String systemName, String id) {
        StarSystemAPI system = getSystem(sector, systemName);
        if (system == null)
            return null;
        return system.getEntityById(id);
    }

    private static PlanetAPI getSystemPlanet(SectorAPI sector, String systemName, String id) {
        try {
            return (PlanetAPI) getSystemEntity(sector, systemName, id);
        } catch (Exception unused) {
            return null;
        }
    }

    public static class NewEnteria {
        public static final String NAME = "New Enteria";
        public static final String STAR = "icfb_new_enteria";
        public static final String LUMINARU = "icfb_luminaru";              // Gas giant
        public static final String LUMINARU_MAJOR = "icfb_luminaru_major";  // - Moon 1
        public static final String LUMINARU_MINOR = "icfb_luminaru_minor";  // - Moon 2
        public static final String LORELAI = "icfb_lorelai";                // Terran eccentric world
        public static final String HEIDI = "icfb_heidi";                    // Irradiated world

        public static StarSystemAPI getSystem() {
            return IcfbStarSystems.getSystem(Global.getSector(), NAME);
        }

        public static PlanetAPI getPlanet(String id) {
            return getSystemPlanet(Global.getSector(), NAME, id);
        }

        public static SectorEntityToken getEntity(String id) {
            return getSystemEntity(Global.getSector(), NAME, id);
        }

        public static StarSystemAPI createSystem(SectorAPI sector, float x, float y) {
            final Color systemLightColor = new Color(255,230,230);

            ItCameFromBeyond.Log.info("- Generating New Enteria...");

            // Star System
            StarSystemAPI system = sector.createStarSystem(NAME);
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
                    IcfbFactions.Boundless.ID
            );
            stableLocation1.setCircularOrbitPointingDown(newEnteriaStar, 55 + 60, 2800, 80);
            SectorEntityToken stableLocation2 = system.addCustomEntity(
                    null,
                    null,
                    "comm_relay",
                    IcfbFactions.Boundless.ID
            );
            stableLocation2.setCircularOrbitPointingDown(newEnteriaStar, 245-60, 3800, 120);
        }

        private static void createStar(StarSystemAPI system) {
            system.initStar(
                    IcfbStarSystems.NewEnteria.STAR,
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
                    IcfbStarSystems.NewEnteria.HEIDI + "_jump",
                    "Heidi Jump-point"
            );
            SectorEntityToken heidi = system.getEntityById(IcfbStarSystems.NewEnteria.HEIDI);
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
                    IcfbStarSystems.NewEnteria.LUMINARU,
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
                    IcfbStarSystems.NewEnteria.LUMINARU_MAJOR,
                    luminaru,
                    "Luminaru Major",
                    "lava",
                    angle + 200f,
                    luminaruMajorRadius,
                    luminaruMajorOrbitDistance,
                    luminaruMajorOrbitDays
            );

            system.addPlanet(
                    IcfbStarSystems.NewEnteria.LUMINARU_MINOR,
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
            final PlanetAPI star = system.getStar();
            final float orbitDistance = 5600;
            final float orbitDays = 200;
            final float angle = 900;
            final float radius = 200;

            PlanetAPI huxley = system.addPlanet(
                    IcfbStarSystems.NewEnteria.LORELAI,
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
        }

        private static void createHeidi(StarSystemAPI system) {
            final PlanetAPI star = system.getStar();
            final float orbitDistance = 2800;
            final float orbitDays = 80;
            final float angle = 245+60;
            final float radius = 140;

            PlanetAPI heidi = system.addPlanet(
                    IcfbStarSystems.NewEnteria.HEIDI,
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
            final PlanetAPI luminaru = (PlanetAPI)system.getEntityById(IcfbStarSystems.NewEnteria.LUMINARU);
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
    public static class Kato {
        public static final String NAME = "Kato";
        public static final String BLACK_HOLE = "icfb_kato";
        public static final String ALICE = "icfb_alice";
        public static final String MOLLY = "icfb_molly";

        public static StarSystemAPI getSystem() {
            return IcfbStarSystems.getSystem(Global.getSector(), NAME);
        }

        public static PlanetAPI getPlanet(String id) {
            return getSystemPlanet(Global.getSector(), NAME, id);
        }

        public static SectorEntityToken getEntity(String id) {
            return getSystemEntity(Global.getSector(), NAME, id);
        }

        public static StarSystemAPI createSystem(SectorAPI sector, float x, float y) {
            final Color systemLightColor = new Color(245,200,200);

            ItCameFromBeyond.Log.info("- Generating Kato...");

            // Star System
            StarSystemAPI system = sector.createStarSystem(NAME);
            system.setBackgroundTextureFilename("graphics/backgrounds/background1.jpg");
            system.getLocation().set(x, y);
            system.setEnteredByPlayer(false);
            system.setLightColor(systemLightColor);
            system.setProcgen(false);
            createBlackHole(system);
            createAlice(system);

            // Misc
            createProcGen(system);
            ShmoGenUtils.generateHyperspace(system);

            return system;
        }

        private static void createAlice(StarSystemAPI system) {
            PlanetAPI alice = system.addPlanet(
                    IcfbStarSystems.Kato.ALICE,
                    system.getStar(),
                    "Alice",
                    "cryovolcanic",
                    180,
                    92,
                    3500,
                    120
            );
            PlanetAPI molly = system.addPlanet(
                    IcfbStarSystems.Kato.MOLLY,
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
                    IcfbStarSystems.Kato.BLACK_HOLE,
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
}
