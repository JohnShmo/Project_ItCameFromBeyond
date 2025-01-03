package org.shmo.icfb.graphics;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class ShifterRiftCloudRenderer {
    public static String CORE_SPRITE_ID = "shifter_rift_core";
    public static String FRINGE_SPRITE_ID = "shifter_rift_fringe";

    public static class Request {
        public Vector2f location;
        public float angle;
        public float scale;
    }

    final transient private List<Request> _requests = new ArrayList<>();

    public void addRequest(Request request) {
        _requests.add(request);
    }

    public void renderFringe() {
        if (_requests.isEmpty())
            return;

        final SpriteAPI fringeSprite = Global.getSettings().getSprite("icfb_fx", FRINGE_SPRITE_ID);
        final float baseWidth = fringeSprite.getWidth();
        final float baseHeight = fringeSprite.getHeight();
        fringeSprite.setAdditiveBlend();

        for (Request request : _requests) {
            final float x = request.location.x + (Misc.random.nextFloat() - 0.5f) * 2;
            final float y = request.location.y + (Misc.random.nextFloat() - 0.5f) * 2;
            fringeSprite.setSize(
                    request.scale * (baseWidth + (Misc.random.nextFloat() - 0.5f) * (baseWidth * 0.02f)),
                    request.scale * (baseHeight + (Misc.random.nextFloat() - 0.5f) * (baseHeight * 0.02f))
            );
            fringeSprite.setAngle(request.angle);
            fringeSprite.renderAtCenter(x, y);
        }

        _requests.clear();
    }

    public void renderCore() {
        if (_requests.isEmpty())
            return;

        final SpriteAPI coreSprite = Global.getSettings().getSprite("icfb_fx", CORE_SPRITE_ID);
        final float baseWidth = coreSprite.getWidth();
        final float baseHeight = coreSprite.getHeight();

        for (Request request : _requests) {
            final float x = request.location.x + (Misc.random.nextFloat() - 0.5f) * 0.5f;
            final float y = request.location.y + (Misc.random.nextFloat() - 0.5f) * 0.5f;
            coreSprite.setSize(
                    request.scale * (baseWidth + (Misc.random.nextFloat() - 0.5f) * (baseWidth * 0.01f)),
                    request.scale * (baseHeight + (Misc.random.nextFloat() - 0.5f) * (baseHeight * 0.01f))
            );
            coreSprite.setAngle(request.angle);
            coreSprite.renderAtCenter(x, y);
        }

        _requests.clear();
    }
}
