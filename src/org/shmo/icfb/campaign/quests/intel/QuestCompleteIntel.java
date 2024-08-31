package org.shmo.icfb.campaign.quests.intel;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.shmo.icfb.campaign.quests.intel.BaseQuestStepIntel;

import java.awt.*;
import java.util.List;

public class QuestCompleteIntel extends BaseQuestStepIntel {
    @Override
    public void addNotificationBody(TooltipMakerAPI info) {
        Color green = new Color(15, 205, 105);
        info.addPara("Complete!", green, 0);
    }

    @Override
    public void addNotificationBulletPoints(TooltipMakerAPI info) {

    }

    @Override
    public void addDescriptionBody(TooltipMakerAPI info) {
        addNotificationBody(info);
    }

    @Override
    public void addDescriptionBulletPoints(TooltipMakerAPI info) {

    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return null;
    }

    @Override
    public List<IntelInfoPlugin.ArrowData> getArrowData(SectorMapAPI map) {
        return null;
    }
}
