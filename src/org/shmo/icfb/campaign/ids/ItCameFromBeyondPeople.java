package org.shmo.icfb.campaign.ids;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.ItCameFromBeyondGen;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondMarkets;

public class ItCameFromBeyondPeople {
    public static void generate(SectorAPI sector) {
        ItCameFromBeyond.Log.info("- Generating people...");
        XentAlabaster.create(sector);
    }

    public static class XentAlabaster {
        public static final String ID = "icfb_xent";

        public static PersonAPI getInstance() {
            return Global.getSector().getImportantPeople().getPerson(ID);
        }

        private static void create(SectorAPI sector) {
            PersonAPI xent = sector.getImportantPeople().getPerson(ID);
            if (xent != null) {
                return;
            }

            xent = ItCameFromBeyondGen.createPerson(
                    ID,
                    sector.getEconomy().getMarket(ItCameFromBeyondMarkets.WINGS_OF_ENTERIA),
                    100,
                    false,
                    "Xent",
                    "Alabaster",
                    FullName.Gender.MALE,
                    Factions.INDEPENDENT,
                    Ranks.AGENT,
                    Ranks.POST_AGENT,
                    "icfb_portraits",
                    "shifter_01_static"
            );

            xent.setImportance(PersonImportance.VERY_HIGH);
            xent.getMarket().getCommDirectory().getEntryForPerson(xent).setHidden(true);
            sector.getImportantPeople().addPerson(xent);
        }
    }
}
