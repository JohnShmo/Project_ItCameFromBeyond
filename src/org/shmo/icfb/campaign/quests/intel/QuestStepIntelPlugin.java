package org.shmo.icfb.campaign.quests.intel;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Set;

public class QuestStepIntelPlugin extends BaseIntelPlugin {
    private final QuestStepIntel _impl;

    public QuestStepIntelPlugin(QuestStepIntel impl) {
        _impl = impl;
        setImportant(true);
    }

    public QuestStepIntel getImpl() {
        return _impl;
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        _impl.setBodyPanelWidth(width);
        info.addSpacer(10);
        _impl.addDescriptionBody(info);
        info.addSpacer(10);
        bullet(info);
        _impl.addDescriptionBulletPoints(info);
        unindent(info);
        _impl.addPostDescriptionBody(info);
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        if (getName() != null)
            info.addPara(getName(), getTitleColor(mode), 0);
        info.addSpacer(2);
        _impl.addNotificationBody(info);
        bullet(info);
        info.setParaFontColor(Misc.getGrayColor());
        _impl.addNotificationBulletPoints(info);
        info.setParaFontColor(Misc.getTextColor());
        unindent(info);
    }

    @Override
    public String getIcon() {
        return _impl.getIcon();
    }

    @Override
    protected String getName() {
        return _impl.getName();
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return _impl.getMapLocation(map);
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> set = super.getIntelTags(map);
        set.addAll(_impl.getTags());
        return set;
    }

    @Override
    public String getSmallDescriptionTitle() {
        return getName();
    }

    @Override
    public List<ArrowData> getArrowData(SectorMapAPI map) {
        return _impl.getArrowData(map);
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        _impl.buttonPressConfirmed(buttonId, ui, this);
    }

    @Override
    public void buttonPressCancelled(Object buttonId, IntelUIAPI ui) {
        _impl.buttonPressCancelled(buttonId, ui, this);
    }

    @Override
    public void createConfirmationPrompt(Object buttonId, TooltipMakerAPI prompt) {
        _impl.createConfirmationPrompt(buttonId, prompt);
        super.createConfirmationPrompt(buttonId, prompt);
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        return _impl.doesButtonHaveConfirmDialog(buttonId) || super.doesButtonHaveConfirmDialog(buttonId);
    }
}
