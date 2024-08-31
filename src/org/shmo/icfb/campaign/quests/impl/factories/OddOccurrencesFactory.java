package org.shmo.icfb.campaign.quests.impl.factories;

import com.fs.starfarer.api.Global;
import org.shmo.icfb.campaign.intel.events.ShiftDriveEvent;
import org.shmo.icfb.campaign.quests.*;
import org.shmo.icfb.campaign.quests.impl.OddOccurrences;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;

public class OddOccurrencesFactory implements QuestFactory {
    @Override
    public Quest create() {
        Quest quest = new Quest(OddOccurrences.ID);
        quest.setName(OddOccurrences.NAME);
        quest.setIcon(Global.getSettings().getSpriteName(ShiftDriveEvent.ICON_CATEGORY, ShiftDriveEvent.ICON_ID));
        quest.addTag("Shift Drive");

        quest.addFinalStep();

        return quest;
    }
}
