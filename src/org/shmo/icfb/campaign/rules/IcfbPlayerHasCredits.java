package org.shmo.icfb.campaign.rules;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class IcfbPlayerHasCredits extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        float credits;
        try {
            credits = Float.parseFloat(params.get(0).string);
        } catch (Exception unused) {
            return true;
        }
        return Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= credits;
    }
}
