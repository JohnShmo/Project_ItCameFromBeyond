package org.shmo.icfb.campaign.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.impl.campaign.BaseCustomEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.icfb.graphics.ShifterRiftRenderer;
import org.shmo.icfb.utilities.ShmoMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ShifterRiftCloud extends BaseCustomEntityPlugin {
    private static class CloudInstance {
        Vector2f location;
        float angle;
        float inLifeTime;
        float outLifeTime;
        float size;
    }

    public static class Params {
        public float size;
        public float x;
        public float y;
        public float lifeTime;
    }

    public enum State {
        IN,
        OUT
    }

    public static SectorEntityToken create(LocationAPI location, float x, float y, float radius, float duration) {
        Params params = new Params();
        params.x = x;
        params.y = y;
        params.size = radius;
        params.lifeTime = duration;
        SectorEntityToken entity = location.addCustomEntity(
                null,
                null,
                "icfb_shifter_rift_cloud",
                Factions.NEUTRAL,
                params
        );
        entity.setLocation(x, y);
        return entity;
    }

    private Params _params = null;
    private List<CloudInstance> _cloudInstances = null;
    private State _state = State.IN;
    private float _lifeTime = 0;

    private void createCloudInstance(float cx, float cy, float distance) {
        if (_cloudInstances == null)
            return;
        final Vector2f location = new Vector2f(cx, cy);
        final float angle = Misc.random.nextFloat() * 360f;
        final float lifeTime = (distance/128);
        CloudInstance cloudInstance = new CloudInstance();
        cloudInstance.location = location;
        cloudInstance.angle = angle;
        cloudInstance.inLifeTime = -lifeTime;
        cloudInstance.outLifeTime = -lifeTime;
        cloudInstance.size =
                (Misc.random.nextFloat() * 2f + 0.5f) * (0.5f + (1f - distance / Math.max(_params.size, 1)) * 0.5f);
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
        _lifeTime = _params.lifeTime;
        entity.setLightSource(null, Color.BLACK);

        final float size = _params.size;
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
                float cloudX = _params.x + offsetX + (Misc.random.nextFloat() - 0.5f) * 24;
                float cloudY = _params.y + offsetY + (Misc.random.nextFloat() - 0.5f) * 24;

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
        return _params.size + 500f;
    }

    @Override
    public void advance(float amount) {
        if (entity == null || entity.isExpired())
            return;
        if (_cloudInstances == null || _cloudInstances.isEmpty())
            return;
        if (Global.getSector().isPaused())
            return;

        if (_lifeTime <= 0) {
            _state = State.OUT;
        }
        else {
            _lifeTime -= amount;
        }

        if (_state.equals(State.IN)) {
            for (CloudInstance cloudInstance : _cloudInstances) {
                cloudInstance.inLifeTime += amount * 2;
                if (cloudInstance.inLifeTime > 1)
                    cloudInstance.inLifeTime = 1;
            }
        } else {
            float highestLifetime = -1;
            for (CloudInstance cloudInstance : _cloudInstances) {
                if (cloudInstance.outLifeTime > highestLifetime)
                    highestLifetime = cloudInstance.outLifeTime;
                cloudInstance.outLifeTime -= amount * 2;
            }
            if (highestLifetime <= 0) {
                Misc.fadeAndExpire(entity);
                entity = null;
                _cloudInstances = null;
            }
        }
    }

    @Override
    public void render(CampaignEngineLayers layer, ViewportAPI viewport) {
        if (entity == null || entity.isExpired())
            return;
        if (_cloudInstances == null || _cloudInstances.isEmpty())
            return;
        final ShifterRiftRenderer riftRenderer = new ShifterRiftRenderer();
        for (CloudInstance cloudInstance : _cloudInstances) {
            if (cloudInstance.inLifeTime <= 0)
                continue;
            ShifterRiftRenderer.Request request = new ShifterRiftRenderer.Request();
            request.location = cloudInstance.location;
            final float lt = _state.equals(State.IN) ? cloudInstance.inLifeTime : cloudInstance.outLifeTime;
            request.scale = cloudInstance.size * ShmoMath.easeInOutQuad(Math.min(Math.max(lt, 0f), 1f));
            request.angle = cloudInstance.angle;
            riftRenderer.addRequest(request);
        }
        riftRenderer.render();
    }
}
