package org.shmo.icfb.campaign.quests.scripts;

import org.shmo.icfb.campaign.quests.Quest;

public abstract class BaseQuestScript implements QuestScript {
    private Quest _quest = null;

    protected Quest getQuest() {
        return _quest;
    }

    @Override
    public void init(Quest quest) {
        _quest = quest;
    }

    @Override
    public void start() {}

    @Override
    public void advance(float deltaTime) {}

    @Override
    public void end() {}

    @Override
    public void cleanup() {
        _quest = null;
    }
}
