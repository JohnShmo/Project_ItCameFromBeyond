package org.shmo.icfb.campaign.quests.impl.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.missions.BaseIcfbMission;
import org.shmo.icfb.campaign.quests.missions.IcfbMissions;
import org.shmo.icfb.campaign.quests.scripts.BaseQuestStepScript;
import org.shmo.icfb.campaign.scripts.temp.IcfbFleetSuspicion;

import java.awt.*;
import java.util.Random;
import java.util.Set;

public class StealPhaseTech extends BaseIcfbMission {

    public static final String IS_BASE_KEY = "$icfbPhsIsBase";
    public static final String PLAYER_INTERACTED_KEY = "$icfbPhsInteracted";
    public static final String FLEET_KEY = "$icfPhsFleet";
    public static final String FLEET_PROGRESS_KEY = "$icfPhsFleetProgress";
    public static final String RUNNING_AWAY_KEY = "$icfbPhsRunningAway";
    public static final String FLEET_WAITING_TO_PROGRESS_KEY = "$icfbPhsWaitingToProgress";
    public static final String FLEET_SEEN_IN_ORBIT = "$icfbPhsSeenInOrbit";
    public static final String MARINE_COUNT_KEY = "$icfbPhsMarineCount";
    public static final int MARINE_COUNT = 100;

    CampaignFleetAPI _fleet = null;
    FleetEventListener _fleetEventListener = null;
    boolean _fleetWasKilled = false;
    PlanetAPI _planetWithBase = null;
    Vector2f _runDestination = null;

    public StealPhaseTech(PersonAPI person) {
        Data data = getData();
        data.missionGiver = person;

        data.targetStarSystem = IcfbMisc.pickSystem(new IcfbMisc.SystemPickerPredicate() {
            @Override
            public boolean isValid(StarSystemAPI starSystem) {
                boolean sanityChecks = starSystem.isProcgen()
                        && starSystem.getPlanets().size() >= 5
                        && !starSystem.hasTag(Tags.THEME_REMNANT_RESURGENT)
                        && !starSystem.hasTag(Tags.THEME_REMNANT_SECONDARY);
                for (PlanetAPI planet : starSystem.getPlanets()) {
                    if (planet.getTypeId().equals(StarTypes.NEUTRON_STAR)) // This would be insanely annoying otherwise
                        return false;
                }
                return sanityChecks;
            }
        });
        if (data.targetStarSystem == null) {
            data.valid = false;
            return;
        }

        _planetWithBase = IcfbMisc.pickUncolonizedPlanet(data.targetStarSystem);
        if (_planetWithBase == null) {
            data.valid = false;
            return;
        }

        data.creditReward = calculateReward(
                data.missionGiver.getMarket().getPrimaryEntity(),
                data.targetStarSystem.getCenter(),
                70000,
                750
        ) + data.targetStarSystem.getPlanets().size() * 5000;

        data.xpReward = 5000;
        data.repReward = 0.07f;
        data.repPenalty = 0.03f;
        data.timeLimitDays = 240f;
        data.targetFaction = Global.getSector().getFaction(Factions.TRITACHYON);
        Global.getSector().getMemoryWithoutUpdate().set(MARINE_COUNT_KEY, MARINE_COUNT);
    }

    @Override
    protected void createMission(Quest quest) {
        addStep(quest, 0, new BaseQuestStepScript() {
            @Override
            public void start() {
                initFleet();
            }

            @Override
            public void advance(float deltaTime) {
                Global.getSector().getMemoryWithoutUpdate().set(MARINE_COUNT_KEY, MARINE_COUNT);
                if (_fleet != null && !_fleet.isExpired())
                    updateFleet(_fleet, deltaTime);
                if (_fleetWasKilled)
                    getData().failed = true;
            }

            @Override
            public void end() {
                cleanupStage1();
            }

            @Override
            public boolean isComplete() {
                return _planetWithBase.getMemoryWithoutUpdate().getBoolean(PLAYER_INTERACTED_KEY);
            }
        });

        addStep(quest, 1, new BaseQuestStepScript() {
            @Override
            public void start() {
                initStep2();
            }

            @Override
            public void advance(float deltaTime) {
                ensureCompletable();
            }

            @Override
            public void end() {
                cleanupStep2();
            }
        });
    }

    private void initStep2() {
        Data data = getData();
        data.targetStarSystem = data.missionGiver.getMarket().getStarSystem();
        data.targetLocation = data.missionGiver.getMarket().getPrimaryEntity();
        data.targetMarket = data.missionGiver.getMarket();
        Misc.makeImportant(data.targetLocation, getReasonId());
        Misc.makeImportant(data.missionGiver, getReasonId());
    }

