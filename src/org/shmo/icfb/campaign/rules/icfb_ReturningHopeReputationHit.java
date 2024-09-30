package org.shmo.icfb.campaign.rules;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.ReputationActionResponsePlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondFactions;

import java.util.List;
import java.util.Map;

public class icfb_ReturningHopeReputationHit extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        CoreReputationPlugin.CustomRepImpact impact = new CoreReputationPlugin.CustomRepImpact();
        impact.delta = -0.15f;
        FactionAPI boundless = Global.getSector().getFaction(ItCameFromBeyondFactions.BOUNDLESS);
        if (boundless.getRelToPlayer().getRel() > -0.2f) {
            impact.delta += -0.15f - boundless.getRelToPlayer().getRel();
        }

        Global.getSector().adjustPlayerReputation(
                new CoreReputationPlugin.RepActionEnvelope(CoreReputationPlugin.RepActions.CUSTOM,
                        impact, null, dialog != null ? dialog.getTextPanel() : null, false, true),
                ItCameFromBeyondFactions.BOUNDLESS);
        return true;
    }
}
