package org.shmo.icfb.campaign.quests;

import org.shmo.icfb.campaign.quests.intel.QuestStepIntel;
import org.shmo.icfb.campaign.quests.scripts.QuestStepScript;

public class QuestStep {
    public QuestStepIntel intel;
    public QuestStepScript script;
    public Quest quest;
    public Object userData;

    public QuestStep(Quest quest) {
        this.intel = null;
        this.script = null;
        this.quest = quest;
        this.userData = null;
    }

    public QuestStep(Quest quest, QuestStepIntel intel, QuestStepScript script) {
        this.intel = intel;
        this.script = script;
        this.quest = quest;
        this.userData = null;
    }

    public QuestStep(Quest quest, QuestStepIntel intel, QuestStepScript script, Object userData) {
        this.intel = intel;
        this.script = script;
        this.quest = quest;
        this.userData = userData;
    }
}
