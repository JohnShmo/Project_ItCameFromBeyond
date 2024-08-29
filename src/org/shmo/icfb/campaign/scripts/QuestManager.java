package org.shmo.icfb.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import org.shmo.icfb.campaign.quests.Quest;
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
    private Map<String, Quest> getIdToQuestMap() {
        return _idToQuestMap;
    }
    private Map<Quest, String> getQuestToIdMap() {
        return _questToIdMap;
    }
    private Set<Quest> getQuestSet() {
        return _questSet;
    }
    public List<Quest> getAllQuests() {
        return new ArrayList<>(getQuestSet());
    }

    public QuestManager() {
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
        _idToQuestMap = new HashMap<>();
        _questToIdMap = new HashMap<>();
        _questSet = new HashSet<>();
    }

    public void add(String id, Quest quest) {
        if (id == null || quest == null)
            return;
        remove(id);
        getIdToQuestMap().put(id, quest);
        getQuestToIdMap().put(quest, id);
        getQuestSet().add(quest);
        quest.start();
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
        Quest questToRemove = getIdToQuestMap().remove(id);
        if (questToRemove != null) {
            getQuestToIdMap().remove(questToRemove);
            getQuestSet().remove(questToRemove);
            questToRemove.end();
        }
    }

    public void remove(Quest quest) {
        if (quest == null)
            return;
        String idToRemove = getQuestToIdMap().remove(quest);
        if (idToRemove != null) {
            getIdToQuestMap().remove(idToRemove);
            getQuestSet().remove(quest);
            quest.end();
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
                remove(quest);
                continue;
            }
            quest.advance(amount);
        }
    }
}
