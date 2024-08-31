package org.shmo.icfb.campaign.quests.scripts;

import org.shmo.icfb.campaign.quests.Quest;

public interface QuestScript {
    void init(Quest quest);
    void start();
    void advance(float deltaTime);
    void end();
    void cleanup();
}
