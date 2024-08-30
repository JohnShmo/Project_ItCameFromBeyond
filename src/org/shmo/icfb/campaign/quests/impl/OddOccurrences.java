package org.shmo.icfb.campaign.quests.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import org.shmo.icfb.campaign.quests.QuestIds;
import org.shmo.icfb.campaign.quests.QuestNames;
import org.shmo.icfb.campaign.quests.impl.builders.OddOccurrencesBuilder;
import org.shmo.icfb.campaign.scripts.QuestManager;

public class OddOccurrences {
    public static final String ID = QuestIds.ODD_OCCURRENCES;
    public static final String NAME = QuestNames.ODD_OCCURRENCES;

    public static void start() {
        QuestManager.getInstance().add(ID, new OddOccurrencesBuilder());
    }

    public static SectorEntityToken getTestPirateSpawnLocation() {
        return Global.getSector().getStarSystem("Penelope's Star").getPlanets().get(2);
    }
}
