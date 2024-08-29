package org.shmo.icfb.campaign.quests;

public class QuestStep {
    public QuestStepIntel intel;
    public QuestStepScript script;
    public Object userData;

    public QuestStep() {
        this.intel = null;
        this.script = null;
        this.userData = null;
    }

    public QuestStep(QuestStepIntel intel, QuestStepScript script) {
        this.intel = intel;
        this.script = script;
        this.userData = null;
    }

    public QuestStep(QuestStepIntel intel, QuestStepScript script, Object userData) {
        this.intel = intel;
        this.script = script;
        this.userData = userData;
    }
}
