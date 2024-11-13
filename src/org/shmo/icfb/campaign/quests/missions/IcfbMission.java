package org.shmo.icfb.campaign.quests.missions;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IcfbMission extends QuestFactory {
    boolean callEvent(String ruleId, String action, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap);
    Set<String> getIntelTags();
    String getIcon();
    String getId();
    String getName();
    String getDescriptionImage();
    void addBulletPoints(TooltipMakerAPI info, int stageIndex);
    void addDescriptionBody(TooltipMakerAPI info, int stageIndex);
    SectorEntityToken getMapLocation();
    int getCreditReward();
    String getLocationName();
    String getTargetFactionName();
    String getTargetMarketName();
    float getTimeLimitDays();
    boolean isFailed();
    boolean isValid();
    float getMinimumRepLevel();
    void cleanup();
}
