package org.shmo.icfb.campaign.gen.impl.factions;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import org.shmo.icfb.campaign.IcfbFactions;
import org.shmo.icfb.campaign.gen.FactionFactory;

public class ShifterFactionFactory implements FactionFactory {
    public FactionAPI createFaction(SectorAPI sector, String id) {
        FactionAPI faction = sector.getFaction(id);

        FactionAPI player = sector.getFaction(Factions.PLAYER);
        FactionAPI hegemony = sector.getFaction(Factions.HEGEMONY);
        FactionAPI tritachyon = sector.getFaction(Factions.TRITACHYON);
        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
        FactionAPI diktat = sector.getFaction(Factions.DIKTAT);
        FactionAPI kol = sector.getFaction(Factions.KOL);
        FactionAPI church = sector.getFaction(Factions.LUDDIC_CHURCH);
        FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
        FactionAPI league = sector.getFaction(Factions.PERSEAN);
        FactionAPI boundless = IcfbFactions.BOUNDLESS.getFaction();

        faction.setRelationship(player.getId(), RepLevel.HOSTILE);
        faction.setRelationship(church.getId(), RepLevel.VENGEFUL);
        faction.setRelationship(path.getId(), RepLevel.VENGEFUL);
        faction.setRelationship(boundless.getId(), RepLevel.VENGEFUL);
        faction.setRelationship(hegemony.getId(), RepLevel.HOSTILE);
        faction.setRelationship(pirates.getId(), RepLevel.HOSTILE);
        faction.setRelationship(diktat.getId(), RepLevel.HOSTILE);
        faction.setRelationship(tritachyon.getId(), RepLevel.HOSTILE);
        faction.setRelationship(kol.getId(), RepLevel.HOSTILE);
        faction.setRelationship(league.getId(), RepLevel.HOSTILE);

        faction.ensureAtBest(path.getId(), RepLevel.HOSTILE);
        faction.ensureAtBest(church.getId(), RepLevel.HOSTILE);
        faction.ensureAtBest(boundless.getId(), RepLevel.HOSTILE);
        faction.ensureAtBest(hegemony.getId(), RepLevel.INHOSPITABLE);
        faction.ensureAtBest(pirates.getId(), RepLevel.INHOSPITABLE);
        faction.ensureAtBest(diktat.getId(), RepLevel.INHOSPITABLE);
        faction.ensureAtBest(tritachyon.getId(), RepLevel.INHOSPITABLE);
        faction.ensureAtBest(kol.getId(), RepLevel.INHOSPITABLE);
        faction.ensureAtBest(league.getId(), RepLevel.INHOSPITABLE);

        return faction;
    }
}
