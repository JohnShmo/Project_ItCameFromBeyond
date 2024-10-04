package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;

public class IcfbFactions {

    public static class Boundless {
        public static final String ID = "icfb_boundless";

        public static FactionAPI getFaction() {
            return Global.getSector().getFaction(ID);
        }

        public static FactionAPI initFaction(SectorAPI sector) {
            FactionAPI faction = getFaction();

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
            faction.setRelationship(church.getId(), RepLevel.NEUTRAL);
            faction.setRelationship(kol.getId(), RepLevel.INHOSPITABLE);
            faction.setRelationship(league.getId(), RepLevel.INHOSPITABLE);
            faction.setRelationship(player.getId(), RepLevel.SUSPICIOUS);

            return faction;
        }
    }
}
