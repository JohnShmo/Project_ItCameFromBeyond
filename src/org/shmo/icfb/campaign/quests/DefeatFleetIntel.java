package org.shmo.icfb.campaign.quests;

import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class DefeatFleetIntel extends LocationIntel {
    private String _targetName = null;

    public void setTargetName(String targetName) {
        _targetName = targetName;
    }

    public String getTargetName() {
        return _targetName;
    }

    @Override
    public void addNotificationBulletPoints(TooltipMakerAPI info) {
        final String name = getTargetName();
        final String desc = "Defeat " + (name != null ? name + "'s fleet." : "the fleet at the destination.");
        info.addPara(desc, 0);
        super.addNotificationBulletPoints(info);
    }

    @Override
    public void addDescriptionBulletPoints(TooltipMakerAPI info) {
        addNotificationBulletPoints(info);
    }
}
