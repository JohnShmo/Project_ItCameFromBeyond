package org.shmo.icfb.campaign.quests.impl;

import org.shmo.icfb.campaign.quests.QuestIds;
import org.shmo.icfb.campaign.quests.QuestNames;
import org.shmo.icfb.campaign.quests.impl.builders.TheHuntBuilder;
import org.shmo.icfb.campaign.scripts.QuestManager;

public class TheHunt {
    public static final String ID = QuestIds.THE_HUNT;
    public static final String NAME = QuestNames.THE_HUNT;

    public static void start() {
        QuestManager.getInstance().add(ID, new TheHuntBuilder());
    }
}
