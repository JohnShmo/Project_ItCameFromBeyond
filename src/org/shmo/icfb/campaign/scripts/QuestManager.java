package org.shmo.icfb.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.QuestBuilder;
import org.shmo.icfb.campaign.quests.QuestListener;
import org.shmo.icfb.utilities.ScriptFactory;

import java.util.*;

public class QuestManager implements EveryFrameScript {
    public static final String KEY = "$icfb_QuestManager";

    public static class Factory implements ScriptFactory {
        @Override
        public EveryFrameScript getInstance() {
            EveryFrameScript script = QuestManager.getInstance();
            if (script == null)
                script = new QuestManager();
            return script;
        }
    }

    public static QuestManager getInstance() {
        return (QuestManager)Global.getSector().getMemoryWithoutUpdate().get(KEY);
    }

    private final Map<String, Quest> _idToQuestMap;
    private final Map<Quest, String> _questToIdMap;
    private final Set<Quest> _questSet;
    private final Set<QuestListener> _questListeners;
    private Map<String, Quest> getIdToQuestMap() {
        return _idToQuestMap;
    }
    private Map<Quest, String> getQuestToIdMap() {
        return _questToIdMap;
    }
    private Set<Quest> getQuestSet() {
        return _questSet;
    }
    private Set<QuestListener> getQuestListeners() {
        return _questListeners;
    }
    public List<Quest> getAllQuests() {
        return new ArrayList<>(getQuestSet());
    }

    public QuestManager() {
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
        _idToQuestMap = new HashMap<>();
        _questToIdMap = new HashMap<>();
        _questSet = new HashSet<>();
        _questListeners = new HashSet<>();
    }

    public void addListener(QuestListener listener) {
        getQuestListeners().add(listener);
    }

    public void removeListener(QuestListener listener) {
        getQuestListeners().remove(listener);
    }

    private void notifyListeners(String questId) {
        for (QuestListener listener : getQuestListeners()) {
            listener.notifyQuestComplete(questId);
        }
    }

    public void add(String id, QuestBuilder questBuilder) {
        Quest quest = new Quest();
        questBuilder.build(quest);
        add(id, quest);
    }

    public void add(String id, Quest quest) {
        if (id == null || quest == null)
            return;
        remove(id);
        getIdToQuestMap().put(id, quest);
        getQuestToIdMap().put(quest, id);
        getQuestSet().add(quest);
        quest.start();
        ItCameFromBeyond.Log.info("Quest with id: { " + getId(quest) + " } was started.");
    }

    public Quest getQuest(String id) {
        return getIdToQuestMap().get(id);
    }

    public String getId(Quest quest) {
        return getQuestToIdMap().get(quest);
    }

    public boolean contains(String id) {
        return getIdToQuestMap().containsKey(id);
    }

    public boolean contains(Quest quest) {
        return getQuestSet().contains(quest);
    }

    public void remove(String id) {
        if (id == null)
            return;
        Quest quest = getIdToQuestMap().remove(id);
        if (quest != null) {
            getQuestToIdMap().remove(quest);
            getQuestSet().remove(quest);
            quest.end();
            ItCameFromBeyond.Log.info("Quest with id: { " + id + " } was ended.");
        }
    }

    public void remove(Quest quest) {
        if (quest == null)
            return;
        String id = getQuestToIdMap().remove(quest);
        if (id != null) {
            getIdToQuestMap().remove(id);
            getQuestSet().remove(quest);
            quest.end();
            ItCameFromBeyond.Log.info("Quest with id: { " + id + " } was ended.");
        }
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
        List<Quest> quests = getAllQuests();
        for (Quest quest : quests) {
            if (quest.isComplete()) {
                notifyListeners(getId(quest));
                remove(quest);
                continue;
            }
            quest.advance(amount);
        }
    }
}
