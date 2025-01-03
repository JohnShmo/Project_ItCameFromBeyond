package org.shmo.icfb.campaign.quests.scripts;

import org.shmo.icfb.campaign.quests.QuestStep;

public abstract class BaseQuestStepScript implements QuestStepScript {
    private QuestStep _questStep = null;

    protected QuestStep getQuestStep() {
        return _questStep;
    }

    @Override
    public void init(QuestStep step) {
        _questStep = step;
    }

    @Override
    public void start() {}

    @Override
    public void advance(float deltaTime) {}

    @Override
    public void end() {}

    @Override
    public void cleanup() {
        _questStep = null;
    }

    @Override
    public boolean isComplete() { return false; }
}
