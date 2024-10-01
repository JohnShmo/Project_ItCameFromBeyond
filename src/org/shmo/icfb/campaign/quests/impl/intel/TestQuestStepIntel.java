package org.shmo.icfb.campaign.quests.impl.intel;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.shmo.icfb.campaign.quests.impl.TestQuest;
import org.shmo.icfb.campaign.quests.intel.DefeatFleetQuestStepIntel;

public class TestQuestStepIntel extends DefeatFleetQuestStepIntel {
    public TestQuestStepIntel() {
        setTarget(TestQuest.getPirateSpawnLocation());
        setLocationHint("near a planet of");
        setTargetName("Jack Sparrow");
    }

    @Override
    public void addDescriptionBody(TooltipMakerAPI info) {
        final float pad = 10;
        info.addPara("Yarrrr! There be a dastardly pirate lurking in a core-world system as of late. " +
                "Kindly take out the trash, starfarer.", pad);
    }
}
