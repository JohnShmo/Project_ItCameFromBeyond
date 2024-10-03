package org.shmo.icfb.campaign.quests.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.impl.campaign.intel.contacts.ContactIntel;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.shmo.icfb.campaign.quests.QuestStep;
import org.shmo.icfb.campaign.quests.intel.QuestStepIntel;

import java.awt.*;
import java.util.List;
import java.util.Set;

public abstract class BaseQuestStepIntel implements QuestStepIntel {
    private static final float LIST_ITEM_TEXT_WIDTH = 261f;
    private QuestStep _questStep = null;
    private transient float _bodyPanelWidth = 128;

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

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {}

    @Override
    public void buttonPressCancelled(Object buttonId, IntelUIAPI ui) {}

    @Override
    public void setBodyPanelWidth(float width) {
        _bodyPanelWidth = width;
    }

    @Override
    public float getBodyPanelWidth() {
        return _bodyPanelWidth;
    }
}
