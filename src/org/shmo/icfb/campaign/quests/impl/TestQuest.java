package org.shmo.icfb.campaign.quests.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import org.shmo.icfb.campaign.intel.events.ShiftDriveEvent;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;
import org.shmo.icfb.campaign.quests.impl.intel.TestQuestStepIntel;
import org.shmo.icfb.campaign.quests.impl.scripts.TestQuestStepScript;

public class TestQuest implements QuestFactory {
    public static final String ID = "test";
    public static final String NAME = "Test Quest";

    @Override
    public Quest create() {
        Quest quest = new Quest(ID);
        quest.setName(NAME);
        quest.setIcon(Global.getSettings().getSpriteName(ShiftDriveEvent.ICON_CATEGORY, ShiftDriveEvent.ICON_ID));

        quest.addStep(new TestQuestStepIntel(), new TestQuestStepScript());
        quest.addFinalStep();

        return quest;
    }

    public static SectorEntityToken getPirateSpawnLocation() {
        return Global.getSector().getStarSystem("Penelope's Star").getPlanets().get(2);
    }
}