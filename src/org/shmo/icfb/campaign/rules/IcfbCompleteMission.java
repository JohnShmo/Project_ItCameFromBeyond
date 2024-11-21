package org.shmo.icfb.campaign.rules;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.campaign.quests.missions.IcfbMission;

import java.util.List;
import java.util.Map;

public class IcfbCompleteMission extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (params.isEmpty())
            return false;
        IcfbMission mission = (IcfbMission)params.get(0).getObject(memoryMap);
        if (mission == null)
            return false;
        mission.completeViaDialog(dialog);
        return true;
    }
}
