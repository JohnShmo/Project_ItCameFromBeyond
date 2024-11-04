package org.shmo.icfb.combat.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.shmo.icfb.utilities.ShmoMath;

import java.awt.*;

public class ShipLights implements EveryFrameWeaponEffectPlugin {
    private static final float FADE_SPEED = 0.33333f;

    private Color _on = null;
    private Color _off = null;
    private float _currentFadeT = 0f;
    private float _frameTimer = 0f;
    private int _currentFrame = 0;

    private void fadeOut(float amount) {
        _currentFadeT = _currentFadeT - amount * FADE_SPEED;
        if (_currentFadeT < 0)
            _currentFadeT = 0;
    }

    private void fadeIn(float amount) {
        _currentFadeT = _currentFadeT + amount * FADE_SPEED;
        if (_currentFadeT > 1)
            _currentFadeT = 1;
    }

    private Color getCurrentColor() {
        if (_on == null)
            return Color.BLACK;
        return ShmoMath.lerp(_off, _on, ShmoMath.easeInOutBounce(_currentFadeT));
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null || engine.isPaused())
            return;
        animate(amount, weapon);
        fade(amount, weapon);
    }

    private void animate(float amount, WeaponAPI weapon) {
        final AnimationAPI animation = weapon.getAnimation();
        if (animation == null)
            return;

        final int frameCount = animation.getNumFrames() - 1; // Accounts for first BLANK frame
        final float framesPerSecond = animation.getFrameRate();
        if (frameCount <= 0)
            return;

        while (_frameTimer >= 1.0f) {
            _frameTimer -= 1.0f;
            _currentFrame++;
        }
        while (_currentFrame >= frameCount) {
            _currentFrame -= frameCount;
        }
        _frameTimer += framesPerSecond * amount * _currentFadeT;

        animation.setFrame(_currentFrame + 1);
    }

    private void fade(float amount, WeaponAPI weapon) {
        final ShipAPI ship = weapon.getShip();
        if (ship == null)
            return;

        final SpriteAPI sprite = weapon.getSprite();
        if (_on == null) {
            _on = new Color(
                    sprite.getColor().getRed(),
                    sprite.getColor().getBlue(),
                    sprite.getColor().getGreen(),
                    sprite.getColor().getAlpha()
            );
            _off = new Color(_on.getRed(), _on.getGreen(), _on.getBlue(), 0);
        }

        if (ship.isHulk() || ship.getFluxTracker().isOverloadedOrVenting()) {
            fadeOut(amount);
        } else {
            fadeIn(amount);
        }

        sprite.setColor(getCurrentColor());
    }
}
