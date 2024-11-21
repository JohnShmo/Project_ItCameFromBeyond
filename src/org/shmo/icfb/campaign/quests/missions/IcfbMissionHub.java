package org.shmo.icfb.campaign.quests.missions;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.CallEvent;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireAll;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.impl.campaign.rulecmd.UpdateMemory;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;
import org.shmo.icfb.campaign.quests.impl.missions.StealPhaseTech;
import org.shmo.icfb.campaign.quests.impl.missions.SubspaceFissure;
import org.shmo.icfb.campaign.scripts.IcfbQuestManager;

import java.util.*;

public class IcfbMissionHub implements CallEvent.CallableEvent, EveryFrameScript {
    public static final String MEM_KEY = "$icfbHub";
    public static final float DAYS_BETWEEN_UPDATES = 5;
    public static final float CHANCE_TO_MAKE_AVAILABLE_PER_UPDATE = 0.15f;

    private final PersonAPI _person;
    private final Set<String> _missionSet;
    private final Random _random;
    private long _timestampOfLastUpdate;

    private IcfbMissionHub(PersonAPI person, Set<String> missionSet) {
        _person = person;
        _missionSet = new HashSet<>(missionSet);
        _random = Misc.random;
        forceAvailableUpdate(3);
    }

    public static void createHub(PersonAPI person, String... availableMissions) {
        removeHub(person);
        final Set<String> missionSet = new HashSet<>();
        Collections.addAll(missionSet, availableMissions);
        final IcfbMissionHub hub = new IcfbMissionHub(person, missionSet);
        person.getMemoryWithoutUpdate().set(MEM_KEY, hub);
        person.getMemoryWithoutUpdate().set(MEM_KEY + "_count", 0);
        Global.getSector().addScript(hub);
    }

    public static void removeHub(PersonAPI person) {
        IcfbMissionHub hub = (IcfbMissionHub)person.getMemoryWithoutUpdate().get(MEM_KEY);
        if (hub != null) {
            for (String missionId : hub._missionSet) {
                hub.removeMission(missionId);
            }
            Global.getSector().removeScript(hub);
        }
        person.getMemoryWithoutUpdate().unset(MEM_KEY);
        person.getMemoryWithoutUpdate().unset(MEM_KEY + "_count");
    }

    public static void addToAvailableMissions(PersonAPI person, String... availableMissions) {
        IcfbMissionHub hub = (IcfbMissionHub)person.getMemoryWithoutUpdate().get(MEM_KEY);
        if (hub == null)
            return;
        Collections.addAll(hub._missionSet, availableMissions);
    }

    private static IcfbMission initMissionFromId(String id, PersonAPI person) {
        switch (id) {
            case IcfbMissions.SUBSPACE_FISSURE: return new SubspaceFissure(person);
            case IcfbMissions.STEAL_PHASE_TECH: return new StealPhaseTech(person);
            // TODO: Other missions
            default: return null;
        }
    }

    public void addMission(String id) {
        if (IcfbQuestManager.getInstance().contains(id))
            return;
        if (_person.getMemory().contains(id))
            return;
        IcfbMission mission = initMissionFromId(id, _person);
        if (mission == null || !mission.isValid())
            return;
        MemoryAPI memory = _person.getMemoryWithoutUpdate();

        memory.set("$" + id, mission);

        memory.set("$" + id + "__available__zz", false);
        memory.set("$" + id + "_available", false);

        memory.set(
                "$" + id + "_reward",
                Misc.getDGSCredits(mission.getCreditReward())
        );

        if (mission.getLocationName() != null) {
            memory.set(
                    "$" + id + "_location",
                    mission.getLocationName()
            );
        }

        if (mission.getMapLocation() != null) {
            memory.set(
                    "$" + id + "_distance",
                    Misc.getRoundedValueMaxOneAfterDecimal(
                            Misc.getDistanceLY(_person.getMarket().getPrimaryEntity(),
                                    mission.getMapLocation()
                            )
                    )
            );
        }

        if (mission.getTargetFactionName() != null) {
            memory.set(
                    "$" + id + "_faction",
                    mission.getTargetFactionName()
            );
        }

        if (mission.getTargetMarketName() != null) {
            memory.set(
                    "$" + id + "_market",
                    mission.getTargetMarketName()
            );
        }

        if (mission.getTimeLimitDays() != 0) {
            memory.set(
                    "$" + id + "_timeLimit",
                    Misc.getWithDGS(mission.getTimeLimitDays())
            );
        }
    }

    public void makeMissionAvailable(String id) {
        if (!canAcceptMission(id))
            return;
        MemoryAPI memory = _person.getMemoryWithoutUpdate();
        memory.set("$" + id + "__available__zz", true);
    }

    public boolean isMissionAvailable(String id) {
        MemoryAPI memory = _person.getMemoryWithoutUpdate();
        IcfbMission mission = (IcfbMission)memory.get("$" + id);

        if (mission == null)
            return false;
        if (!mission.isValid()) {
            removeMission(id);
            return false;
        }
        if (!canAcceptMission(id))
            return false;

        return (memory.getBoolean("$" + id + "__available__zz")
                && mission.getMinimumRepLevel() <= _person.getRelToPlayer().getRel())
                || Global.getSettings().isDevMode();
    }

