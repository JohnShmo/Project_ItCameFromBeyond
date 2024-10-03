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

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {}

    @Override
    public void buttonPressCancelled(Object buttonId, IntelUIAPI ui) {}

    public FactionAPI getFactionForUIColors() {
        return Global.getSector().getPlayerFaction();
    }

    public ButtonAPI addGenericButton(TooltipMakerAPI info, float width, String text, Object data) {
        return addGenericButton(info, width,
                getFactionForUIColors().getBaseUIColor(), getFactionForUIColors().getDarkUIColor(), text, data);
    }

    public ButtonAPI addGenericButton(TooltipMakerAPI info, float width, Color tc, Color bg, String text, Object data) {
        float opad = 10f;
        return info.addButton(text, data, tc, bg,
                (int)(width), 20f, opad * 2f);
    }
}
