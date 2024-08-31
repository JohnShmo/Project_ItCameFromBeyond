package org.shmo.icfb.campaign.listeners;

public interface QuestListener {
    void notifyQuestStarted(String questId);
    void notifyQuestCompleted(String questId);
}
