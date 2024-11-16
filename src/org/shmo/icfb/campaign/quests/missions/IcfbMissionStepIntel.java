package org.shmo.icfb.campaign.quests.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.campaign.quests.intel.BaseQuestStepIntel;
import org.shmo.icfb.campaign.quests.intel.QuestStepIntelPlugin;
import org.shmo.icfb.campaign.scripts.IcfbQuestManager;
import org.shmo.icfb.utilities.ShmoGuiUtils;

import java.awt.*;
import java.util.List;

public class IcfbMissionStepIntel extends BaseQuestStepIntel {
    private final BaseIcfbMission _mission;
    private final int _stageIndex;

    enum DefaultButtons {
        ABANDON
    }

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
            ShmoGuiUtils.addCenteredImage(info, _mission.getDescriptionImage(), getBodyPanelWidth(), 0);
        }
        info.addSpacer(10);
        _mission.addDescriptionBody(info, _stageIndex);
    }

    @Override
    public void addDescriptionBulletPoints(TooltipMakerAPI info) {
        info.addSpacer(10);
        addBulletPoints(info);
    }

    @Override
    public void addPostDescriptionBody(TooltipMakerAPI info) {
        if (!_mission.getData().canAbandon)
            return;
        ShmoGuiUtils.addGenericButton(info, getBodyPanelWidth(), "Abandon", DefaultButtons.ABANDON);
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui, QuestStepIntelPlugin plugin) {
        PersonAPI person = _mission.getData().missionGiver;
        float timePassedDays = Global.getSector().getClock().getElapsedDaysSince(_mission.getData().startTimeStamp);

        if (buttonId == DefaultButtons.ABANDON) {
            if (timePassedDays < 1 || _mission.getData().repPenalty <= 0) {
                IcfbQuestManager.getInstance().remove(person.getId() + ":" + _mission.getId());
            } else {
                _mission.fail();
            }
            ui.recreateIntelUI();
        }
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        if (buttonId == DefaultButtons.ABANDON) {
            return true;
        }
        return super.doesButtonHaveConfirmDialog(buttonId);
    }

    @Override
    public void createConfirmationPrompt(Object buttonId, TooltipMakerAPI prompt) {
        BaseIcfbMission.Data data = _mission.getData();
        PersonAPI person = data.missionGiver;
        float timePassedDays = Global.getSector().getClock().getElapsedDaysSince(data.startTimeStamp);

        if (buttonId == DefaultButtons.ABANDON) {
            if (data.repPenalty > 0) {
                if (timePassedDays < 1) {
                    prompt.addPara(
                            "Less than a day has passed since accepting this mission, meaning you can abandon it "
                                    + "without penalty. Are you sure you want to?", 0
                    );
                } else if (!_mission.getData().repAppliesToFaction) {
                    prompt.addPara(
                            "Abandoning this mission will incur a reputation penalty with %s. Are you sure you want to?",
                            0,
                            person.getFaction().getBaseUIColor(),
                            person.getName().getFullName()
                    );
                } else {
                    prompt.addPara(
                            "Abandoning this mission will incur a reputation penalty with %s " +
                                    "and %s. Are you sure you want to?",
                            0,
                            person.getFaction().getBaseUIColor(),
                            person.getName().getFullName(),
                            person.getFaction().getDisplayNameWithArticle()
                    );
                }
            } else {
                prompt.addPara(
                        "Are you sure you want to abandon this mission?", 0
                );
            }
        }
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
        if (data.targetLocation != null)
            return data.targetLocation;
        else if (data.targetMarket != null)
            return data.targetMarket.getPrimaryEntity();
        else if (data.targetStarSystem != null)
            return data.targetStarSystem.getCenter();
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
