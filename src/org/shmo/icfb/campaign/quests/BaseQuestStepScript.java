package org.shmo.icfb.campaign.quests;

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
    public abstract void start();

    @Override
    public abstract void advance(float deltaTime);

    @Override
    public abstract void end();

    @Override
    public void cleanup() {
        _questStep = null;
    }

    @Override
    public abstract boolean isComplete();
}
