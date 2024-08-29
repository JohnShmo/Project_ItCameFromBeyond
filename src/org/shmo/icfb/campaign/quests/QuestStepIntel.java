package org.shmo.icfb.campaign.quests;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.util.List;
import java.util.Set;

public interface QuestStepIntel {
    void init(QuestStep step);
    void cleanup();
    String getName();
    String getIcon();
    Set<String> getTags();
    void addNotificationBody(TooltipMakerAPI info);
    void addNotificationBulletPoint(int index, TooltipMakerAPI info);
    int getNotificationBulletPointCount();
    void addDescriptionBody(TooltipMakerAPI info);
    void addDescriptionBulletPoint(int index, TooltipMakerAPI info);
    int getDescriptionBulletPointCount();
    SectorEntityToken getMapLocation(SectorMapAPI map);
    List<IntelInfoPlugin.ArrowData> getArrowData(SectorMapAPI map);
}
