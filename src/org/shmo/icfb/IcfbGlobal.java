package org.shmo.icfb;

import org.shmo.icfb.campaign.abilities.ShiftJump;
import org.shmo.icfb.campaign.abilities.ShiftJumpAbilityPlugin;

public class IcfbGlobal {
    public static ShiftJumpAbilityPlugin getPlayerShiftJumpPlugin() {
        return ShiftJumpAbilityPlugin.getPlayerInstance();
    }

    public static ShiftJump getPlayerShiftJump() {
        ShiftJumpAbilityPlugin plugin = getPlayerShiftJumpPlugin();
        if (plugin == null)
            return null;
        return plugin.getImpl();
    }

    public static IcfbSettings getSettings() {
        return IcfbModPlugin.getInstance().getSettings();
    }
}
