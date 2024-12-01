package org.shmo.icfb.campaign.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Incursion {
    public final static String SYSTEM_KEY = "$system";
    public final static String FLEETS_KEY = "$fleets";
    public final static String ACTIVE_FLEETS_KEY = "$activeFleets";

    private final MemoryAPI _memory;
    private boolean _initialized;

    public Incursion(StarSystemAPI system) {
        _memory = Global.getFactory().createMemory();
        _initialized = false;

        _memory.set(SYSTEM_KEY, system);
    }

    public MemoryAPI getMemoryWithoutUpdate() {
        return _memory;
    }

    public StarSystemAPI getSystem() {
        return (StarSystemAPI)getMemoryWithoutUpdate().get(SYSTEM_KEY);
    }

    @SuppressWarnings("unchecked")
    private Set<CampaignFleetAPI> getFleets() {
        final MemoryAPI memory = getMemoryWithoutUpdate();
        if (!memory.contains(FLEETS_KEY))
            memory.set(FLEETS_KEY, new HashSet<CampaignFleetAPI>());
        return (HashSet<CampaignFleetAPI>)memory.get(FLEETS_KEY);
    }

    public List<CampaignFleetAPI> getFleetsCopy() {
        return new ArrayList<>(getFleets());
    }

    @SuppressWarnings("unchecked")
    private Set<CampaignFleetAPI> getActiveFleets() {
        final MemoryAPI memory = getMemoryWithoutUpdate();
        if (!memory.contains(ACTIVE_FLEETS_KEY))
            memory.set(ACTIVE_FLEETS_KEY, new HashSet<CampaignFleetAPI>());
        return (HashSet<CampaignFleetAPI>)memory.get(ACTIVE_FLEETS_KEY);
    }

    public List<CampaignFleetAPI> getActiveFleetsCopy() {
        return new ArrayList<>(getActiveFleets());
    }

    private void addFleet(CampaignFleetAPI fleet) {
        if (fleet == null)
            return;
        getFleets().add(fleet);
    }

    private void addToActiveFleets(CampaignFleetAPI fleet) {
        if (fleet == null || !getFleets().contains(fleet))
            return;
        getActiveFleets().add(fleet);
    }

    private void removeFleet(CampaignFleetAPI fleet) {
        if (fleet == null)
            return;
        getFleets().remove(fleet);
        getActiveFleets().remove(fleet);
    }

    public void start() {


        markAsInitialized();
    }

    private void markAsInitialized() {
        _initialized = true;
    }

    private boolean isInitialized() {
        return _initialized;
    }

    public void end() {

    }

    public void advance(float deltaTime) {

    }

    public boolean isDone() {
        if (!isInitialized())
            return true;
        if (getFleets().isEmpty())
            return true;
        return false;
    }
}
