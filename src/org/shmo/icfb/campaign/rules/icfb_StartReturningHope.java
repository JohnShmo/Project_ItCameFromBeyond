package org.shmo.icfb.campaign.rules;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.campaign.quests.impl.factories.ReturningHopeFactory;
import org.shmo.icfb.campaign.scripts.QuestManager;

import java.util.List;
import java.util.Map;

public class icfb_StartReturningHope extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        QuestManager.getInstance().add(new ReturningHopeFactory());
        return true;
    }
}
