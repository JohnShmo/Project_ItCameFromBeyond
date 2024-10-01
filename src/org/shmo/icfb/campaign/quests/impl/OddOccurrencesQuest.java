package org.shmo.icfb.campaign.quests.impl;

import com.fs.starfarer.api.Global;
import org.shmo.icfb.campaign.intel.events.ShiftDriveEvent;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;

public class OddOccurrencesQuest implements QuestFactory {
    public static final String ID = "odd_occurrences";
    public static final String NAME = "Odd Occurrences";

    @Override
    public Quest create() {
        Quest quest = new Quest(ID);
        quest.setName(NAME);
        quest.setIcon(Global.getSettings().getSpriteName(ShiftDriveEvent.ICON_CATEGORY, ShiftDriveEvent.ICON_ID));
        quest.addTag("Shift Drive");

        quest.addFinalStep();

        return quest;
    }
}
