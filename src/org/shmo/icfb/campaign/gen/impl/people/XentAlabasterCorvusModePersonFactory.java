package org.shmo.icfb.campaign.gen.impl.people;

import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import org.shmo.icfb.campaign.gen.PersonFactory;
import org.shmo.icfb.utilities.ShmoGenUtils;

public class XentAlabasterCorvusModePersonFactory implements PersonFactory {
    public PersonAPI createPerson(SectorAPI sector, String id, MarketAPI market) {
        PersonAPI person = ShmoGenUtils.createPerson(
                id,
                market,
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

        person.setImportance(PersonImportance.VERY_HIGH);
        person.getMarket().getCommDirectory().getEntryForPerson(person).setHidden(true);

        return person;
    }
}
