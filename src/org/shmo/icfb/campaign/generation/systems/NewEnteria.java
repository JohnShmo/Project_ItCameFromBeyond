package org.shmo.icfb.campaign.generation.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.RingBand;
import org.magiclib.util.MagicCampaign;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class NewEnteria {
    public static final String STAR_ID = "icfb_new_enteria";
    public static final String LUMINARU_ID = "icfb_luminaru";
    public static final String LUMINARU_PRIME_ID = "icfb_luminaru_prime";
    public static final String WINGS_OF_ENTERIA_ID = "icfb_wings_of_enteria_station";
    public static final String WINGS_OF_ENTERIA_MARKET_ID = WINGS_OF_ENTERIA_ID + "_market";

    public void generate(SectorAPI sector) {
        final float luminaruOrbitDistance = 8000f;
        final float luminaruPrimeOrbitDistance = 1600f;
        final float wingsOfEnteriaOrbitDistance = 600f;
        final Color pinkColor = new Color(245,169,199);

        // Initialize system
        StarSystemAPI newEnteriaSystem = sector.createStarSystem("New Enteria");
        newEnteriaSystem.getLocation().set(1300, -22200);
        newEnteriaSystem.setEnteredByPlayer(true);

        // Create the star
        final PlanetAPI newEnteriaStar = newEnteriaSystem.initStar(
                STAR_ID,
                "star_red_dwarf",
                980f,
                300f
        );
        newEnteriaSystem.setLightColor(pinkColor);

        // Create stable locations
        SectorEntityToken stableLocation1 = newEnteriaSystem.addCustomEntity(null, null, "sensor_array_makeshift", "icfb_boundless");
        stableLocation1.setCircularOrbitPointingDown(newEnteriaStar, 55 + 60, 2800, 100);
        SectorEntityToken stableLocation2 = newEnteriaSystem.addCustomEntity(null, null, "comm_relay", "icfb_boundless");
        stableLocation2.setCircularOrbitPointingDown(newEnteriaStar, 245-60, 4500, 200);

        // Create Luminaru and its moons
        final PlanetAPI luminaru = newEnteriaSystem.addPlanet(
                LUMINARU_ID,
                newEnteriaStar,
                "Luminaru",
                "gas_giant",
                900f,
                450f,
                luminaruOrbitDistance,
                505f
        );
        final PlanetAPI luminaruPrime = newEnteriaSystem.addPlanet(
                LUMINARU_PRIME_ID,
                luminaru,
                "Luminaru Prime",
                "lava",
                400f,
                120f,
                luminaruPrimeOrbitDistance,
                34f
        );
        newEnteriaSystem.addAsteroidBelt(
                luminaruPrime,
                50,
                wingsOfEnteriaOrbitDistance,
                wingsOfEnteriaOrbitDistance, 15f, 30f
        );
        newEnteriaSystem.addRingBand(
                luminaruPrime,
                "misc",
                "rings_dust0",
                256f,
                3,
                Color.WHITE,
                256*2,
                wingsOfEnteriaOrbitDistance,
                25,
                null,
                null
        );

        // Create Wings of Enteria
        final SectorEntityToken wingsOfEnteria = newEnteriaSystem.addCustomEntity(
                WINGS_OF_ENTERIA_ID,
                "Wings of Enteria",
                "icfb_wings_of_enteria",
                "icfb_boundless"
        );
        wingsOfEnteria.setCircularOrbit(luminaruPrime, 90, wingsOfEnteriaOrbitDistance, 20);
        wingsOfEnteria.setCustomDescriptionId("icfb_wings_of_enteria");
        final MarketAPI wingsOfEnteriaMarket = MagicCampaign.addSimpleMarket(
                wingsOfEnteria,
                WINGS_OF_ENTERIA_MARKET_ID,
                "Wings of Enteria",
                6,
                "icfb_boundless",
                false,
                false,
                new ArrayList<>(Arrays.asList(
                        Conditions.POPULATION_6,
                        Conditions.ORE_ULTRARICH,
                        Conditions.VOLATILES_ABUNDANT,
                        Conditions.HOT,
                        Conditions.HABITABLE
                )),
                new ArrayList<>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
                        Industries.WAYSTATION,
                        Industries.STARFORTRESS_MID,
                        Industries.HEAVYBATTERIES,
                        Industries.MINING,
                        Industries.REFINING,
                        Industries.ORBITALWORKS,
                        Industries.MILITARYBASE
                )),
                true,
                true,
                true,
                true,
                true,
                false
        );
        wingsOfEnteriaMarket.setHasSpaceport(true);
        wingsOfEnteria.getMemoryWithoutUpdate().set(MemFlags.STORY_CRITICAL, true);
        wingsOfEnteria.getMemoryWithoutUpdate().set(MemFlags.STATION_MARKET, true);
        SpecialItemData pristineNano = new SpecialItemData("pristine_nanoforge", null);
        wingsOfEnteriaMarket.getIndustry(Industries.ORBITALWORKS).setSpecialItem(pristineNano);
        wingsOfEnteriaMarket.reapplyIndustries();
        Global.getSector().getEconomy().addMarket(wingsOfEnteriaMarket, true);

        // Generate hyperspace jump points
        newEnteriaSystem.autogenerateHyperspaceJumpPoints(true, true);

        // Clear away hyperspace clouds
        final HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        final NebulaEditor editor = new NebulaEditor(plugin);
        final float minRadius = plugin.getTileSize() * 2;
        final float radius = newEnteriaSystem.getMaxRadiusInHyperspace();
        editor.clearArc(
                newEnteriaSystem.getLocation().x, newEnteriaSystem.getLocation().y,
                0, radius + minRadius, 0, 360
        );
        editor.clearArc(
                newEnteriaSystem.getLocation().x, newEnteriaSystem.getLocation().y,
                0, radius + minRadius, 0, 360, 0.25f
        );

    }
}
