package org.shmo.icfb.campaign.quests.intel;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.shmo.icfb.campaign.quests.QuestStep;
import org.shmo.icfb.campaign.quests.intel.QuestStepIntel;

import java.util.List;
import java.util.Set;

public abstract class BaseQuestStepIntel implements QuestStepIntel {
    private QuestStep _questStep = null;

    protected QuestStep getQuestStep() {
        return _questStep;
    }

    @Override
    public void init(QuestStep step) {
        _questStep = step;
    }

    @Override
    public void cleanup() {
        _questStep = null;
    }

    @Override
    public String getName() {
        if (getQuestStep() != null)
            return getQuestStep().quest.getName();
        return null;
    }

    @Override
    public String getIcon() {
        if (getQuestStep() != null)
            return getQuestStep().quest.getIcon();
        return null;
    }

    @Override
    public Set<String> getTags() {
        if (getQuestStep() != null)
            return getQuestStep().quest.getTags();
        return null;
    }

    @Override
    public abstract void addNotificationBody(TooltipMakerAPI info);

    @Override
    public abstract void addNotificationBulletPoints(TooltipMakerAPI info);

    @Override
    public abstract void addDescriptionBody(TooltipMakerAPI info);

    @Override
    public abstract void addDescriptionBulletPoints(TooltipMakerAPI info);

    @Override
    public abstract SectorEntityToken getMapLocation(SectorMapAPI map);

    @Override
    public abstract List<IntelInfoPlugin.ArrowData> getArrowData(SectorMapAPI map);
}
