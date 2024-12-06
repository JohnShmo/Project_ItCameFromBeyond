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
import org.shmo.icfb.campaign.entities.plugins.ShifterRiftCloud;
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
        final float max = 10000f;
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
    private boolean _initialized;

    public Incursion(StarSystemAPI system) {
        _memory = Global.getFactory().createMemory();
        _memory.set(SYSTEM_KEY, system);
        _initialized = false;
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
        getActiveFleets().add(fleet);
    }

    private void removeFleet(CampaignFleetAPI fleet) {
        if (fleet == null)
            return;
        getFleets().remove(fleet);
        getActiveFleets().remove(fleet);
    }

    public void start() {
        setSpawnLocation(createSpawnLocation(getSystem()));

        for (int i = 0; i < 16; i++) {
            Vector2f point = getPointToSpawnAt();
            if (point != null)
                ShifterRiftCloud.create(getSystem(), point.x, point.y, 200, 120);
        }
        timestamp = Global.getSector().getClock().getTimestamp();
        setPointsContributed(25);
        setImportant(true);
        setHidden(false);
        Global.getSector().getIntelManager().addIntel(this);
        markAsInitialized();
    }

    private void markAsInitialized() {
        _initialized = true;
    }

    private boolean isInitialized() {
        return _initialized;
    }

    public void end() {
        SectorEntityToken spawnLocation = getSpawnLocation();
        if (spawnLocation != null) {
            Misc.fadeAndExpire(spawnLocation);
            unsetSpawnLocation();
        }
    }

    public int getPointsContributed() {
        return getMemoryWithoutUpdate().getInt(POINTS_CONTRIBUTED_KEY);
    }

    public void setPointsContributed(int points) {
        getMemoryWithoutUpdate().set(POINTS_CONTRIBUTED_KEY, points);
    }

    public void advance(float deltaTime) {
        super.advance(deltaTime);
        if (isDone() || Global.getSector().isPaused())
            return;
    }

    @Override
    public boolean isDone() {
        return isEnded();
    }

    @Override
    public boolean isEnded() {
        if (Global.getSector().getClock().getElapsedDaysSince(timestamp) < 1)
            return false;
        if (!isInitialized())
            return true;
        if (getFleets().isEmpty())
            return true;
        return false;
    }

    @Override
    protected String getName() {
        if (getSystem() == null)
            return "Incursion";
        return "Incursion: " + getSystem().getName();
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("icfb_events", "incursion");
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
    public boolean autoAddCampaignMessage() {
        return true;
    }

    @Override
    protected void addBulletPoints(TooltipMakerAPI info, ListInfoMode mode, boolean isUpdate, Color tc, float initPad) {
        if (getSystem() == null)
            return;
        info.addPara("Location: %s", initPad, Misc.getHighlightColor(), getSystem().getName());
    }
}
