package org.shmo.icfb.campaign.quests;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.utilities.FleetFactory;

public class DefeatFleetScript extends ImportantTargetScript implements FleetEventListener {

    protected FleetFactory _fleetFactory = null;
    protected SectorEntityToken _spawnLocation = null;
    protected CampaignFleetAPI _fleet = null;
    protected FleetMemberAPI _flagShip = null;
    protected FactionAPI _faction = null;
    protected WinCondition _winCondition = WinCondition.BASIC_VICTORY;

    public enum WinCondition {
        BASIC_VICTORY,
        DEFEAT_FLAGSHIP,
        TOTAL_ANNIHILATION
    }

    private void setFlagShip(FleetMemberAPI flagShip) {
        _flagShip = flagShip;
    }

    protected FleetMemberAPI getFlagShip() {
        return _flagShip;
    }

    protected FactionAPI getFaction() {
        return _faction;
    }

    private void setFaction(FactionAPI faction) {
        _faction = faction;
    }

    public void setSpawnLocation(SectorEntityToken spawnLocation) {
        _spawnLocation = spawnLocation;
    }

    public SectorEntityToken getSpawnLocation() {
        return _spawnLocation;
    }

    public void setFleetFactory(FleetFactory fleetFactory) {
        _fleetFactory = fleetFactory;
    }

    public FleetFactory getFleetFactory() {
        return _fleetFactory;
    }

    private void setFleet(CampaignFleetAPI fleet) {
        _fleet = fleet;
    }

    protected CampaignFleetAPI getFleet() {
        return _fleet;
    }

    public void setWinCondition(WinCondition winCondition) {
        _winCondition = winCondition;
    }

    public WinCondition getWinCondition() {
        return _winCondition;
    }

    @Override
    public void start() {
        if (getFleetFactory() != null && getSpawnLocation() != null) {
            CampaignFleetAPI fleet = getFleetFactory().create(getSpawnLocation());
            fleet.setNoAutoDespawn(true);
            fleet.addEventListener(this);
            setFleet(fleet);
            setTarget(fleet);
            setFlagShip(fleet.getFlagship());
            setFaction(fleet.getFaction());
        }
        super.start();
    }

    @Override
    public void end() {
        super.end();
        cleanUpFleet();
    }

    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
        if (fleet != getFleet())
            return;
        cleanUpFleet();
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {
        if (fleet != getFleet())
            return;

        boolean taskComplete = false;
        switch (getWinCondition()) {
            case BASIC_VICTORY:
                if (primaryWinner != fleet || fleet.getNumShips() == 0)
                    taskComplete = true;
                break;
            case DEFEAT_FLAGSHIP:
                if (fleet.getFlagship() != getFlagShip() || fleet.getNumShips() == 0)
                    taskComplete = true;
                break;
            case TOTAL_ANNIHILATION:
                if (fleet.getNumShips() == 0)
                    taskComplete = true;
                break;
        }

        if (taskComplete)
            cleanUpFleet();
    }

    protected void cleanUpFleet() {
        if (getFleet() == null)
            return;
        Misc.makeUnimportant(getTarget(), getQuestStep().quest.getName());
        CampaignFleetAPI fleet = getFleet();
        fleet.setNoAutoDespawn(false);
        fleet.clearAssignments();
        fleet.getAI().addAssignment(
                FleetAssignment.GO_TO_LOCATION_AND_DESPAWN,
                getSpawnLocation(),
                1000000f,
                null
        );
        setTarget(null);
        setFleet(null);
        setFlagShip(null);
        setFaction(null);
    }

    @Override
    public void advance(float deltaTime) {
        super.advance(deltaTime);
        if (getFleet() != null) {
            final CampaignFleetAPI fleet = getFleet();
            final FactionAPI faction = getFaction();
            final FactionAPI neutral = Global.getSector().getFaction(Factions.NEUTRAL);
            if (fleet.isInCurrentLocation() && fleet.getFaction() != faction) {
                fleet.setFaction(faction.getId(), true);
            } else if (!fleet.isInCurrentLocation() && fleet.getFaction() != neutral) {
                fleet.setFaction(Factions.NEUTRAL, true);
            }
        }
    }
}
