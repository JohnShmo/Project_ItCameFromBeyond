package org.shmo.icfb.campaign.quests.impl.factories;

import com.fs.starfarer.api.Global;
import org.shmo.icfb.campaign.intel.events.ShiftDriveEvent;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;
import org.shmo.icfb.campaign.quests.impl.UnwantedCompany;

public class UnwantedCompanyFactory implements QuestFactory {
    @Override
    public Quest create() {
        Quest quest = new Quest(UnwantedCompany.ID);
        quest.setName(UnwantedCompany.NAME);
        quest.setIcon(Global.getSettings().getSpriteName(ShiftDriveEvent.ICON_CATEGORY, ShiftDriveEvent.ICON_ID));
        quest.addTag("Shift Drive");

        quest.addFinalStep();

        return quest;
    }
}
