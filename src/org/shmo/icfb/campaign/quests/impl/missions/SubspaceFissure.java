package org.shmo.icfb.campaign.quests.impl.missions;

import com.fs.starfarer.api.EveryFrameScriptWithCleanup;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.CommRelayEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.SectorProcGen;
import com.fs.starfarer.api.impl.campaign.procgen.themes.ThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.Themes;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.magiclib.util.MagicCampaign;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.IcfbFactions;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.missions.IcfbMissions;
import org.shmo.icfb.campaign.quests.missions.BaseIcfbMission;
import org.shmo.icfb.campaign.quests.scripts.BaseQuestStepScript;

import java.awt.*;
import java.util.*;

public class SubspaceFissure extends BaseIcfbMission {

    private SectorEntityToken _fissure = null;
    private CampaignFleetAPI _fleet = null;

    public SubspaceFissure(PersonAPI missionGiver) {
        Data data = getData();
        data.missionGiver = missionGiver;

        data.targetStarSystem = IcfbMisc.pickSystem(true);
        if (data.targetStarSystem == null) {
            data.valid = false;
            return;
        }

        data.creditReward = calculateReward(missionGiver.getMarket().getPrimaryEntity(), data.targetStarSystem.getCenter());
        data.xpReward = 5000;
        data.repReward = 0.1f;
        data.repPenalty = 0.05f;
        data.timeLimitDays = 180f;
        data.targetFaction = IcfbFactions.BOUNDLESS.getFaction();
    }

    private static int calculateReward(SectorEntityToken start, SectorEntityToken objective) {
        final float baseReward = 20000;
        final float rewardPerLY = 1500;
        final float distanceLY = Misc.getDistanceLY(start, objective);
        final float result = baseReward + (rewardPerLY * distanceLY);
        return (int)result;
    }

    private String getFissureId() {
        return getData().missionGiver.getId() + ":" + getId() + ":" + "subspace_fissure";
    }

    private String getFissureType() {
        return "icfb_subspace_fissure";
    }

    @Override
    public Quest create() {
        Quest quest = initQuest();

        addStep(quest, 0, new BaseQuestStepScript() {
            @Override
            public void start() {
                spawnFissure();
            }

            @Override
            public void advance(float deltaTime) {
                if (_fleet == null && Global.getSector().getPlayerFleet().getContainingLocation() == getData().targetStarSystem) {
                    spawnFleet();
                }
                if (_fleet != null) {
                    if (Global.getSector().getPlayerFleet().isVisibleToSensorsOf(_fleet)) {
                        _fissure.getMemoryWithoutUpdate().set("$icfbSeen", true, 1);
                    }
                }
            }

            @Override
            public void end() {
                if (_fissure != null)
                    Misc.makeUnimportant(_fissure, null);
                if (_fleet != null)
                    Misc.makeUnimportant(_fleet, null);
            }

            @Override
            public boolean isComplete() {
                return _fissure.getMemoryWithoutUpdate().getBoolean("$icfbInvestigated");
            }
        });

        addStep(quest, 1, new BaseQuestStepScript() {
            @Override
            public void start() {

            }

            @Override
            public void advance(float deltaTime) {

            }

            @Override
            public void end() {

            }

            @Override
            public boolean isComplete() {
                return Global.getSector().getIntelManager().isPlayerInRangeOfCommRelay();
            }
        });

        addFinalStep(quest);
        return quest;
    }

    private void spawnFissure() {
        Data data = getData();
        _fissure = data.targetStarSystem.addCustomEntity(
                getFissureId(),
                "Subspace Fissure",
                getFissureType(),
                Factions.NEUTRAL
        );
        _fissure.setCircularOrbit(
                data.targetStarSystem.getCenter(),
                Misc.random.nextFloat() * 360,
                5000f + Misc.random.nextFloat() * 8000f,
                360
        );
        _fissure.setDiscoverable(true);
        _fissure.setDiscoveryXP(2000f);
        _fissure.getMemoryWithoutUpdate().set("$icfbSubspaceFissure", true);
        Misc.makeImportant(_fissure, null);
    }

    private void spawnFleet() {
        Data data = getData();

        _fleet = createFleet(
                data.targetFaction.getId(),
                FleetTypes.PATROL_LARGE,
                data.targetFaction.getDisplayName() + " " + data.targetFaction.getFleetTypeName(FleetTypes.PATROL_LARGE),
                275,
                true,
                true,
                _fissure,
                null,
                FleetAssignment.PATROL_SYSTEM,
                null
        );
        Misc.makeHostile(_fleet);
        _fleet.setTransponderOn(true);
    }

    private void despawnFissure() {
        if (_fissure != null) {
            final LocationAPI location = _fissure.getContainingLocation();
            if (location != null) {
                Misc.makeUnimportant(_fissure, null);
                Misc.fadeAndExpire(_fissure, 3);
            }
            _fissure = null;
        }
    }

    @Override
    public Set<String> getIntelTags() {
        final Set<String> tags = new HashSet<>();
        tags.add(Tags.INTEL_EXPLORATION);
        return tags;
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

    @Override
    protected void cleanupImpl() {
        despawnFissure();
    }

    @Override
    public void addBulletPoints(TooltipMakerAPI info, int stageIndex) {
        if (stageIndex == 0) {
            info.addPara("Locate the subspace fissure in the %s", 0, Misc.getHighlightColor(), getLocationName());
        }
        if (stageIndex == 1) {
            info.addPara("Get within range of a comm relay to complete the mission", 0);
        }
    }

    @Override
    public void addDescriptionBody(TooltipMakerAPI info, int stageIndex) {
        Data data = getData();
        String factionName = "[UNDEFINED]";
        String factionAOrAn = "[UNDEFINED]";
        Color factionColor = Misc.getTextColor();
        if (data.targetFaction != null) {
            factionName = data.targetFaction.getDisplayName();
            factionAOrAn = data.targetFaction.getPersonNamePrefixAOrAn();
            factionColor = data.targetFaction.getBaseUIColor();
        }

        if (stageIndex == 0) {
            info.addPara(
                    "Locate and examine a spacial anomaly known as a 'subspace fissure'." +
                            " The fissure was last spotted some distance away from the center of the" +
                            " %s by prior investigators. It's likely being monitored by" +
                            " %s defense fleet.",
                    0,
                    new Color[] {Misc.getHighlightColor(), factionColor},
                    data.targetStarSystem.getName(),
                    factionAOrAn + " " + factionName
            );
            if (Global.getSector().getPlayerFleet().hasAbility(Abilities.GRAVITIC_SCAN)) {
                info.addPara(
                        "Your %s should help you locate the fissure.",
                        10,
                        Misc.getHighlightColor(),
                        "Neutrino Detector");
            }
        }

        if (stageIndex == 1) {
            info.addPara("You successfully found and scanned the subspace fissure. " +
                    "Get within range of a comm relay to complete the mission", 0);
        }
    }
}
