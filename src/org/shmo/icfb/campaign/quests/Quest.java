package org.shmo.icfb.campaign.quests;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import java.util.ArrayList;
import java.util.List;

public class Quest {
    private final List<QuestStep> _steps;
    private boolean _started;
    private int _currentStepIndex;
    private QuestScript _script;

    public static void sendIntelForStep(QuestStep step) {
        QuestStepIntelPlugin intelPlugin = new QuestStepIntelPlugin(step.intel);
        removeIntelForStep(step); // <-- Just in case
        intelPlugin.getImpl().init(step);
        Global.getSector().getIntelManager().addIntel(intelPlugin);
    }

    private static QuestStepIntelPlugin tryConvertToQuestIntelPlugin(IntelInfoPlugin intelPlugin) {
        if (intelPlugin instanceof QuestStepIntelPlugin) {
            return (QuestStepIntelPlugin)intelPlugin;
        }
        return null;
    }

    public static QuestStepIntelPlugin getIntelPluginForStep(QuestStep step) {
        QuestStepIntel intel = step.intel;
        List<IntelInfoPlugin> allIntelPlugins = Global.getSector().getIntelManager().getIntel();
        for (IntelInfoPlugin intelPlugin : allIntelPlugins) {
            QuestStepIntelPlugin questPlugin = tryConvertToQuestIntelPlugin(intelPlugin);
            if (questPlugin != null && questPlugin.getImpl() == intel)
                return questPlugin;
        }
        return null;
    }

    public static void removeIntelForStep(QuestStep step) {
        QuestStepIntelPlugin plugin = getIntelPluginForStep(step);
        if (plugin == null)
            return;
        Global.getSector().getIntelManager().removeIntel(plugin);
        plugin.getImpl().cleanup();
    }

    private static void beginStep(QuestStep step) {
        if (step == null)
            return;
        if (step.script != null) {
            step.script.init(step);
            step.script.start();
        }
        if (step.intel != null) {
            sendIntelForStep(step);
        }
    }

    private static void endStep(QuestStep step) {
        if (step == null)
            return;
        if (step.intel != null) {
            removeIntelForStep(step);
        }
        if (step.script != null) {
            step.script.end();
            step.script.cleanup();
        }
    }

    public Quest() {
        _steps = new ArrayList<>();
        _currentStepIndex = 0;
        _started = false;
        _script = null;
    }

    public void setScript(QuestScript script) {
        _script = script;
    }

    public QuestScript getScript() {
        return _script;
    }

    public void start() {
        if (isStarted())
            return;
        markStarted();
        if (getScript() != null) {
            getScript().init(this);
            getScript().start();
        }
        beginCurrentStep();
    }

    public void end() {
        endCurrentStep();
        resetStepIndex();
        unmarkStarted();
        if (getScript() != null) {
            getScript().end();
            getScript().cleanup();
        }
    }

    private void markStarted() {
        _started = true;
    }

    private void unmarkStarted() {
        _started = false;
    }

    private void beginCurrentStep() {
        if (isComplete())
            return;
        beginStep(getCurrentStep());
    }

    private void endCurrentStep() {
        if (isComplete())
            return;
        endStep(getCurrentStep());
    }

    private void incrementStepIndex() { setStepIndex(getStepIndex() + 1); }
    private void setStepIndex(int index) { _currentStepIndex = index; }
    private int getStepIndex() { return _currentStepIndex; }
    private void resetStepIndex() { setStepIndex(0); }

    private List<QuestStep> getSteps() { return _steps; }

    private void progress() {
        endCurrentStep();
        incrementStepIndex();
        beginCurrentStep();
    }

    public void addStep(QuestStep step) {
        getSteps().add(step);
    }

    public QuestStep getCurrentStep() {
        return getSteps().get(getStepIndex());
    }

    public void advance(float deltaTime) {
        if (!isStarted() || isComplete())
            return;

        if (getScript() != null) {
            getScript().advance(deltaTime);
        }

        QuestStep current = getCurrentStep();
        if (current == null || current.script == null) {
            progress();
            return;
        }

        current.script.advance(deltaTime);
        if (current.script.isComplete())
            progress();
    }

    public boolean isStarted() {
        return _started;
    }

    public boolean isComplete() {
        return getStepIndex() >= getSteps().size();
    }
}
