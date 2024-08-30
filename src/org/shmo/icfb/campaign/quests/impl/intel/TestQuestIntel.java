package org.shmo.icfb.campaign.quests.impl.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.shmo.icfb.campaign.quests.DefeatFleetIntel;
import org.shmo.icfb.campaign.quests.LocationIntel;
import org.shmo.icfb.campaign.quests.impl.OddOccurrences;

public class TestQuestIntel extends DefeatFleetIntel {
    public TestQuestIntel() {
        setTarget(OddOccurrences.getTestPirateSpawnLocation());
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
