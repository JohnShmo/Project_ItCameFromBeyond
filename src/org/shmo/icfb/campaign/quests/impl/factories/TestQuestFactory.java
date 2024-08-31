package org.shmo.icfb.campaign.quests.impl.factories;

import com.fs.starfarer.api.Global;
import org.shmo.icfb.campaign.intel.events.ShiftDriveEvent;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;
import org.shmo.icfb.campaign.quests.impl.TestQuest;
import org.shmo.icfb.campaign.quests.impl.intel.TestQuestStepIntel;
import org.shmo.icfb.campaign.quests.impl.scripts.TestQuestStepScript;

public class TestQuestFactory implements QuestFactory {
    @Override
    public Quest create() {
        Quest quest = new Quest(TestQuest.ID);
        quest.setName(TestQuest.NAME);
        quest.setIcon(Global.getSettings().getSpriteName(ShiftDriveEvent.ICON_CATEGORY, ShiftDriveEvent.ICON_ID));

        quest.addStep(new TestQuestStepIntel(), new TestQuestStepScript());
        quest.addFinalStep();

        return quest;
    }
}