package org.shmo.icfb.campaign.quests.impl.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.QuestStep;
import org.shmo.icfb.campaign.quests.missions.BaseIcfbMission;
import org.shmo.icfb.campaign.quests.missions.IcfbMissions;
import org.shmo.icfb.campaign.quests.scripts.BaseQuestStepScript;

import java.util.Set;

public class StealPhaseTech extends BaseIcfbMission {

    CampaignFleetAPI _fleet = null;
    PlanetAPI _planetWithBase = null;

    public StealPhaseTech(PersonAPI person) {
        Data data = getData();
        data.missionGiver = person;

        data.targetStarSystem = IcfbMisc.pickSystem(new IcfbMisc.SystemPickerPredicate() {
            @Override
            public boolean isValid(StarSystemAPI starSystem) {
                return starSystem.isProcgen()
                        && starSystem.getPlanets().size() >= 5
                        && !starSystem.hasTag(Tags.THEME_REMNANT_RESURGENT)
                        && !starSystem.hasTag(Tags.THEME_REMNANT_SECONDARY);
            }
        });
        if (data.targetStarSystem == null) {
            data.valid = false;
            return;
        }

        _planetWithBase = IcfbMisc.pickPlanet(data.targetStarSystem);
        if (_planetWithBase == null) {
            data.valid = false;
            return;
        }

        data.creditReward = calculateReward(
                data.missionGiver.getMarket().getPrimaryEntity(),
                data.targetStarSystem.getCenter(),
                40000,
                1100
        );

        data.xpReward = 5000;
        data.repReward = 0.07f;
        data.repPenalty = 0.03f;
        data.timeLimitDays = 240f;
        data.targetFaction = Global.getSector().getFaction(Factions.TRITACHYON);
    }

    @Override
    protected void createMission(Quest quest) {
        addStep(quest, 0, new BaseQuestStepScript() {
            @Override
            public void start() {

            }

            @Override
            public void advance(float deltaTime) {

            }

            @Override
            public void end() {

            }
        });
    }

    @Override
    public Set<String> getIntelTags() {
        return IcfbMisc.setOf(Tags.INTEL_EXPLORATION);
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("icfb_intel", "phs");
    }

    @Override
    public String getId() {
        return IcfbMissions.STEAL_PHASE_TECH;
    }

    @Override
    public String getName() {
        return "Find Secret Research Facility";
    }
}
