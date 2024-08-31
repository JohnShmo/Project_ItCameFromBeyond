package org.shmo.icfb.campaign.quests.intel;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Set;

public class QuestStepIntelPlugin extends BaseIntelPlugin {
    private final QuestStepIntel _impl;

    public QuestStepIntelPlugin(QuestStepIntel impl) {
        _impl = impl;
    }

    public QuestStepIntel getImpl() {
        return _impl;
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        if (getName() != null)
            info.addTitle(getName());
        _impl.addDescriptionBody(info);
        bullet(info);
        info.setParaFontColor(Misc.getGrayColor());
        _impl.addDescriptionBulletPoints(info);
        info.setParaFontColor(Misc.getTextColor());
        unindent(info);
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        if (getName() != null)
            info.addPara(getName(), getTitleColor(mode), 0);
        _impl.addNotificationBody(info);
        bullet(info);
        info.setParaFontColor(Misc.getGrayColor());
        _impl.addNotificationBulletPoints(info);
        info.setParaFontColor(Misc.getTextColor());
        unindent(info);
    }

    @Override
    public boolean isImportant() {
        return true;
    }

    @Override
    public boolean canTurnImportantOff() {
        return false;
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
}
