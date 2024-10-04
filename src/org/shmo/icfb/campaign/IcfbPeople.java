package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.utilities.ShmoGenUtils;

public class IcfbPeople {

    public static void generateForCorvus(SectorAPI sector) {
        ItCameFromBeyond.Log.info("- Generating people...");
        XentAlabaster.createPerson(sector);
    }

    private static PersonAPI getImportantPerson(SectorAPI sector, String personId) {
        return sector.getImportantPeople().getPerson(personId);
    }

    private static void addImportantPerson(SectorAPI sector, PersonAPI person) {
        sector.getImportantPeople().addPerson(person);
    }

    private static MarketAPI getImportantPersonMarket(SectorAPI sector, String personId) {
        PersonAPI person = getImportantPerson(sector, personId);
        if (person == null)
            return null;
        return person.getMarket();
    }

    public static class XentAlabaster {
        public static final String ID = "icfb_xent";

        public static PersonAPI getPerson() {
            return getImportantPerson(Global.getSector(), ID);
        }

        public static MarketAPI getMarket() {
            return getImportantPersonMarket(Global.getSector(), ID);
        }

        public static void createPerson(SectorAPI sector) {
            PersonAPI xent = sector.getImportantPeople().getPerson(ID);
            if (xent != null) {
                return;
            }

            xent = ShmoGenUtils.createPerson(
                    ID,
                    IcfbMarkets.WingsOfEnteria.getMarket(),
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
            addImportantPerson(sector, xent);
        }
    }
}