    private void cleanupStep2() {
        Misc.makeUnimportant(getData().targetLocation, getReasonId());
        Misc.makeUnimportant(getData().missionGiver, getReasonId());
        getData().missionGiver.getMemoryWithoutUpdate().unset("$icfbPhs_complete");
        getData().missionGiver.getMemoryWithoutUpdate().unset("$" + getId() + "_ref");
    }

    private void ensureCompletable() {
        Data data = getData();
        if (!data.missionGiver.getMemoryWithoutUpdate().getBoolean("$icfbPhs_complete"))
            data.missionGiver.getMemoryWithoutUpdate().set("$icfbPhs_complete", true);
        if (!data.missionGiver.getMemoryWithoutUpdate().contains("$" + getId() + "_ref"))
            data.missionGiver.getMemoryWithoutUpdate().set("$" + getId() + "_ref", this);
    }

    private void initFleet() {
        final Data data = getData();
        _fleet = createFleet(
                data.targetFaction.getId(),
                FleetTypes.TASK_FORCE,
                data.targetFaction.getDisplayName() + " Supply Fleet",
                120,
                true,
                true
        );
        MemoryAPI memory = _fleet.getMemoryWithoutUpdate();
        memory.set(MemFlags.FLEET_IGNORED_BY_OTHER_FLEETS, true);
        memory.set(MemFlags.FLEET_IGNORES_OTHER_FLEETS, true);
        memory.set(MemFlags.MEMORY_KEY_MAKE_NON_HOSTILE, true);
        memory.set(MemFlags.MEMORY_KEY_NO_JUMP, true);
        memory.set(FLEET_KEY, true);
        data.targetStarSystem.addEntity(_fleet);
        _fleet.setLocation(_planetWithBase.getLocation().x, _planetWithBase.getLocation().y);
        _fleet.setTransponderOn(false);
        _fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, data.targetStarSystem.getStar(), 100000f);
        _fleetEventListener = new FleetEventListener() {
            @Override
            public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
                if (reason.equals(CampaignEventListener.FleetDespawnReason.DESTROYED_BY_BATTLE))
                    _fleetWasKilled = true;
            }

