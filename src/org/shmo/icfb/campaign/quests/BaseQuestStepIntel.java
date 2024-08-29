package org.shmo.icfb.campaign.quests;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseQuestStepIntel implements QuestStepIntel {
    private QuestStep _questStep = null;
    private String _title = null;
    private String _icon = null;
    private final Set<String> _tags = new HashSet<>();

    protected QuestStep getQuestStep() {
        return _questStep;
    }

    protected void setTitle(String title) {
        _title = title;
    }

    protected void setIcon(String iconSpriteId) {
        _icon = iconSpriteId;
    }

    protected void addTag(String tag) {
        _tags.add(tag);
    }

    protected void removeTag(String tag) {
        _tags.remove(tag);
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
        return _title;
    }

    @Override
    public String getIcon() {
        return _icon;
    }

    @Override
    public Set<String> getTags() {
        return _tags;
    }

    @Override
    public abstract void addNotificationBody(TooltipMakerAPI info);

    @Override
    public abstract void addNotificationBulletPoint(int index, TooltipMakerAPI info);

    @Override
    public abstract int getNotificationBulletPointCount();

    @Override
    public abstract void addDescriptionBody(TooltipMakerAPI info);

    @Override
    public abstract void addDescriptionBulletPoint(int index, TooltipMakerAPI info);

    @Override
    public abstract int getDescriptionBulletPointCount();

    @Override
    public abstract SectorEntityToken getMapLocation(SectorMapAPI map);

    @Override
    public abstract List<IntelInfoPlugin.ArrowData> getArrowData(SectorMapAPI map);
}
