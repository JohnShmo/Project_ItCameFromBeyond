package org.shmo.icfb.campaign.quests;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import org.shmo.icfb.ItCameFromBeyond;

import java.util.ArrayList;
import java.util.List;

public class Quest {
    private final List<QuestStep> _steps;
    private int _stepIndex;
    private boolean _started;
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

    private static void startStep(QuestStep step) {
        if (step == null)
            return;
        if (step.script != null) {
            step.script.init(step);
            step.script.start();
        }
        if (step.intel != null) {
            sendIntelForStep(step);
            ItCameFromBeyond.Log.info("Quest step with name: { " + step.intel.getName() + " } was started.");
        }
    }

    private static void endStep(QuestStep step) {
        if (step == null)
            return;
        if (step.intel != null) {
            removeIntelForStep(step);
            ItCameFromBeyond.Log.info("Quest step with name: { " + step.intel.getName() + " } was ended.");
        }
        if (step.script != null) {
            step.script.end();
            step.script.cleanup();
        }
    }

    public Quest() {
        _steps = new ArrayList<>();
        _stepIndex = 0;
        _started = false;
        _script = null;
    }

    public Quest(QuestScript script) {
        _steps = new ArrayList<>();
        _stepIndex = 0;
        _started = false;
        _script = script;
    }

    public Quest(List<QuestStep> steps) {
        _steps = new ArrayList<>(steps);
        _stepIndex = 0;
        _started = false;
        _script = null;
    }

    public Quest(List<QuestStep> steps, QuestScript script) {
        _steps = new ArrayList<>(steps);
        _stepIndex = 0;
        _started = false;
        _script = script;
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
        startCurrentStep();
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

    private void startCurrentStep() {
        if (isComplete())
            return;
        startStep(getCurrentStep());
    }

    private void endCurrentStep() {
        if (isComplete())
            return;
        endStep(getCurrentStep());
    }

    private void incrementStepIndex() { setStepIndex(getStepIndex() + 1); }
    private void setStepIndex(int index) { _stepIndex = index; }
    private int getStepIndex() { return _stepIndex; }
    private void resetStepIndex() { setStepIndex(0); }

    private List<QuestStep> getSteps() { return _steps; }

    private void progress() {
        endCurrentStep();
        incrementStepIndex();
        startCurrentStep();
    }

    public void addStep(QuestStep step) {
        getSteps().add(step);
    }

    public void addStep(QuestStepIntel intel, QuestStepScript script, Object userData) {
        addStep(new QuestStep(intel, script, userData));
    }

    public void addStep(QuestStepIntel intel, QuestStepScript script) {
        addStep(new QuestStep(intel, script));
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
