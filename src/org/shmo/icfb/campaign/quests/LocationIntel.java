package org.shmo.icfb.campaign.quests;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;

public class LocationIntel extends BaseQuestStepIntel {
    private SectorEntityToken _targetEntity = null;
    private String _locationHint = null;

    public void setTarget(SectorEntityToken targetEntity) {
        _targetEntity = targetEntity;
    }

    public SectorEntityToken getTarget() {
        return _targetEntity;
    }

    public void setLocationHint(String locationHint) {
        _locationHint = locationHint;
    }

    public String getLocationHint() {
        return _locationHint;
    }

    @Override
    public void addNotificationBody(TooltipMakerAPI info) {}

    private void addBulletPointsImpl(TooltipMakerAPI info) {
        SectorEntityToken target = getTarget();
        if (target == null)
            return;

        if (!target.isInHyperspace()) {
            if (Global.getSector().getPlayerFleet().getContainingLocation() != target.getContainingLocation()) {
                info.addPara("Make your way to the %s system.",
                        0, Misc.getHighlightColor(),
                        target.getContainingLocation().getNameWithNoType());
            } else {
                String desc;
                if (getLocationHint() != null) {
                    desc = getLocationHint();
                } else {
                    desc = "somewhere in";
                }
                info.addPara("Your target is " + desc + " the %s system.",
                        0, Misc.getHighlightColor(),
                        getTarget().getContainingLocation().getNameWithNoType());
            }
        } else {
            info.addPara("Go to the target location in hyperspace.", 0);
        }
    }

    @Override
    public void addNotificationBulletPoints(TooltipMakerAPI info) {
        addBulletPointsImpl(info);
    }

    @Override
    public void addDescriptionBody(TooltipMakerAPI info) {}

    @Override
    public void addDescriptionBulletPoints(TooltipMakerAPI info) {
        addBulletPointsImpl(info);
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        SectorEntityToken target = getTarget();
        if (target == null)
            return null;
        if (target.isInHyperspace())
            return target;
        StarSystemAPI system = target.getStarSystem();
        return Misc.getDistressJumpPoint(system);
    }

    @Override
    public List<IntelInfoPlugin.ArrowData> getArrowData(SectorMapAPI map) {
        return null;
    }
}
