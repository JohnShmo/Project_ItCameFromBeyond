package org.shmo.icfb.campaign.quests.intel;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.shmo.icfb.campaign.quests.QuestStep;

import java.util.List;
import java.util.Set;

public interface QuestStepIntel {
    void init(QuestStep step);
    void cleanup();
    String getName();
    String getIcon();
    Set<String> getTags();
    void addNotificationBody(TooltipMakerAPI info);
    void addNotificationBulletPoints(TooltipMakerAPI info);
    void addDescriptionBody(TooltipMakerAPI info);
    void addDescriptionBulletPoints(TooltipMakerAPI info);
    void buttonPressConfirmed(Object buttonId, IntelUIAPI ui);
    void buttonPressCancelled(Object buttonId, IntelUIAPI ui);
    SectorEntityToken getMapLocation(SectorMapAPI map);
    List<IntelInfoPlugin.ArrowData> getArrowData(SectorMapAPI map);
}
