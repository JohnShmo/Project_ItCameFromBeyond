package org.shmo.icfb;

import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.campaign.generation.entities.WingsOfEnteria;
import org.shmo.icfb.campaign.generation.systems.NewEnteria;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondStarSystems;


public class ItCameFromBeyondGen {
    public static void initFactionRelationships(SectorAPI sector) {
        FactionAPI player = sector.getFaction(Factions.PLAYER);
        FactionAPI hegemony = sector.getFaction(Factions.HEGEMONY);
        FactionAPI tritachyon = sector.getFaction(Factions.TRITACHYON);
        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
        FactionAPI kol = sector.getFaction(Factions.KOL);
        FactionAPI church = sector.getFaction(Factions.LUDDIC_CHURCH);
        FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
        FactionAPI league = sector.getFaction(Factions.PERSEAN);
        FactionAPI boundless = sector.getFaction("icfb_boundless");

        boundless.setRelationship(path.getId(), RepLevel.HOSTILE);
        boundless.setRelationship(hegemony.getId(), RepLevel.SUSPICIOUS);
        boundless.setRelationship(pirates.getId(), RepLevel.HOSTILE);
        boundless.setRelationship(tritachyon.getId(), RepLevel.SUSPICIOUS);
        boundless.setRelationship(church.getId(), RepLevel.NEUTRAL);
        boundless.setRelationship(kol.getId(), RepLevel.INHOSPITABLE);
        boundless.setRelationship(league.getId(), RepLevel.INHOSPITABLE);
        boundless.setRelationship(player.getId(), RepLevel.SUSPICIOUS);
    }

    public static void generateForCorvusMode(SectorAPI sector) {
        final float newEnteriaLocationX = 1300;
        final float newEnteriaLocationY = -22200;
        final float wingsOfEnteriaOrbitDistance = 600;
        final float wingsOfEnteriaOrbitDays = 20;

        initFactionRelationships(sector);

        // Generate systems
        final StarSystemAPI newEnteria = NewEnteria.generate(sector, newEnteriaLocationX, newEnteriaLocationY);

        // Generate entities
        WingsOfEnteria.generate(
                sector,
                newEnteria.getEntityById(ItCameFromBeyondStarSystems.NewEnteria.LUMINARU_MAJOR),
                wingsOfEnteriaOrbitDistance,
                wingsOfEnteriaOrbitDays
        );
    }

    public static void generateHyperspace(StarSystemAPI system) {
        system.autogenerateHyperspaceJumpPoints(true, true);

        // Clear away hyperspace clouds
        final HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        final NebulaEditor editor = new NebulaEditor(plugin);
        final float minRadius = plugin.getTileSize() * 2;
        final float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(
                system.getLocation().x, system.getLocation().y,
                0, radius + minRadius, 0, 360
        );
        editor.clearArc(
                system.getLocation().x, system.getLocation().y,
                0, radius + minRadius, 0, 360, 0.25f
        );
    }
}
