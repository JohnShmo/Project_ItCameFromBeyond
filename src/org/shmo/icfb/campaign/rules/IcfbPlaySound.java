package org.shmo.icfb.campaign.rules;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class IcfbPlaySound extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (params.isEmpty())
            return false;
        String id = params.get(0).getStringWithTokenReplacement(ruleId, dialog, memoryMap);
        float pitch = 1;
        float volume = 1;
        if (params.size() == 2)
            volume = params.get(1).getFloat(memoryMap);
        if (params.size() >= 3) {
            pitch = params.get(1).getFloat(memoryMap);
            volume = params.get(2).getFloat(memoryMap);
        }
        Global.getSoundPlayer().playUISound(id, pitch, volume);
        return true;
    }
}
