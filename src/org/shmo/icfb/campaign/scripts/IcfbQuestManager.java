package org.shmo.icfb.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;
import org.shmo.icfb.campaign.listeners.QuestListener;
import org.shmo.icfb.factories.ScriptFactory;
import org.shmo.icfb.utilities.MemoryHelper;

import java.util.*;

public class IcfbQuestManager implements EveryFrameScript {
    public static final String KEY = "$IcfbQuestManager";
    public static final String QUEST_MAP_KEY = KEY + ":questMap";
    public static final String LISTENERS_KEY = KEY + ":listeners";

    public static class Factory implements ScriptFactory {
        @Override
        public EveryFrameScript createOrGetInstance() {
            EveryFrameScript script = IcfbQuestManager.getInstance();
            if (script == null)
                script = new IcfbQuestManager();
            return script;
        }
    }

    public static IcfbQuestManager getInstance() {
        return (IcfbQuestManager)Global.getSector().getMemoryWithoutUpdate().get(KEY);
    }

    private Map<String, Quest> getQuestMap() {
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        Map<String, Quest> result = MemoryHelper.get(memory, QUEST_MAP_KEY);
        if (result == null)
            result = MemoryHelper.set(memory, QUEST_MAP_KEY, new HashMap<String, Quest>());
        return result;
    }

    private Set<QuestListener> getListeners() {
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        Set<QuestListener> result = MemoryHelper.get(memory, LISTENERS_KEY);
        if (result == null)
            result = MemoryHelper.set(memory, LISTENERS_KEY, new HashSet<QuestListener>());
        return result;
    }

    public List<Quest> getAllQuests() {
        final List<Quest> result = new ArrayList<>();
        final Map<String, Quest> questMap = getQuestMap();
        for (Map.Entry<String, Quest> quest : questMap.entrySet()) {
            result.add(quest.getValue());
        }
        return result;
    }

    public IcfbQuestManager() {
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
    }

    public void addListener(QuestListener listener) {
        getListeners().add(listener);
    }

    public void removeListener(QuestListener listener) {
        getListeners().remove(listener);
    }

    private void broadcastQuestCompleted(String questId) {
        IcfbLog.info("Quest with id: { " + questId + " } was completed.");
        for (QuestListener listener : getListeners()) {
            listener.notifyQuestCompleted(questId);
        }
    }

    private void broadcastQuestStarted(String questId) {
        IcfbLog.info("Quest with id: { " + questId + " } was started.");
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
