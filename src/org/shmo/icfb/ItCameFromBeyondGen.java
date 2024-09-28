package org.shmo.icfb;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import org.shmo.icfb.campaign.generation.systems.NewEnteria;

public class ItCameFromBeyondGen {
    public static void initFactionRelationships(SectorAPI sector) {
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
    }

    public void generate(SectorAPI sector) {
        initFactionRelationships(sector);
        new NewEnteria().generate(sector);
    }
}
