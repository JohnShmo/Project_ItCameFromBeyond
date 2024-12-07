package org.shmo.icfb.campaign.events;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.icfb.IcfbGlobal;
import org.shmo.icfb.campaign.scripts.IcfbIncursionManager;
import org.shmo.icfb.utilities.ShifterFleetUtils;
import org.shmo.icfb.utilities.ShmoMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Incursion extends BaseIntelPlugin {
    public final static String SYSTEM_KEY = "$system";
    public final static String FLEETS_KEY = "$fleets";
    public final static String ACTIVE_FLEETS_KEY = "$activeFleets";
    public final static String SPAWN_LOCATION_KEY = "$spawnLocation";
    public final static String POINTS_CONTRIBUTED_KEY = "$pointsContributed";
    public final static String DURATION_KEY = "$duration";

    public static final float STANDARD_DURATION_DAYS = 90f;
    public static final int MAX_POINTS_CONTRIBUTED = 50;
    public static final int MAX_FLEETS = 20;

    private static SectorEntityToken createSpawnLocation(StarSystemAPI system) {
        SectorEntityToken center = system.getCenter();
        SectorEntityToken player = Global.getSector().getPlayerFleet();
        if (player == null)
            return null;
        final boolean playerIsInSystem = center.isInCurrentLocation();
        final PlanetAPI star = system.getStar();

        if (star != null)
            center = star;

        if (playerIsInSystem) {
            final float distanceToPlayer = Misc.getDistance(player, center);
            final float range = player.getSensorStrength();
            float min = Math.max(distanceToPlayer - range, IcfbGlobal.getSettings().shiftJump.arrivalDistanceFromDestination);
            final float max = distanceToPlayer + range;
            if (star != null) {
                min = Math.max(min, center.getRadius() + 2f * (star.getRadius()
                        + star.getSpec().getCoronaSize())
                        + IcfbGlobal.getSettings().shiftJump.arrivalDistanceFromDestination);
            }
            final float distance = ShmoMath.lerp(min, max, Misc.random.nextFloat());

            Vector2f pointing = new Vector2f();
            pointing = Vector2f.sub(player.getLocation(), center.getLocation(), pointing);
            pointing.normalise();
            final float angle = Misc.getAngleInDegrees(pointing) + (Misc.random.nextFloat() - 0.5f) * 15;

            Vector2f location = Misc.getUnitVectorAtDegreeAngle(angle);
            location.scale(distance);

            return system.createToken(location);
        }

        float min = IcfbGlobal.getSettings().shiftJump.arrivalDistanceFromDestination;
        final float max = IcfbGlobal.getSettings().shiftJump.arrivalDistanceFromDestination * 2;
        if (star != null) {
            min = Math.max(min, center.getRadius() + 2f * (star.getRadius()
                    + star.getSpec().getCoronaSize())
                    + IcfbGlobal.getSettings().shiftJump.arrivalDistanceFromDestination);
        }
        final float distance = ShmoMath.lerp(min, max, Misc.random.nextFloat());
        Vector2f location = Misc.getPointAtRadius(center.getLocation(), distance, Misc.random);

        return system.createToken(location);
    }

    private final MemoryAPI _memory;

    public Incursion(StarSystemAPI system) {
        _memory = Global.getFactory().createMemory();
        _memory.set(SYSTEM_KEY, system);
    }

    private void setDurationDays(float days) {
        getMemoryWithoutUpdate().set(DURATION_KEY, days);
    }

    public float getDurationDays() {
        return getMemoryWithoutUpdate().getFloat(DURATION_KEY);
    }

    public MemoryAPI getMemoryWithoutUpdate() {
        return _memory;
    }

    public StarSystemAPI getSystem() {
        return (StarSystemAPI)getMemoryWithoutUpdate().get(SYSTEM_KEY);
    }

    public SectorEntityToken getSpawnLocation() {
        return getMemoryWithoutUpdate().getEntity(SPAWN_LOCATION_KEY);
    }

    private void setSpawnLocation(SectorEntityToken location) {
        if (location == null)
            getMemoryWithoutUpdate().unset(SPAWN_LOCATION_KEY);
        getMemoryWithoutUpdate().set(SPAWN_LOCATION_KEY, location);
    }

    private void unsetSpawnLocation() {
        setSpawnLocation(null);
    }

    private Vector2f getPointToSpawnAt() {
        SectorEntityToken spawnLocation = getSpawnLocation();
        if (spawnLocation == null)
            return null;
        return Misc.getPointWithinRadiusUniform(
                spawnLocation.getLocation(),
                IcfbGlobal.getSettings().shiftJump.arrivalDistanceFromDestination,
                Misc.random
        );
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
        Vector2f point = getPointToSpawnAt();
        if (point == null)
            point = new Vector2f();
        ShifterFleetUtils.spawnFleet(fleet, getSystem(), point.x, point.y);
        getActiveFleets().add(fleet);
    }

    private void removeFleet(CampaignFleetAPI fleet) {
        if (fleet == null)
            return;
        getFleets().remove(fleet);
        getActiveFleets().remove(fleet);
    }

    private void cleanup() {
        for (CampaignFleetAPI fleet : getActiveFleets()) {
            ShifterFleetUtils.despawnFleet(fleet, getSpawnLocation());
        }
        getActiveFleets().clear();
        getFleets().clear();
        cleanupSpawnLocation();
    }

    private void cleanupSpawnLocation() {
        SectorEntityToken spawnLocation = getSpawnLocation();
        if (spawnLocation != null) {
            Misc.fadeAndExpire(spawnLocation);
            unsetSpawnLocation();
        }
    }

    public void start() {
        setSpawnLocation(createSpawnLocation(getSystem()));
        setPlayerVisibleTimestamp(Global.getSector().getClock().getTimestamp());
        setDurationDays(STANDARD_DURATION_DAYS + ShmoMath.lerp(-15, 45, Misc.random.nextFloat()));
        setPointsContributed(MAX_POINTS_CONTRIBUTED);
        setImportant(true);
        Global.getSector().getIntelManager().addIntel(this);

        for (int i = 0; i < MAX_FLEETS; i++) {
            int fleetPoints = ShmoMath.lerp(15, 160, Misc.random.nextFloat());
            addFleet(ShifterFleetUtils.createFleet(fleetPoints, IcfbIncursionManager.getInstance().isNerfed()));
        }

        List<CampaignFleetAPI> fleets = getFleetsCopy();
        addToActiveFleets(fleets.get(0));
        addToActiveFleets(fleets.get(1));
        addToActiveFleets(fleets.get(2));
    }

    public void end() {
        cleanup();
    }

    public int getPointsContributed() {
        if (isEnded() || isEnding())
            return 0;
        return getMemoryWithoutUpdate().getInt(POINTS_CONTRIBUTED_KEY);
    }

    public void setPointsContributed(int points) {
        getMemoryWithoutUpdate().set(POINTS_CONTRIBUTED_KEY, points);
    }

    public void advance(float deltaTime) {
        super.advance(deltaTime);
        if (isEnded() || isEnding())
            return;
        startEndingIfNeeded();
        updateFleets(deltaTime);
    }

    private void updateFleets(float deltaTime) {
        final Set<CampaignFleetAPI> activeFleets = getActiveFleets();

        final List<CampaignFleetAPI> toRemove = new ArrayList<>();
        for (CampaignFleetAPI fleet : activeFleets) {
            if (!fleet.isAlive() || fleet.isExpired()) {
                toRemove.add(fleet);
                continue;
            }
        }
        for (CampaignFleetAPI fleet : toRemove) {
            removeFleet(fleet);
        }

        final List<CampaignFleetAPI> fleetsCopy = getFleetsCopy();
        if (activeFleets.size() < 5) {
            float chanceToSpawn = 0.50f * deltaTime;
            if (Misc.random.nextFloat() <= chanceToSpawn) {
                int index = Misc.random.nextInt(fleetsCopy.size());
                CampaignFleetAPI fleet = getFleetsCopy().get(index);

                if (!activeFleets.contains(fleet)) {
                    addToActiveFleets(fleetsCopy.get(index));
                }
            }
        }

        final float fractionOfFleetsRemaining = (float)getFleets().size() / (float)MAX_FLEETS;
        final int pointsFromFleets = (int)((MAX_POINTS_CONTRIBUTED - 10) * fractionOfFleetsRemaining);
        final int totalPoints = pointsFromFleets + 10;
        setPointsContributed(totalPoints);
    }

    private void startEndingIfNeeded() {
        final float daysPassed = Global.getSector().getClock().getElapsedDaysSince(getPlayerVisibleTimestamp());
        boolean shouldEnd;
        if (daysPassed < 10)
            shouldEnd = false;
        else if (getFleets().isEmpty())
            shouldEnd = true;
        else shouldEnd = daysPassed > STANDARD_DURATION_DAYS;
        if (!isEnding() && shouldEnd) {
            end();
            endAfterDelay();
            sendUpdateIfPlayerHasIntel(null, null);
        }
    }

    @Override
    public float getTimeRemainingFraction() {
        final float daysPassed = Global.getSector().getClock().getElapsedDaysSince(getPlayerVisibleTimestamp());
        final float durationDays = getDurationDays();
        final float timeRemaining = Math.max(durationDays - daysPassed, 0f);
        return timeRemaining / durationDays;
    }

    @Override
    protected String getName() {
        if (!isEnding())
            if (getSystem() != null)
                return "Incursion - " + getSystem().getBaseName();
            else
                return "Incursion";
        else
            return "Incursion - Over";
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("icfb_events", "incursion");
    }

    @Override
    public String getCommMessageSound() {
        if (!isEnding())
            return getSoundColonyThreat();
        else
            return getSoundStandardPosting();
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        StarSystemAPI system = getSystem();
        if (system == null)
            return null;
        return system.getCenter();
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        if (tags == null)
            tags = new HashSet<>();
        tags.add("Incursions");
        return tags;
    }

    @Override
    protected void addBulletPoints(TooltipMakerAPI info, ListInfoMode mode, boolean isUpdate, Color tc, float initPad) {
    }
}
