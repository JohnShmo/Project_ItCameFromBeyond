package org.shmo.icfb.campaign.rules;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class IcfbMusic extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (params.size() < 1)
            return false;
        String param = params.get(0).string;
        if ("start".equals(param)) {
            if (params.size() < 2)
                return false;
            String id = params.get(1).getStringWithTokenReplacement(ruleId, dialog, memoryMap);
            if (id == null)
                return false;
            Global.getSoundPlayer().playCustomMusic(1, 1, id, true);
            return true;
        }
        if ("stop".equals(param)) {
            Global.getSoundPlayer().restartCurrentMusic();
            return true;
        }
        return false;
    }
}
