package org.shmo.icfb.campaign.quests.impl.builders;

import com.fs.starfarer.api.Global;
import org.shmo.icfb.campaign.intel.events.ShiftDriveEvent;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.QuestBuilder;
import org.shmo.icfb.campaign.quests.impl.TheHunt;
import org.shmo.icfb.campaign.quests.impl.intel.TestQuestIntel;
import org.shmo.icfb.campaign.quests.impl.scripts.TestQuestScript;

public class TheHuntBuilder implements QuestBuilder {
    @Override
    public void build(Quest quest) {
        quest.setName(TheHunt.NAME);
        quest.setIcon(Global.getSettings().getSpriteName(ShiftDriveEvent.ICON_CATEGORY, ShiftDriveEvent.ICON_ID));
        quest.addTag("Shift Drive");

        quest.addStep(new TestQuestIntel(), new TestQuestScript());
        quest.addFinalStep();
    }
}
