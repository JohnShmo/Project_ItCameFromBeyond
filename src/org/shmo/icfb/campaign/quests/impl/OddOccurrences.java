package org.shmo.icfb.campaign.quests.impl;

import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.QuestId;
import org.shmo.icfb.campaign.quests.impl.builders.OddOccurrencesBuilder;
import org.shmo.icfb.campaign.scripts.QuestManager;

public class OddOccurrences {
    public static void start() {
        QuestManager.getInstance().add(QuestId.ODD_OCCURRENCES, new OddOccurrencesBuilder());
    }

    public static Quest getInstance() {
        return QuestManager.getInstance().getQuest(QuestId.ODD_OCCURRENCES);
    }
}
