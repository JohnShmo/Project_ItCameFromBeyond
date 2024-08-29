package org.shmo.icfb.campaign.quests;

public interface QuestScript {
    void init(Quest quest);
    void start();
    void advance(float deltaTime);
    void end();
    void cleanup();
}
