package org.shmo.icfb.campaign.gen.impl.people;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Voices;
import org.shmo.icfb.campaign.gen.PersonFactory;
import org.shmo.icfb.utilities.ShmoGenUtils;

public class AriaSerokaKotovCorvusModePersonFactory implements PersonFactory {
    @Override
    public PersonAPI createPerson(SectorAPI sector, String id, MarketAPI market) {
        PersonAPI person = ShmoGenUtils.createPerson(
                id,
                market,
                100,
                false,
                "Aria",
                "Seroka-Kotov",
                FullName.Gender.FEMALE,
                Factions.INDEPENDENT,
                Ranks.SPACE_CAPTAIN,
                Ranks.POST_MERCENARY,
                "icfb_portraits",
                "aria"
        );

        person.setVoice(Voices.SPACER);
        person.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 1);
        person.getStats().setSkillLevel(Skills.FIELD_MODULATION, 1);
        person.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 1);
        person.getStats().setSkillLevel(Skills.ORDNANCE_EXPERTISE, 1);
        person.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
        person.getStats().setSkillLevel(Skills.WOLFPACK_TACTICS, 1);
        person.getStats().setSkillLevel(Skills.COORDINATED_MANEUVERS, 1);

        return person;
    }
}
