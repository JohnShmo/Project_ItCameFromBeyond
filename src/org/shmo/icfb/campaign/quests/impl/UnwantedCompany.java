package org.shmo.icfb.campaign.quests.impl;

import org.shmo.icfb.campaign.quests.QuestIds;
import org.shmo.icfb.campaign.quests.QuestNames;
import org.shmo.icfb.campaign.quests.impl.builders.UnwantedCompanyBuilder;
import org.shmo.icfb.campaign.scripts.QuestManager;

public class UnwantedCompany {
    public static final String ID = QuestIds.UNWANTED_COMPANY;
    public static final String NAME = QuestNames.UNWANTED_COMPANY;

    public static void start() {
        QuestManager.getInstance().add(ID, new UnwantedCompanyBuilder());
    }
}
