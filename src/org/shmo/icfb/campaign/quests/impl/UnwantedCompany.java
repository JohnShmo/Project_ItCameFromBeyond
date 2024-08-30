package org.shmo.icfb.campaign.quests.impl;

import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.QuestId;
import org.shmo.icfb.campaign.quests.impl.builders.UnwantedCompanyBuilder;
import org.shmo.icfb.campaign.scripts.QuestManager;

public class UnwantedCompany {
    public static void start() {
        QuestManager.getInstance().add(QuestId.UNWANTED_COMPANY, new UnwantedCompanyBuilder());
    }
}
