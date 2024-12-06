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
        setPlayerVisibleTimestamp(Global.getSector().getClock().getTimestamp());
        setDurationDays(STANDARD_DURATION_DAYS + ShmoMath.lerp(-15, 45, Misc.random.nextFloat()));
        setPointsContributed(35);
        setImportant(true);
        Global.getSector().getIntelManager().addIntel(this);
    }

    public void end() {
        SectorEntityToken spawnLocation = getSpawnLocation();
        if (spawnLocation != null) {
            Misc.fadeAndExpire(spawnLocation);
            unsetSpawnLocation();
        }
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
            return "Incursion";
        else
            return "Incursion Ended";
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
        if (getSystem() == null)
            return;
        info.addPara("Location: %s", initPad, Misc.getHighlightColor(), getSystem().getName());
    }
}
