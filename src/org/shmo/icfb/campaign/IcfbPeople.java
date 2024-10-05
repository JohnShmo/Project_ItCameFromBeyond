package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.campaign.gen.PersonFactory;
import org.shmo.icfb.campaign.gen.impl.people.XentAlabasterCorvusModePersonFactory;

public class IcfbPeople {
    public static final PersonData XENT_ALABASTER = new PersonData("icfb_xent");

    public static void generateForCorvusMode(SectorAPI sector) {
        IcfbLog.info("- Initializing people...");

        XENT_ALABASTER.createPerson(
                new XentAlabasterCorvusModePersonFactory(),
                sector,
                IcfbMarkets.WINGS_OF_ENTERIA.getMarket()
        );
    }

    public static class PersonData {
        private final String _id;

        public PersonData(String id) {
            _id = id;
        }

        private void createPerson(PersonFactory factory, SectorAPI sector, MarketAPI market) {
            PersonAPI person = factory.createPerson(sector, _id, market);
            addImportantPerson(sector, person);
        }

        public String getId() {
            return _id;
        }

        public PersonAPI getPerson() {
            return Global.getSector().getImportantPeople().getPerson(_id);
        }

        private static void addImportantPerson(SectorAPI sector, PersonAPI person) {
            sector.getImportantPeople().addPerson(person);
        }
    }
}