    private void refreshAvailability(String id) {
        if (isMissionAvailable(id)) {
            _person.getMemoryWithoutUpdate().set("$" + id + "_available", true);
        } else if (canAcceptMission(id)) {
            _person.getMemoryWithoutUpdate().set("$" + id + "_available", false);
        }
    }

    private void removeMission(String id) {
        MemoryAPI memory = _person.getMemoryWithoutUpdate();
        memory.unset("$" + id);
        memory.unset("$" + id + "__available__zz");
        memory.unset("$" + id + "_available");
        memory.unset("$" + id + "_reward");
        memory.unset("$" + id + "_location");
        memory.unset("$" + id + "_distance");
        memory.unset("$" + id + "_faction");
        memory.unset("$" + id + "_market");
        memory.unset("$" + id + "_timeLimit");
    }

    public boolean canAcceptMission(String id) {
        return _person.getMemoryWithoutUpdate().contains("$" + id)
                && !IcfbQuestManager.getInstance().contains(_person.getId() + ":" + id);
    }

    public void acceptMission(String id) {
        if (!canAcceptMission(id))
            return;
        IcfbQuestManager.getInstance().add((QuestFactory)_person.getMemoryWithoutUpdate().get("$" + id));
        removeMission(id);
    }

    private IcfbMission getMission(String id) {
        MemoryAPI memory = _person.getMemoryWithoutUpdate();
        return (IcfbMission)memory.get("$" + id);
    }

    @Override
    public boolean callEvent(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (params.isEmpty())
            return false;

        String id = params.get(0).getString(memoryMap);

        if (id.equals("refresh")) {
            refresh();
            return true;
        }

        if (id.equals("listMissions")) {
            listMissions(dialog, memoryMap);
            return true;
        }

        String action = params.get(1).getString(memoryMap);
        if (!canAcceptMission(id))
            return false;

        if (action.equals("accept")) {
            acceptMission(id);
            return true;
        }

        if (action.equals("complete")) {
            IcfbMission mission = (IcfbMission)_person.getMemory().get("$" + id + "_ref");
            mission.completeViaDialog(dialog);
            return true;
        }

        final List<Misc.Token> newParams = new ArrayList<>();
        for (int i = 2; i < params.size(); i++) {
            newParams.add(params.get(i));
        }

        IcfbMission mission = (IcfbMission)_person.getMemory().get("$" + id);
        return mission.callEvent(ruleId, action, dialog, newParams, memoryMap);
    }

    private void listMissions(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        refresh();
        MemoryAPI memory = _person.getMemory();
        int count = memory.getInt(MEM_KEY + "_count");

        if (count == 0) {
            FireBest.fire(null, dialog, memoryMap, "IcfbMHPreMissionListText");
            FireAll.fire(null, dialog, memoryMap, "PopulateOptions");
            return;
        }

        if (!memory.getBoolean("$icfbMHAlreadyListedBlurbs")) {
            FireBest.fire(null, dialog, memoryMap, "IcfbMHOpenText");
            FireBest.fire(null, dialog, memoryMap, "IcfbMHPreMissionListText");
            for (String missionId : _missionSet) {
                if (isMissionAvailable(missionId))
                    FireBest.fire(null, dialog, memoryMap, "IcfbMHAddBlurb_" + missionId);
            }
            if (count > 1) {
                FireBest.fire(null, dialog, memoryMap, "IcfbMHPostMissionListText");
            }
            memory.set("$icfbMHAlreadyListedBlurbs", true, 0);
        }

        FireAll.fire(null, dialog, memoryMap, "IcfbMHAddOptions");
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        if (shouldUpdate())
            update();
    }

    private boolean shouldUpdate() {
        return Global.getSector().getClock().getElapsedDaysSince(_timestampOfLastUpdate) >= DAYS_BETWEEN_UPDATES;
    }

    public void refresh() {
        int totalAvailable = 0;
        for (String id : _missionSet) {
            refreshAvailability(id);
            if (isMissionAvailable(id))
                totalAvailable++;
        }
        _person.getMemoryWithoutUpdate().set(MEM_KEY + "_count", totalAvailable);
    }

    public void update() {
        updateTimestamp();

        for (String id : _missionSet) {
            addMission(id);
            if (_random.nextFloat() <= CHANCE_TO_MAKE_AVAILABLE_PER_UPDATE)
                makeMissionAvailable(id);
        }

        refresh();
    }

    public void forceAvailableUpdate(int count) {
        updateTimestamp();

        if (count <= 0)
            count = 1;
        if (count > _missionSet.size())
            count = _missionSet.size();
        if (count != 0) {
            Set<Integer> indices = new HashSet<>();
            while (indices.size() < count) {
                indices.add(_random.nextInt(count));
            }
            int i = 0;
            for (String id : _missionSet) {
                addMission(id);
                if (indices.contains(i))
                    makeMissionAvailable(id);
                i++;
            }
        }

        refresh();
    }

    private void updateTimestamp() {
        _timestampOfLastUpdate = Global.getSector().getClock().getTimestamp();
    }
}
