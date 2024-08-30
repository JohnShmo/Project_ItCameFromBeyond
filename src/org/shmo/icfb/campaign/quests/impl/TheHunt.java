package org.shmo.icfb.campaign.quests.impl;

import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.QuestId;
import org.shmo.icfb.campaign.quests.impl.builders.TheHuntBuilder;
import org.shmo.icfb.campaign.scripts.QuestManager;

public class TheHunt {
    public static void start() {
        QuestManager.getInstance().add(QuestId.THE_HUNT, new TheHuntBuilder());
    }
}
