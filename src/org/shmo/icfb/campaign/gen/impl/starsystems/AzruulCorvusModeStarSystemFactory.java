package org.shmo.icfb.campaign.gen.impl.starsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.SectorProcGen;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SectorThemeGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.BaseTiledTerrain;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.StarCoronaTerrainPlugin;
import com.fs.starfarer.api.loading.TerrainSpecAPI;
import com.fs.starfarer.api.util.Misc;
import org.magiclib.util.MagicCampaign;
import org.shmo.icfb.campaign.IcfbFactions;
import org.shmo.icfb.campaign.IcfbPlanets;
import org.shmo.icfb.campaign.gen.StarSystemFactory;
import org.shmo.icfb.utilities.ShmoGenUtils;

import java.awt.*;

public class AzruulCorvusModeStarSystemFactory implements StarSystemFactory {
    @Override
    public StarSystemAPI createStarSystem(SectorAPI sector, String name, float x, float y) {
        final Color systemLightColor = new Color(245,200,200);

        StarSystemAPI system = sector.createStarSystem(name);
        system.setType(StarSystemGenerator.StarSystemType.BINARY_CLOSE);
        system.getLocation().set(x, y);
        system.setEnteredByPlayer(true);
        system.setLightColor(systemLightColor);
        system.setProcgen(false);
        createStars(system);
        createStableLocations(system);

        // Planets
        createPlanets(system);

        // Misc
        createNebula(system);
        createJumpPoints(system);
        ShmoGenUtils.generateHyperspace(system);

        return system;
    }

    private static void createStableLocations(StarSystemAPI system) {
        SectorEntityToken center = system.getCenter();
        SectorEntityToken stableLocation1 = system.addCustomEntity(
                null,
                null,
                "sensor_array",
                IcfbFactions.BOUNDLESS.getId()
        );
        stableLocation1.setCircularOrbitPointingDown(center, 55 + 60, 4000, 100);
        SectorEntityToken stableLocation2 = system.addCustomEntity(
                null,
                null,
                "comm_relay_makeshift",
                IcfbFactions.BOUNDLESS.getId()
        );
        stableLocation2.setCircularOrbitPointingDown(center, 245-60, 6000, 200);
    }

    private static void createJumpPoints(StarSystemAPI system) {
        MagicCampaign.addJumpPoint(
                system.getId() + "_jump_point_inner",
                "Inner Jump Point",
                null,
                system.getCenter(),
                300,
                3100,
                68
        );

        MagicCampaign.addJumpPoint(
                system.getId() + "_jump_point_outer",
                "Outer Jump Point",
                null,
                system.getCenter(),
                -300,
                7500,
                300
        );
    }

    private static void createStars(StarSystemAPI system) {
        IcfbPlanets.AZRUUL_STAR_1.registerPlanet(system);
        IcfbPlanets.AZRUUL_STAR_2.registerPlanet(system);

        final SectorEntityToken center = system.initNonStarCenter();
        final PlanetAPI primary = system.addPlanet(
                IcfbPlanets.AZRUUL_STAR_1.getId(),
                center,
                "Azure Nexus",
                StarTypes.BLUE_GIANT,
                90,
                980,
                100,
                10
        );
        final PlanetAPI secondary = system.addPlanet(
                IcfbPlanets.AZRUUL_STAR_2.getId(),
                center,
                "Tura",
                StarTypes.BROWN_DWARF,
                -90,
                400,
                1800,
                40
        );
        system.setStar(primary);
        system.setSecondary(secondary);
        system.addCorona(
                primary,
                primary.getRadius() / 2,
                10,
                0.5f,
                1f
        );
        system.addCorona(
                secondary,
                secondary.getRadius() / 4,
                5,
                0.25f,
                0.5f
        );
    }

