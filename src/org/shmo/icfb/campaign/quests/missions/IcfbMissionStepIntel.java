package org.shmo.icfb.campaign.quests.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.campaign.quests.intel.BaseQuestStepIntel;

import java.util.List;

public class IcfbMissionStepIntel extends BaseQuestStepIntel {
    private final BaseIcfbMission _mission;
    private final int _stageIndex;

    public IcfbMissionStepIntel(BaseIcfbMission mission, int stageIndex) {
        _mission = mission;
        _stageIndex = stageIndex;
    }

    @Override
    public void addNotificationBody(TooltipMakerAPI info) {}

    @Override
    public void addNotificationBulletPoints(TooltipMakerAPI info) {
        _mission.addBulletPoints(info, _stageIndex);
        addBulletPoints(info);
    }

    @Override
    public void addDescriptionBody(TooltipMakerAPI info) {
        if (_mission.getDescriptionImage() != null) {
            info.addImage(_mission.getDescriptionImage(), getBodyPanelWidth(), 0);
        }
        info.addSpacer(10);
        _mission.addDescriptionBody(info, _stageIndex);
    }

    @Override
    public void addDescriptionBulletPoints(TooltipMakerAPI info) {
        info.addSpacer(10);
        addBulletPoints(info);
    }

    private void addBulletPoints(TooltipMakerAPI info) {
        BaseIcfbMission.Data data = _mission.getData();
        if (data.creditReward != 0)
            info.addPara("%s reward", 0, Misc.getHighlightColor(), Misc.getDGSCredits(data.creditReward));
        if (data.timeLimitDays > 0) {
            float daysRemaining = data.timeLimitDays - (Global.getSector().getClock().getElapsedDaysSince(data.startTimeStamp));
            if (daysRemaining < 0)
                daysRemaining = 0;
            info.addPara("%s days to complete", 0, Misc.getHighlightColor(), Misc.getWithDGS(daysRemaining));
        }
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        BaseIcfbMission.Data data = _mission.getData();
        if (data.target != null)
            return data.target;
        else if (data.starSystem != null)
            return data.starSystem.getCenter();
        return null;
    }

    @Override
    public List<IntelInfoPlugin.ArrowData> getArrowData(SectorMapAPI map) {
        return null;
    }

    @Override
    public boolean forceNoMessage() {
        return _mission.isFailed();
    }
}
