package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.campaign.gen.PersonFactory;
import org.shmo.icfb.campaign.gen.impl.people.AriaSerokaKotovCorvusModePersonFactory;
import org.shmo.icfb.campaign.gen.impl.people.XentAlabasterCorvusModePersonFactory;

import java.util.List;

public class IcfbPeople {
    public static final PersonData XENT_ALABASTER = new PersonData("icfb_xent");
    public static final PersonData ARIA_SEROKA_KOTOV = new PersonData("icfb_aria");

    public static void generateForCorvusMode(SectorAPI sector) {
        IcfbLog.info("  Initializing people...");

        final String putPeopleInChargeKey = "$icfbPutPeopleInCharge";
        if (!sector.getMemoryWithoutUpdate().getBoolean(putPeopleInChargeKey)) {
            sector.getMemoryWithoutUpdate().set(putPeopleInChargeKey, true);
            genPeople(IcfbMarkets.WINGS_OF_ENTERIA.getMarket());
            genPeople(IcfbMarkets.LORELAI.getMarket());
            genPeople(IcfbMarkets.PANGEA.getMarket());
        }

        XENT_ALABASTER.createPerson(
                new XentAlabasterCorvusModePersonFactory(),
                sector,
                sector.getEconomy().getMarket(IcfbMarkets.CELADON.getId())
        );

        ARIA_SEROKA_KOTOV.createPerson(
                new AriaSerokaKotovCorvusModePersonFactory(),
                sector,
                null
        );
    }

    private static void genPeople(MarketAPI market) {
        final FactionAPI faction = market.getFaction();
        if (faction == null || !faction.getId().equals(IcfbFactions.BOUNDLESS.getId()))
            return;

        final PersonAPI admin = faction.createRandomPerson();
        admin.setPostId(Ranks.POST_ADMINISTRATOR);
        market.setAdmin(admin);
        market.getCommDirectory().addPerson(admin);
    }


    public static class PersonData {
        private final String _id;

        public PersonData(String id) {
            _id = id;
        }

        public void createPerson(PersonFactory factory, SectorAPI sector, MarketAPI market) {
            IcfbLog.info("    Creating person: { " + _id + " }...");
            if (isGenerated()) {
                IcfbLog.info("      Skipped!");
                return;
            }

            PersonAPI person = factory.createPerson(sector, _id, market);
            addImportantPerson(sector, person);
            IcfbLog.info("      Done");

            markAsGenerated();
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

        public boolean isGenerated() {
            return Global.getSector().getMemoryWithoutUpdate().getBoolean(getIsGeneratedKey());
        }

        private void markAsGenerated() {
            Global.getSector().getMemoryWithoutUpdate().set(getIsGeneratedKey(), true);
        }

        private String getKey() {
            return "$IcfbPeople:" + _id;
        }

        private String getIsGeneratedKey() {
            return getKey() + ":isGenerated";
        }
    }
}