            @Override
            public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {
                if (battle.isPlayerInvolved()) {
                    _fleetWasKilled = true;
                    despawnFleet(_fleet, _fleet);
                }
            }
        };
        _fleet.addEventListener(_fleetEventListener);
        memory.set(FLEET_PROGRESS_KEY, 0);
    }

    private void updateFleet(CampaignFleetAPI fleet, float deltaTime) {
        if (!IcfbFleetSuspicion.fleetHasSuspicion(fleet))
            IcfbFleetSuspicion.addToFleet(fleet);
        Data data = getData();
        final MemoryAPI memory = fleet.getMemoryWithoutUpdate();
        final float susLevel = IcfbFleetSuspicion.getFleetSuspicion(fleet);
        final boolean playerCanSee = fleet.isVisibleToPlayerFleet();
        if (memory.getBoolean(RUNNING_AWAY_KEY) && _runDestination != null) {
            memory.set(FLEET_WAITING_TO_PROGRESS_KEY, 25f);
            memory.unset(FLEET_SEEN_IN_ORBIT);
            fleet.setMoveDestinationOverride(_runDestination.x, _runDestination.y);
            return;
        }
        if (susLevel >= 1) {
            memory.set(FLEET_PROGRESS_KEY, 0);
            memory.unset(FLEET_SEEN_IN_ORBIT);
            memory.set(RUNNING_AWAY_KEY, true, 0.5f);
            _runDestination = Misc.pickLocationNotNearPlayer(fleet.getContainingLocation(), fleet.getLocation(), 4000f);
            fleet.getAbility(Abilities.EMERGENCY_BURN).activate();
            return;
        }
            if (
                    playerCanSee &&
                            (fleet.getCurrentAssignment() == null
                            || !fleet.getCurrentAssignment().getAssignment().equals(FleetAssignment.GO_TO_LOCATION)
                            ) && memory.getFloat(FLEET_WAITING_TO_PROGRESS_KEY) <= 0
            ) {
                if (memory.getInt(FLEET_PROGRESS_KEY) > 2) {
                    fleet.clearAssignments();
                    IcfbFleetSuspicion.removeFromFleet(fleet);
                    despawnFleet(fleet, _planetWithBase);
                    Misc.setFlagWithReason(_planetWithBase.getMemoryWithoutUpdate(), IS_BASE_KEY, getReasonId(), true, data.timeLimitDays);
                    Misc.makeImportant(_planetWithBase, getReasonId());
                    data.targetLocation = _planetWithBase;
                    _fleet = null;
                    return;
                }

                PlanetAPI nextDest;
                if (memory.getInt(FLEET_PROGRESS_KEY) < 2) {
                    nextDest = IcfbMisc.pickUncolonizedPlanet(data.targetStarSystem);
                } else {
                    nextDest = _planetWithBase;
                }
                Random random = Misc.random;
                float chance = random.nextFloat();
                if (chance < 0.5f && nextDest != _planetWithBase) {
                    fleet.clearAssignments();
                    fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, fleet.getStarSystem().getStar(), 100000);
                    memory.set(FLEET_WAITING_TO_PROGRESS_KEY, 25f);
                    memory.set(FLEET_PROGRESS_KEY, memory.getInt(FLEET_PROGRESS_KEY) + 1);
                    memory.unset(FLEET_SEEN_IN_ORBIT);
                    return;
                }

                fleet.clearAssignments();
                fleet.addAssignment(
                        FleetAssignment.GO_TO_LOCATION,
                        nextDest, (Misc.getDistance(_fleet, nextDest) / Math.max(_fleet.getFleetData().getTravelSpeed(), 1)) * 10,
                        new Script() {
                            @Override
                            public void run() {
                                memory.set(FLEET_WAITING_TO_PROGRESS_KEY, 20f);
                            }
                        });
                fleet.addAssignment(FleetAssignment.ORBIT_PASSIVE, nextDest, 100000f);
                memory.set(FLEET_PROGRESS_KEY, memory.getInt(FLEET_PROGRESS_KEY) + 1);
                memory.unset(FLEET_SEEN_IN_ORBIT);

            } else if (playerCanSee || memory.getBoolean(FLEET_SEEN_IN_ORBIT)) {
                memory.set("$icfbPhsWaitingToProgress", memory.getFloat("$icfbPhsWaitingToProgress") - deltaTime);
                if (fleet.getCurrentAssignment() != null && fleet.getCurrentAssignment().getAssignment().equals(FleetAssignment.ORBIT_PASSIVE)) {
                    memory.set(FLEET_SEEN_IN_ORBIT, true);
                }
            }
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

    @Override
    protected boolean isValidImpl() {
        return planetIsValid();
    }

    @Override
    protected void cleanupImpl() {
        cleanupStage1();
    }

    private void cleanupStage1() {
        Misc.makeUnimportant(_planetWithBase, getReasonId());
        Misc.setFlagWithReason(_planetWithBase.getMemoryWithoutUpdate(), IS_BASE_KEY, getReasonId(), false, 0);
        _planetWithBase.getMemoryWithoutUpdate().unset(PLAYER_INTERACTED_KEY);
        IcfbFleetSuspicion.removeFromFleet(_fleet);
    }

    @NotNull
    private String getReasonId() {
        return getData().missionGiver.getId() + ":" + getId();
    }

    private boolean planetIsValid() {
        return _planetWithBase != null && (_planetWithBase.getMarket() == null || !_planetWithBase.getMarket().isInEconomy());
    }

    @Override
    public void addDescriptionBody(TooltipMakerAPI info, int stageIndex) {
        Data data = getData();
        Color hl = Misc.getHighlightColor();
        Color ng = Misc.getNegativeHighlightColor();
        Color fc = data.targetFaction.getBaseUIColor();
        final int marines = Global.getSector().getPlayerFleet().getCargo().getMarines();

        if (stageIndex == 0) {
            info.addPara(
                    "A secret base lies hidden somewhere in the %s system. It would take many cycles to find it via " +
                            "trial and error. Instead, you are instructed to locate the %s fleet roaming the system, " +
                            "and %s.",
                    0,
                    new Color[] { hl, fc, hl },
                    getLocationName(),
                    getTargetFactionName(),
                    "track it without raising too much suspicion"
            );

            info.addPara(
                    "Eventually the fleet should land at its base of origin " +
                    "for resupply, and that's when you strike. %s",
                    10,
                    Misc.getNegativeHighlightColor(),
                    "Attacking the fleet before locating the secret base will fail the mission."
            );

            info.addPara(
                    "You will need %s(%s) marines to carry out the ensuing " +
                            "raid on the base - to claim the experimental phase technology you're after.",
                    10,
                    new Color[] { hl, marines >= MARINE_COUNT ? hl : ng },
                    Misc.getWithDGS(MARINE_COUNT),
                    Misc.getWithDGS(marines)
            );
        }

        if (stageIndex == 1) {
            info.addPara(
                    "You've acquired the experimental phase technology package. Return to %s to complete the mission.",
                    0,
                    data.missionGiver.getFaction().getBaseUIColor(),
                    data.missionGiver.getName().getFullName()
            );
        }
    }
}
