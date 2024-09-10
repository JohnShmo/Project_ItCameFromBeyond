package org.shmo.icfb.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;
import org.shmo.icfb.campaign.listeners.QuestListener;
import org.shmo.icfb.factories.ScriptFactory;

import java.util.*;

public class QuestManager implements EveryFrameScript {
    public static final String KEY = "$icfb_QuestManager";

    public static class Factory implements ScriptFactory {
        @Override
        public EveryFrameScript createOrGetInstance() {
            EveryFrameScript script = QuestManager.getInstance();
            if (script == null)
                script = new QuestManager();
            return script;
        }
    }

    public static QuestManager getInstance() {
        return (QuestManager)Global.getSector().getMemoryWithoutUpdate().get(KEY);
    }

    private final Map<String, Quest> _questMap;
    private final Set<QuestListener> _listeners;

    private Map<String, Quest> getQuestMap() {
        return _questMap;
    }

    private Set<QuestListener> getListeners() {
        return _listeners;
    }

    public List<Quest> getAllQuests() {
        final List<Quest> result = new ArrayList<>();
        final Map<String, Quest> questMap = getQuestMap();
        for (Map.Entry<String, Quest> quest : questMap.entrySet()) {
            result.add(quest.getValue());
        }
        return result;
    }

    public QuestManager() {
        _questMap = new HashMap<>();
        _listeners = new HashSet<>();
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
    }

    public void addListener(QuestListener listener) {
        getListeners().add(listener);
    }

    public void removeListener(QuestListener listener) {
        getListeners().remove(listener);
    }

    private void broadcastQuestCompleted(String questId) {
        ItCameFromBeyond.Log.info("Quest with id: { " + questId + " } was completed.");
        for (QuestListener listener : getListeners()) {
            listener.notifyQuestCompleted(questId);
        }
    }

    private void broadcastQuestStarted(String questId) {
        ItCameFromBeyond.Log.info("Quest with id: { " + questId + " } was started.");
        for (QuestListener listener : getListeners()) {
            listener.notifyQuestStarted(questId);
        }
    }

    public void add(QuestFactory questFactory) {
        Quest quest = questFactory.create();
        add(quest.getId(), quest);
    }

    private void add(String id, Quest quest) {
        if (id == null || quest == null)
            return;
        remove(id);
        getQuestMap().put(id, quest);
        quest.start();
        broadcastQuestStarted(id);
    }

    public Quest getQuest(String id) {
        return getQuestMap().get(id);
    }

    public boolean contains(String id) {
        return getQuestMap().containsKey(id);
    }

    public boolean contains(Quest quest) {
        return getQuestMap().containsValue(quest);
    }

    public void remove(String id) {
        if (id == null)
            return;
        final Quest quest = getQuestMap().remove(id);
        if (quest != null)
            quest.end();
    }

    public void remove(Quest quest) {
        if (quest == null)
            return;
        final Quest removed = getQuestMap().remove(quest.getId());
        if (removed != null)
            removed.end();
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        final List<Quest> quests = getAllQuests();
        for (Quest quest : quests) {
            if (quest.isComplete()) {
                broadcastQuestCompleted(quest.getId());
                remove(quest);
                continue;
            }
            quest.advance(amount);
        }
    }
}