    private static void createPlanets(StarSystemAPI system) {
        IcfbPlanets.AURUCELLO.registerPlanet(system);
        IcfbPlanets.CELADON.registerPlanet(system);
        IcfbPlanets.PANGEA.registerPlanet(system);
        IcfbPlanets.LANTERNIA.registerPlanet(system);

        system.addPlanet(
                IcfbPlanets.AURUCELLO.getId(),
                system.getCenter(),
                "Aurucello",
                "barren-desert",
                100,
                90,
                3500,
                80
        );

        system.addPlanet(
                IcfbPlanets.CELADON.getId(),
                system.getCenter(),
                "Celadon",
                Planets.BARREN_VENUSLIKE,
                -200,
                105,
                4750,
                140
        );

        system.addAsteroidBelt(
                system.getCenter(),
                900,
                5700,
                130,
                180,
                210
        );

        PlanetAPI pangea = system.addPlanet(
                IcfbPlanets.PANGEA.getId(),
                system.getCenter(),
                "Pangea",
                Planets.PLANET_TERRAN,
                0,
                180,
                6900,
                240
        );
        pangea.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "volturn"));
        pangea.getSpec().setGlowColor(Color.WHITE);
        pangea.getSpec().setUseReverseLightForGlow(true);
        pangea.applySpecChanges();
        SectorEntityToken magField = system.addTerrain(
                Terrain.MAGNETIC_FIELD,
                new MagneticFieldTerrainPlugin.MagneticFieldParams(
                        pangea.getRadius() + 400,
                        (pangea.getRadius() + 400) * 0.5f,
                        pangea,
                        pangea.getRadius() + 50,
                        pangea.getRadius() + 450,
                        new Color(80, 40, 100, 40),
                        0.65f,
                        new Color(140, 100, 235),
                        new Color(180, 110, 210),
                        new Color(150, 140, 190),
                        new Color(140, 190, 210),
                        new Color(90, 200, 170),
                        new Color(65, 230, 160),
                        new Color(20, 220, 70)
                )
        );
        magField.setCircularOrbit(pangea, 0, 0, -100);
        final float shadeOrbitRadius = pangea.getRadius() + 450;
        final float shadeBaseAngle = 180;
        final float shadeAngleOffset = 30;
        final float shadeOrbitDays = pangea.getCircularOrbitPeriod();
        SectorEntityToken shade1 = system.addCustomEntity(
                pangea.getId() + "_shade_1",
                "Stellar Shade",
                "stellar_shade",
                IcfbFactions.BOUNDLESS.getId()
        );
        shade1.setCircularOrbitPointingDown(pangea, shadeBaseAngle, shadeOrbitRadius, shadeOrbitDays);
        SectorEntityToken shade2 = system.addCustomEntity(
                pangea.getId() + "_shade_2",
                "Stellar Shade",
                "stellar_shade",
                IcfbFactions.BOUNDLESS.getId()
        );
        shade2.setCircularOrbitPointingDown(pangea, shadeBaseAngle + shadeAngleOffset, shadeOrbitRadius, shadeOrbitDays);
        SectorEntityToken shade3 = system.addCustomEntity(
                pangea.getId() + "_shade_3",
                "Stellar Shade",
                "stellar_shade",
                IcfbFactions.BOUNDLESS.getId()
        );
        shade3.setCircularOrbitPointingDown(pangea, shadeBaseAngle - shadeAngleOffset, shadeOrbitRadius, shadeOrbitDays);

        final PlanetAPI lanternia = system.addPlanet(
                IcfbPlanets.LANTERNIA.getId(),
                system.getCenter(),
                "Lanternia",
                Planets.BARREN_CASTIRON,
                300,
                100,
                8400,
                360
        );

        system.addPlanet(
                IcfbPlanets.LANTERNIA.getId() + "_moon",
                lanternia,
                "Lanterluna",
                Planets.BARREN2,
                120,
                65,
                400,
                30
        );

        StarSystemGenerator.addOrbitingEntities(
                system,
                system.getCenter(),
                StarAge.YOUNG,
                2,
                4,
                10000,
                5,
                true
        );
    }

    private static void createNebula(StarSystemAPI system) {
        final PlanetAPI star = system.getStar();
        final PlanetAPI pangea = IcfbPlanets.PANGEA.getPlanet();
        SectorEntityToken nebula = system.addTerrain(Terrain.NEBULA, new BaseTiledTerrain.TileParams(
                "xxx   " +
                        "x xx x" +
                        "xxxx  " +
                        "xxxxxx" +
                        "  x x " +
                        "     x",
                6, 6, // size of the nebula grid, should match above string
                "terrain", "nebula_amber", 4, 4, null));
        nebula.getLocation().set(pangea.getLocation().x + 1000f, pangea.getLocation().y);
        nebula.setCircularOrbit(star, 140f, 15000, 800);

        StarSystemGenerator.addSystemwideNebula(system, StarAge.OLD);
    }
}
