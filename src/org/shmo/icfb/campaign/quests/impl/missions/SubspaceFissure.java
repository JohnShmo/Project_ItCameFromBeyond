package org.shmo.icfb.campaign.quests.impl.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.missions.IcfbMissions;
import org.shmo.icfb.campaign.quests.missions.BaseIcfbMission;
import org.shmo.icfb.campaign.quests.scripts.BaseQuestStepScript;

import java.util.*;

public class SubspaceFissure extends BaseIcfbMission {

    public SubspaceFissure(PersonAPI missionGiver) {
        Data data = getData();
        data.missionGiver = missionGiver;

        data.starSystem = IcfbMisc.pickSystem(true);
        if (data.starSystem == null) {
            data.valid = false;
            return;
        }

        data.creditReward = calculateReward(missionGiver.getMarket().getPrimaryEntity(), data.starSystem.getCenter());
        data.xpReward = 5000;
        data.repReward = 0.1f;
        data.repPenalty = 0.05f;
    }

    private static int calculateReward(SectorEntityToken start, SectorEntityToken objective) {
        final float baseReward = 20000;
        final float rewardPerLY = 1500;
        final float distanceLY = Misc.getDistanceLY(start, objective);
        final float result = baseReward + (rewardPerLY * distanceLY);
        return (int)result;
    }

    @Override
    public Quest create() {
        Quest quest = initQuest();


        addFinalStep(quest);
        return quest;
    }

    @Override
    public Set<String> getIntelTags() {
        return new HashSet<>();
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("icfb_intel", "xent_fis");
    }

    @Override
    public String getId() {
        return IcfbMissions.SUBSPACE_FISSURE;
    }

    @Override
    public String getName() {
        return "Investigate Subspace Fissure";
    }

    @Override
    public String getDescriptionImage() {
        return getData().missionGiver.getPortraitSprite();
    }
}
