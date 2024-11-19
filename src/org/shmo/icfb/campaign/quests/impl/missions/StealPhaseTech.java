package org.shmo.icfb.campaign.quests.impl.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.missions.BaseIcfbMission;
import org.shmo.icfb.campaign.quests.missions.IcfbMissions;
import org.shmo.icfb.campaign.quests.scripts.BaseQuestStepScript;
import org.shmo.icfb.campaign.scripts.temp.IcfbFleetSuspicion;

import java.util.Random;
import java.util.Set;

public class StealPhaseTech extends BaseIcfbMission {

    public static final String IS_BASE_KEY = "$icfbIsBase";
    public static final String PLAYER_SEES_BASE_KEY = "$icfbPlayerSeesBase";
    public static final String FLEET_KEY = "$icfPhsFleet";
    public static final String FLEET_PROGRESS_KEY = "$icfPhsFleetProgress";

    CampaignFleetAPI _fleet = null;
    PlanetAPI _planetWithBase = null;
    Vector2f _runDestination = null;

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

        _planetWithBase = IcfbMisc.pickUncolonizedPlanet(data.targetStarSystem);
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
                initBase();
                initFleet();
            }

            @Override
            public void advance(float deltaTime) {
                if (_fleet != null && !_fleet.isExpired())
                    updateFleet(_fleet, deltaTime);
            }

            @Override
            public void end() {

            }
        });
    }

    private void initBase() {
        _planetWithBase.getMemoryWithoutUpdate().set(IS_BASE_KEY, true);
        _planetWithBase.getMemoryWithoutUpdate().set(PLAYER_SEES_BASE_KEY, false);
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
        memory.set(FLEET_PROGRESS_KEY, 0);
    }

    private void updateFleet(CampaignFleetAPI fleet, float deltaTime) {
        if (!IcfbFleetSuspicion.fleetHasSuspicion(fleet))
            IcfbFleetSuspicion.addToFleet(fleet);
        Data data = getData();
        final MemoryAPI memory = fleet.getMemoryWithoutUpdate();
        final float susLevel = IcfbFleetSuspicion.getFleetSuspicion(fleet);
        final boolean playerCanSee = fleet.isVisibleToPlayerFleet();
        if (memory.getBoolean("$icfbRunningAway") && _runDestination != null) {
            memory.set("$icfbPhsWaitingToProgress", 25f);
            fleet.setMoveDestinationOverride(_runDestination.x, _runDestination.y);
            return;
        }
        if (susLevel >= 1) {
            memory.set(FLEET_PROGRESS_KEY, 0);
            memory.set("$icfbRunningAway", true, 0.5f);
            _runDestination = Misc.pickLocationNotNearPlayer(fleet.getContainingLocation(), fleet.getLocation(), 4000f);
            fleet.getAbility(Abilities.EMERGENCY_BURN).activate();
            return;
        }
            if (
                    playerCanSee &&
                            (fleet.getCurrentAssignment() == null
                            || !fleet.getCurrentAssignment().getAssignment().equals(FleetAssignment.GO_TO_LOCATION)
                            ) && memory.getFloat("$icfbPhsWaitingToProgress") <= 0
            ) {
                if (memory.getInt(FLEET_PROGRESS_KEY) > 2) {
                    fleet.clearAssignments();
                    IcfbFleetSuspicion.removeFromFleet(fleet);
                    despawnFleet(fleet, _planetWithBase);
                    _planetWithBase.getMemoryWithoutUpdate().set(PLAYER_SEES_BASE_KEY, true);
                    Misc.makeImportant(_planetWithBase, data.missionGiver.getId() + ":" + getId());
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
                    memory.set("$icfbPhsWaitingToProgress", 25f);
                    memory.set(FLEET_PROGRESS_KEY, memory.getInt(FLEET_PROGRESS_KEY) + 1);
                    return;
                }

                fleet.clearAssignments();
                fleet.addAssignment(
                        FleetAssignment.GO_TO_LOCATION,
                        nextDest, (Misc.getDistance(_fleet, nextDest) / Math.max(_fleet.getFleetData().getTravelSpeed(), 1)) * 10,
                        new Script() {
                            @Override
                            public void run() {
                                memory.set("$icfbPhsWaitingToProgress", 20f);
                            }
                        });
                fleet.addAssignment(FleetAssignment.ORBIT_PASSIVE, nextDest, 100000f);
                memory.set(FLEET_PROGRESS_KEY, memory.getInt(FLEET_PROGRESS_KEY) + 1);

            } else if (playerCanSee) {
                memory.set("$icfbPhsWaitingToProgress", memory.getFloat("$icfbPhsWaitingToProgress") - deltaTime);
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
        Misc.makeUnimportant(_planetWithBase, getData().missionGiver.getId() + ":" + getId());
        IcfbFleetSuspicion.removeFromFleet(_fleet);
    }

    private boolean planetIsValid() {
        return _planetWithBase != null && (_planetWithBase.getMarket() == null || !_planetWithBase.getMarket().isInEconomy());
    }

}
