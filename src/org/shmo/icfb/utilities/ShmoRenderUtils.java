package org.shmo.icfb.utilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class ShmoRenderUtils {
    public static void drawShipTrailEffect(ShipAPI ship, Color color, boolean additive) {
        SpriteAPI sprite = Global.getSettings().getSprite(ship.getHullSpec().getSpriteName());
        MagicRender.battlespace(
                sprite,
                ship.getLocation(),
                new Vector2f(0, 0),
                new Vector2f(sprite.getWidth(), sprite.getHeight()),
                new Vector2f(sprite.getWidth()*2, sprite.getHeight()*2),
                ship.getFacing() - 90,
                0,
                color,
                additive,
                10f,
                10f,
                0.75f,
                0.75f,
                0f,
                0.0f,
                0.1f,
                0.1f,
                ship.getLayer()
        );
    }

    public static void drawShipSprite(ShipAPI ship, Vector2f location, float facing, Color blend, boolean additive) {
        SpriteAPI sprite = Global.getSettings().getSprite(ship.getHullSpec().getSpriteName());
        MagicRender.singleframe(
                sprite,
                location,
                new Vector2f(sprite.getWidth(), sprite.getHeight()),
                facing - 90,
                blend,
                additive
        );
    }

    public static void drawShipJitter(ShipAPI ship, Vector2f location, float facing, Color blend, boolean additive, float range, float tilt) {
        SpriteAPI sprite = Global.getSettings().getSprite(ship.getHullSpec().getSpriteName());
        MagicRender.battlespace(
                sprite,
                location,
                new Vector2f(),
                new Vector2f(sprite.getWidth(), sprite.getHeight()),
                new Vector2f(),
                facing - 90,
                0,
                blend,
                additive,
                range,
                tilt,
                0.75f,
                0.75f,
                0f,
                0.0f,
                0.1f,
                0.1f,
                ship.getLayer()
        );
    }

}
