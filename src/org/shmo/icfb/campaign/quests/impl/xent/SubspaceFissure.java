package org.shmo.icfb.campaign.quests.impl.xent;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;
import org.jetbrains.annotations.NotNull;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;
import org.shmo.icfb.campaign.quests.xent.XentMissions;

import java.util.List;
import java.util.Random;

public class SubspaceFissure implements QuestFactory {

    public static class Data {
        public PersonAPI missionGiver = null;
        public StarSystemAPI starSystem = null;
        public int creditReward = 0;
    }

    private final Data _data = new Data();

    public Data getData() {
        return _data;
    }

    public SubspaceFissure(PersonAPI missionGiver) {
        _data.missionGiver = missionGiver;
        _data.starSystem = pickSystem();
        _data.creditReward = calculateReward(missionGiver.getMarket().getPrimaryEntity(), _data.starSystem.getCenter());
    }

    @NotNull
    private static StarSystemAPI pickSystem() {
        Random random = Misc.random;
        final List<StarSystemAPI> starSystems = Global.getSector().getStarSystems();
        final float chanceDelta = 0.01f;
        float chance = 0.01f;
        StarSystemAPI pickedSystem = null;
        while (pickedSystem == null) {
            for (StarSystemAPI starSystem : starSystems) {
                if (!starSystem.isProcgen())
                    continue;
                if (random.nextFloat() < chance) {
                    pickedSystem = starSystem;
                    break;
                }
            }
            chance += chanceDelta;
        }
        return pickedSystem;
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
        Quest quest = new Quest(XentMissions.SUBSPACE_FISSURE);
        quest.setName("Investigate Subspace Fissure");
        quest.setIcon(Global.getSettings().getSpriteName("icfb_intel", "xent_fis"));

        // TODO: Build quest

        return quest;
    }

}
