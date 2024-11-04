package org.shmo.icfb.combat.weapons;

import com.fs.starfarer.api.AnimationAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.icfb.utilities.ShmoCombatUtils;
import org.shmo.icfb.utilities.ShmoMath;

import java.awt.*;
import java.util.Random;

public class Tendril implements EveryFrameWeaponEffectPlugin {
    private static final float FADE_SPEED = 1f;
    private static final float FLICKER_FADE_SPEED = 3f;
    private static final float FLICKER_CHANCE = 3f;
    private static final float FLICKER_DELTA = 30f;
    private static final float BASE_ALPHA_MULT = 0.5f;
    private static final float ANGLE_APPROACH_MULT = 5f;

    private final Random _random = new Random();
    private boolean _initialized = false;
    private boolean _isHulk = false;
    private Color _on = null;
    private Color _off = null;
    private float _currentFadeT = 1f;
    private float _frameTimer = 0f;
    private int _currentFrame = 0;
    private int _flickerAlpha = 0;
    private float _currentFlickerGoal = 0f;
    private float _currentFlickerValue = 0f;

    private float _startAngle = 0f;
    private float _currentAngle = 0f;
    private float _minAngleDiff = 0f;
    private float _maxAngleDiff = 0f;
    private float _goalAngle = 0f;

    private void init(WeaponAPI weapon) {
        final ShipAPI ship = weapon.getShip();
        if (ship == null)
            return;

        if (_initialized)
            return;
        _initialized = true;

        final SpriteAPI sprite = weapon.getSprite();
        _on = new Color(
                sprite.getColor().getRed(),
                sprite.getColor().getBlue(),
                sprite.getColor().getGreen(),
                (int)(sprite.getColor().getAlpha() * BASE_ALPHA_MULT)
        );
        _off = new Color(_on.getRed(), _on.getGreen(), _on.getBlue(), (int)(sprite.getColor().getAlpha() * 0.1f));
        _flickerAlpha = (int)(sprite.getColor().getAlpha() * (1f - BASE_ALPHA_MULT));

        _startAngle = weapon.getSlot().getAngle();
        _currentAngle = _startAngle;
        _goalAngle = _startAngle;
        _maxAngleDiff = weapon.getSlot().getArc() / 2;
        _minAngleDiff = -_maxAngleDiff;
    }

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

    private void flicker(float amount) {
        final float val = _random.nextFloat();
        if (val <= FLICKER_CHANCE * amount)
            _currentFlickerGoal = 1f;
    }

    private void animateFlicker(float amount) {
        if (_currentFlickerValue < _currentFlickerGoal) {
            _currentFlickerValue += amount * FLICKER_DELTA;
            if (_currentFlickerValue > 1)
                _currentFlickerValue = 1;
        } else if (_currentFlickerValue > _currentFlickerGoal) {
            _currentFlickerValue -= amount * FLICKER_DELTA;
            if (_currentFlickerValue < 0)
                _currentFlickerValue = 0;
        }
        _currentFlickerGoal -= amount * FLICKER_FADE_SPEED;
        if (_currentFlickerGoal < 0)
            _currentFlickerGoal = 0;
    }

    private void animateAngle(float amount, WeaponAPI weapon) {
        ShipAPI ship = weapon.getShip();
        if (ship == null)
            return;

        Vector2f shipVelocity = ShmoCombatUtils.computeShipAccelerationVectorLocal(ship);

        if (shipVelocity.lengthSquared() == 0) {
            float angleDiff = ship.getAngularVelocity();
            angleDiff = Math.min(_maxAngleDiff, Math.max(angleDiff, _minAngleDiff));
            _goalAngle = _startAngle - angleDiff;
        } else {
            shipVelocity.x = -shipVelocity.x;
            shipVelocity.y = -shipVelocity.y;

            float velocityAngle = Misc.getAngleInDegrees(shipVelocity);
            float angleDiff = ShmoMath.angleDifference(_startAngle, velocityAngle);
            float sign = Math.signum(ShmoMath.signedAngleDifference(_startAngle, velocityAngle));
            angleDiff = sign * ShmoMath.dampen(angleDiff, 180f, 2);
            angleDiff += ship.getAngularVelocity();
            angleDiff = Math.min(_maxAngleDiff, Math.max(angleDiff, _minAngleDiff));

            _goalAngle = _startAngle - angleDiff;
        }

        _currentAngle = ShmoMath.lerp(_currentAngle, _goalAngle, amount * ANGLE_APPROACH_MULT);
        weapon.setCurrAngle(Misc.normalizeAngle(_currentAngle + ship.getFacing()));
    }

    private Color getCurrentColor() {
        if (_on == null)
            return Color.BLACK;
        int onAlpha = _on.getAlpha() + (int)(_flickerAlpha * _currentFlickerValue);
        int offAlpha = 0;
        if (!_isHulk) {
            offAlpha = _off.getAlpha() + (int)(_flickerAlpha * _currentFlickerValue);
        }
        Color on = new Color(
            _on.getRed(),
            _on.getGreen(),
            _on.getBlue(), onAlpha
        );
        Color off = new Color(
                _off.getRed(),
                _off.getGreen(),
                _off.getBlue(), offAlpha
        );
        return ShmoMath.lerp(off, on, ShmoMath.easeInOutBounce(_currentFadeT));
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null || engine.isPaused())
            return;
        init(weapon);
        animate(amount, weapon);
        fadeAndFlicker(amount, weapon);
        animateAngle(amount, weapon);
    }

    private void animate(float amount, WeaponAPI weapon) {
        final AnimationAPI animation = weapon.getAnimation();
        if (animation == null)
            return;

        final int frameCount = animation.getNumFrames();
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
        _frameTimer += framesPerSecond * amount;

        animation.setFrame(_currentFrame);
    }

    private void fadeAndFlicker(float amount, WeaponAPI weapon) {
        final ShipAPI ship = weapon.getShip();
        if (ship == null)
            return;

        final SpriteAPI sprite = weapon.getSprite();

        if (ship.isHulk() || ship.getFluxTracker().isOverloadedOrVenting()) {
            fadeOut(amount);
            if (ship.isHulk())
                _isHulk = true;
        } else {
            fadeIn(amount);
        }

        flicker(amount);
        animateFlicker(amount);
        sprite.setColor(getCurrentColor());
    }
}
