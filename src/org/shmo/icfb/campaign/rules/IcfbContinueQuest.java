package org.shmo.icfb.campaign.rules;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.scripts.IcfbQuestManager;

import java.util.List;
import java.util.Map;

public class IcfbContinueQuest extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (params.size() < 1)
            return false;
        Quest quest = IcfbQuestManager.getInstance().getQuest(
                params.get(0).getStringWithTokenReplacement(ruleId, dialog, memoryMap)
        );
        if (quest != null) {
            quest.progress();
            return true;
        }
        return false;
    }
}
