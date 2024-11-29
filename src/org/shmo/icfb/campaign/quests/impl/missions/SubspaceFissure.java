package org.shmo.icfb.campaign.quests.impl.missions;

import com.fs.starfarer.api.EveryFrameScriptWithCleanup;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.ai.FleetAssignmentDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.IcfbFactions;
import org.shmo.icfb.campaign.entities.plugins.ShifterRiftCloud;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.missions.IcfbMissions;
import org.shmo.icfb.campaign.quests.missions.BaseIcfbMission;
import org.shmo.icfb.campaign.quests.scripts.BaseQuestStepScript;

import java.awt.*;
import java.util.*;

public class SubspaceFissure extends BaseIcfbMission {

    private SectorEntityToken _fissure = null;
    private CampaignFleetAPI _fleet1 = null;
    private CampaignFleetAPI _fleet2 = null;
    private ShifterRiftCloud _riftCloud = null;

    public SubspaceFissure(PersonAPI missionGiver) {
        Data data = getData();
        data.missionGiver = missionGiver;

        data.targetStarSystem = IcfbMisc.pickSystem(true);
        if (data.targetStarSystem == null) {
            data.valid = false;
            return;
        }

        data.creditReward = calculateReward(
                missionGiver.getMarket().getPrimaryEntity(),
                data.targetStarSystem.getCenter(),
                30000,
                1500
        );
        data.xpReward = 5000;
        data.repReward = 0.05f;
        data.repPenalty = 0.02f;
        data.timeLimitDays = 180f;
        data.targetFaction = IcfbFactions.BOUNDLESS.getFaction();
    }

    private String getFissureId() {
        return getReasonId() + ":" + "subspace_fissure";
    }

    private String getFissureType() {
        return "icfb_subspace_fissure";
    }

    @Override
    public void createMission(Quest quest) {
        addStep(quest, 0, new BaseQuestStepScript() {
            @Override
            public void start() {
                spawnFissure();
            }

            @Override
            public void advance(float deltaTime) {
                spawnPatrolFleetsIfNeeded();
                updatePatrolFleet(_fleet1);
                updatePatrolFleet(_fleet2);
            }

            @Override
            public void end() {
                makeUnimportant(_fissure);
                makeUnimportant(_fleet1);
                makeUnimportant(_fleet2);
            }

            @Override
            public boolean isComplete() {
                return isFissureScanned();
            }
        });

        addStep(quest, 1, new BaseQuestStepScript() {
            @Override
            public boolean isComplete() {
                return isPlayerInRangeOfCommRelay();
            }
        });
    }

    private boolean isPlayerInRangeOfCommRelay() {
        return Global.getSector().getIntelManager().isPlayerInRangeOfCommRelay();
    }

    private boolean isFissureScanned() {
        return _fissure.getMemoryWithoutUpdate().getBoolean("$icfbInvestigated");
    }

    private void makeUnimportant(SectorEntityToken entity) {
        if (entity != null)
            Misc.makeUnimportant(entity, null);
    }

    private void updatePatrolFleet(CampaignFleetAPI fleet) {
        if (fleet != null) {
            if (isVisibleToSensorsOf(fleet)) {
                markPlayerAsSeen();
                FleetAssignmentDataAPI assignmentData = fleet.getCurrentAssignment();
                if (assignmentData == null || !FleetAssignment.DEFEND_LOCATION.equals(assignmentData.getAssignment())) {
                    fleet.clearAssignments();
                    fleet.addAssignment(FleetAssignment.DEFEND_LOCATION, _fissure, 10);
                    fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, getData().targetStarSystem.getStar(), 100000f);
                }
            }
        }
    }

    private boolean isVisibleToSensorsOf(CampaignFleetAPI fleet) {
        return Global.getSector().getPlayerFleet().isVisibleToSensorsOf(fleet);
    }

    private void markPlayerAsSeen() {
        _fissure.getMemoryWithoutUpdate().set("$icfbSeen", true, 2);
    }

    private void spawnPatrolFleetsIfNeeded() {
        if (Global.getSector().getPlayerFleet().getContainingLocation() == getData().targetStarSystem) {
            if (_fleet1 == null)
                _fleet1 = createPatrolFleet();
            if (_fleet2 == null)
                _fleet2 = createPatrolFleet();
        }
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
                5000f + Misc.random.nextFloat() * 9000f,
                360
        );
        _fissure.setDiscoverable(true);
        _fissure.setDiscoveryXP(2000f);
        _fissure.getMemoryWithoutUpdate().set("$icfbSubspaceFissure", true);
        _riftCloud = ShifterRiftCloud.create(
                _fissure.getContainingLocation(),
                _fissure.getLocation().x,
                _fissure.getLocation().y,
                _fissure.getRadius()
        );
        _riftCloud.getEntity().setExtendedDetectedAtRange(2000f);
        _riftCloud.getEntity().setDetectionRangeDetailsOverrideMult(2000f);
        _riftCloud.getEntity().setSensorProfile(2000f);
        _fissure.addScript(new EveryFrameScriptWithCleanup() {
            @Override
            public void cleanup() {
                _riftCloud.expire();
            }

            @Override
            public boolean isDone() {
                return _fissure == null || _fissure.isExpired() || !_fissure.isAlive();
            }

            @Override
            public boolean runWhilePaused() {
                return false;
            }

            @Override
            public void advance(float v) {
                try {
                    _riftCloud.getEntity().setSensorProfile(2000f);
                    _riftCloud.setLocation(_fissure.getLocation().x, _fissure.getLocation().y);
                    _riftCloud.setFacing(_fissure.getFacing());
                } catch (Exception ignored) {}
            }
        });
        Misc.makeImportant(_fissure, null);
    }

    private CampaignFleetAPI createPatrolFleet() {
        Data data = getData();

        CampaignFleetAPI fleet = createFleet(
                data.targetFaction.getId(),
                FleetTypes.PATROL_LARGE,
                data.targetFaction.getDisplayName() + " " + data.targetFaction.getFleetTypeName(FleetTypes.PATROL_LARGE),
                250,
                true,
                true,
                _fissure,
                null,
                FleetAssignment.PATROL_SYSTEM,
                null
        );
        Misc.makeHostile(fleet);
        fleet.setTransponderOn(true);
        return fleet;
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
        if (_riftCloud != null) {
            _riftCloud.expire();
            _riftCloud = null;
        }
    }

    @Override
    public Set<String> getIntelTags() {
        return IcfbMisc.setOf(Tags.INTEL_EXPLORATION);
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("icfb_intel", "fis");
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
                            " %s by prior investigators. It's likely being monitored by " + factionAOrAn +
                            " %s defense fleet.",
                    0,
                    new Color[] {Misc.getHighlightColor(), factionColor},
                    data.targetStarSystem.getName(),
                    factionName
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
