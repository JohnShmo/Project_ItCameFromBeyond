package org.shmo.icfb.campaign.quests;

public interface QuestStepScript {
    void init(QuestStep step);
    void start();
    void advance(float deltaTime);
    void end();
    void cleanup();
    boolean isComplete();
}
