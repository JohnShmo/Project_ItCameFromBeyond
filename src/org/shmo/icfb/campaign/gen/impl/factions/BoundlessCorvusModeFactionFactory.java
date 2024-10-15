package org.shmo.icfb.campaign.gen.impl.factions;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import org.shmo.icfb.campaign.gen.FactionFactory;

public class BoundlessCorvusModeFactionFactory implements FactionFactory {
    public FactionAPI createFaction(SectorAPI sector, String id) {
        FactionAPI faction = sector.getFaction(id);

        FactionAPI player = sector.getFaction(Factions.PLAYER);
        FactionAPI hegemony = sector.getFaction(Factions.HEGEMONY);
        FactionAPI tritachyon = sector.getFaction(Factions.TRITACHYON);
        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
        FactionAPI kol = sector.getFaction(Factions.KOL);
        FactionAPI church = sector.getFaction(Factions.LUDDIC_CHURCH);
        FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
        FactionAPI league = sector.getFaction(Factions.PERSEAN);

        faction.setRelationship(path.getId(), RepLevel.HOSTILE);
        faction.setRelationship(hegemony.getId(), RepLevel.SUSPICIOUS);
        faction.setRelationship(pirates.getId(), RepLevel.HOSTILE);
        faction.setRelationship(tritachyon.getId(), RepLevel.SUSPICIOUS);
        faction.setRelationship(church.getId(), RepLevel.FAVORABLE);
        faction.setRelationship(kol.getId(), RepLevel.INHOSPITABLE);
        faction.setRelationship(league.getId(), RepLevel.INHOSPITABLE);
        faction.setRelationship(player.getId(), RepLevel.SUSPICIOUS);

        return faction;
    }
}
