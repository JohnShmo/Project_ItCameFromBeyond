package org.shmo.icfb.campaign.entities.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.impl.campaign.BaseCustomEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.icfb.graphics.ShifterRiftCloudRenderer;
import org.shmo.icfb.utilities.ShmoMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ShifterRiftCloud extends BaseCustomEntityPlugin {
    private static class CloudInstance {
        public float x;
        public float y;
        public float angle;
        public float inLifeTime;
        public float outLifeTime;
        public float size;
    }

    public static class Params {
        public float x;
        public float y;
        public float radius;
        public float duration;
    }

    public enum State {
        IN,
        TRANSITION,
        OUT,
        END,
    }

    public static ShifterRiftCloud create(LocationAPI location, float x, float y, float radius, float duration) {
        Params params = new Params();
        params.x = x;
        params.y = y;
        params.radius = radius;
        params.duration = duration;
        SectorEntityToken entity;
        if (location != null) {
            entity = location.addCustomEntity(
                    null,
                    null,
                    "icfb_shifter_rift_cloud",
                    Factions.NEUTRAL,
                    params
            );
            entity.setLocation(x, y);
            entity.setFacing(Misc.random.nextFloat() * 360);
        } else {
            entity = Global.getSector().getHyperspace().addCustomEntity(
                    null,
                    null,
                    "icfb_shifter_rift_cloud",
                    Factions.NEUTRAL,
                    params
            );
            entity.setLocation(x, y);
            entity.setFacing(Misc.random.nextFloat() * 360);
            if (entity.getContainingLocation() != null)
                entity.getContainingLocation().removeEntity(entity);
        }
        return (ShifterRiftCloud)entity.getCustomPlugin();
    }

    public static ShifterRiftCloud create(LocationAPI location, float x, float y, float radius) {
        return create(location, x, y, radius, -1);
    }

    private Params _params = null;
    private List<CloudInstance> _cloudInstances = null;
    private State _state = State.IN;
    private float _lifeTime = 0;

    public State getState() {
        return _state;
    }

    public SectorEntityToken getEntity() {
        return entity;
    }

    public void expire() {
        if (getState().equals(ShifterRiftCloud.State.IN))
            _state = ShifterRiftCloud.State.TRANSITION;
    }

    public void setLocation(float x, float y) {
        if (_params == null)
            return;
        if (entity == null)
            return;
        entity.setLocation(x, y);
        _params.x = x;
        _params.y = y;
    }

    public Vector2f getLocation() {
        if (_params == null || entity == null || entity.isExpired())
            return new Vector2f();
        return new Vector2f(_params.x, _params.y);
    }

    public LocationAPI getContainingLocation() {
        if (entity == null || entity.isExpired())
            return null;
        return entity.getContainingLocation();
    }

    public void setContainingLocation(LocationAPI location) {
        if (entity == null || entity.isExpired())
            return;
        if (entity.getContainingLocation() != null) {
            entity.getContainingLocation().removeEntity(entity);
        }
        location.addEntity(entity);
    }

    private void createCloudInstance(float x, float y, float distance) {
        if (_cloudInstances == null)
            return;
        CloudInstance cloudInstance = new CloudInstance();
        cloudInstance.x = x;
        cloudInstance.y = y;
        cloudInstance.angle = Misc.random.nextFloat() * 360f;
        cloudInstance.inLifeTime = -(distance/128);
        cloudInstance.outLifeTime = cloudInstance.inLifeTime;
        cloudInstance.size =
                (Misc.random.nextFloat() * 2f + 0.5f) * (0.5f + (1f - distance / Math.max(_params.radius, 1)) * 0.5f);
        _cloudInstances.add(cloudInstance);
    }

    @Override
    public void init(SectorEntityToken entity, Object pluginParams) {
        super.init(entity, pluginParams);
        Params params = (Params)pluginParams;
        if (params == null)
            return;
        _params = params;
        _cloudInstances = new ArrayList<>();
        _state = State.IN;
        _lifeTime = _params.duration;
        entity.setLightSource(null, Color.BLACK);

        final float size = _params.radius;
        for (float d = 0; d <= size; d += 48) {
            final float distance = ((Misc.random.nextFloat() - 0.5f) * 8) + d;

            // Calculate the number of clouds for this ring based on the distance
            int cloudsInRing = Math.min(Math.max(4, (int)(d / 16)), 12);
            final float angleIncrement = 360f / cloudsInRing;

            // Generate points around a circle at the current distance
            for (float a = 0; a < 360; a += angleIncrement) {
                float angle = Misc.random.nextFloat() * 360f;

                // Convert polar coordinates (distance, angle) to Cartesian (x, y)
                float offsetX = distance * (float)Math.cos(Math.toRadians(angle));
                float offsetY = distance * (float)Math.sin(Math.toRadians(angle));

                // Add some randomness to the cloud placement
                float cloudX = offsetX + (Misc.random.nextFloat() - 0.5f) * 24;
                float cloudY = offsetY + (Misc.random.nextFloat() - 0.5f) * 24;

                createCloudInstance(cloudX, cloudY, distance);
            }
        }

        float lowest = 0;
        for (CloudInstance cloudInstance : _cloudInstances) {
            if (lowest > cloudInstance.outLifeTime)
                lowest = cloudInstance.outLifeTime;
        }
        for (CloudInstance cloudInstance : _cloudInstances) {
            cloudInstance.outLifeTime -= lowest - 1;
        }
    }

    @Override
    public float getRenderRange() {
        if (_params == null)
            return 0;
        return _params.radius + 500f;
    }

    @Override
    public void advance(float amount) {
        if (entity == null || entity.isExpired())
            return;
        if (_params == null)
            return;
        if (_cloudInstances == null || _cloudInstances.isEmpty())
            return;
        if (Global.getSector().isPaused())
            return;
        _params.x = entity.getLocation().x;
        _params.y = entity.getLocation().y;

        if (_params.duration > 0) {
            if (_lifeTime <= 0 && _state.equals(State.IN)) {
                _state = State.TRANSITION;
            } else {
                _lifeTime -= amount;
            }
        }

        if (_state.equals(State.IN)) {
            float highestLifetime = -1;
            for (CloudInstance cloudInstance : _cloudInstances) {
                if (cloudInstance.inLifeTime > highestLifetime)
                    highestLifetime = cloudInstance.inLifeTime;
                cloudInstance.inLifeTime += amount * 2;
            }
            float t = ShmoMath.easeOutCirc(Math.max(Math.min(1f, highestLifetime / 2f), 0f));
            Global.getSoundPlayer().playLoop(
                    "icfb_shifter_rift_loop",
                    this,
                    0.5f + t * 0.5f,
                    t,
                    entity.getLocation(), entity.getVelocity(),
                    0.1f,
                    0.1f
            );
        } else if (_state.equals(State.OUT)) {
            float highestLifetime = -1;
            for (CloudInstance cloudInstance : _cloudInstances) {
                if (cloudInstance.outLifeTime > highestLifetime)
                    highestLifetime = cloudInstance.outLifeTime;
                cloudInstance.outLifeTime -= amount * 2;
            }
            float t = ShmoMath.easeOutCirc(Math.max(Math.min(1f, highestLifetime / 2f), 0f));
            Global.getSoundPlayer().playLoop(
                    "icfb_shifter_rift_loop",
                    this,
                    0.5f + t * 0.5f,
                    t,
                    entity.getLocation(), entity.getVelocity(),
                    0.1f,
                    0.1f
            );
            if (highestLifetime <= 0) {
                Misc.fadeAndExpire(entity);
                entity = null;
                _cloudInstances = null;
                _state = State.END;
            }
        } else if (_state.equals(State.TRANSITION)) {
            for (CloudInstance cloudInstance : _cloudInstances) {
                cloudInstance.outLifeTime = Math.min(cloudInstance.inLifeTime, cloudInstance.outLifeTime);
            }
            _state = State.OUT;
            Global.getSoundPlayer().playLoop(
                    "icfb_shifter_rift_loop",
                    this,
                    1.0f,
                    1.0f,
                    entity.getLocation(),
                    entity.getVelocity(),
                    0.1f,
                    0.1f
            );
        }
    }

    @Override
    public void render(CampaignEngineLayers layer, ViewportAPI viewport) {
        if (entity == null || entity.isExpired())
            return;
        if (_params == null)
            return;
        if (_cloudInstances == null || _cloudInstances.isEmpty())
            return;

        final ShifterRiftCloudRenderer riftRenderer = new ShifterRiftCloudRenderer();

        for (CloudInstance cloudInstance : _cloudInstances) {
            if (cloudInstance.inLifeTime <= 0)
                continue;
            ShifterRiftCloudRenderer.Request request = new ShifterRiftCloudRenderer.Request();

            final float lifeTime = _state.equals(State.IN) ? cloudInstance.inLifeTime : cloudInstance.outLifeTime;
            final float t = ShmoMath.easeInOutQuad(Math.min(Math.max(lifeTime, 0f), 1f));
            final Vector2f center = new Vector2f(_params.x, _params.y);
            Vector2f location = new Vector2f(_params.x + cloudInstance.x, _params.y + cloudInstance.y);
            if (entity.getFacing() != 0f)
                location = Misc.rotateAroundOrigin(location, entity.getFacing(), center);

            request.location = ShmoMath.lerp(center, location, 0.7f + (t * 0.3f));
            request.scale = cloudInstance.size * t;
            request.angle = cloudInstance.angle;
            riftRenderer.addRequest(request);
        }

        if (layer.equals(CampaignEngineLayers.TERRAIN_6B)) {
            riftRenderer.renderFringe();
        } else if (layer.equals(CampaignEngineLayers.BELOW_STATIONS)) {
            riftRenderer.renderCore();
        }
    }
}
