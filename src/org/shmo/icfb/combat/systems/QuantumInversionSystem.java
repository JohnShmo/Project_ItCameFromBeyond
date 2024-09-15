package org.shmo.icfb.combat.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.utilities.ShmoMath;
import org.shmo.icfb.utilities.ShmoRenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuantumInversionSystem extends BaseShipSystemScript {
    public static final String ID = "icfb_quantum_inversion";
    public static final String DATA_KEY = "$ICFB_QUANTUM_INVERSION_DATA";
    public static final String EFFECT_LEVEL_KEY = "$ICFB_QUANTUM_INVERSION_LEVEL";
    public static final String ACTIVE_KEY = "$ICFB_QUANTUM_INVERSION_ACTIVE";
    public static final String PLAYING_KEY = "$ICFB_QUANTUM_INVERSION_PLAYING";

    public static class Frame {

        public static class WeaponData {
            public WeaponAPI weapon;
            public int ammo;
            public float health;
            public float angle;
            public float remainingCooldown;
            public boolean disabled;

            public WeaponData(){}
            public WeaponData(WeaponAPI weapon) {
                this.weapon = weapon;
                ammo = weapon.getAmmo();
                health = weapon.getCurrHealth();
                angle = weapon.getCurrAngle();
                remainingCooldown = weapon.getCooldownRemaining();
                disabled = weapon.isDisabled();
            }

            void apply() {
                weapon.setAmmo(ammo);
                weapon.setCurrHealth(health);
                weapon.setCurrAngle(angle);
                weapon.setRemainingCooldownTo(remainingCooldown);
                if (!disabled) {
                    weapon.repair();
                }
            }

            WeaponData createTween(WeaponData other, float amount) {
                if (other.weapon != weapon)
                    return null;

                if (amount <= 0)
                    return this;
                if (amount >= 1)
                    return other;

                WeaponData result = new WeaponData();

                result.weapon = weapon;
                result.ammo = ShmoMath.lerp(ammo, other.ammo, amount);
                result.health = ShmoMath.lerp(health, other.health, amount);
                result.angle = ShmoMath.lerp(angle, other.angle, amount);
                result.remainingCooldown = ShmoMath.lerp(remainingCooldown, other.remainingCooldown, amount);
                result.disabled = amount > 0.5f ? other.disabled : disabled;

                return result;
            }
        }

        public static class EngineData {
            public ShipEngineControllerAPI.ShipEngineAPI engine;
            public float hitPoints;
            public boolean disabled;

            public EngineData(){}
            public EngineData(ShipEngineControllerAPI.ShipEngineAPI engine) {
                this.engine = engine;
                hitPoints = engine.getHitpoints();
                disabled = engine.isDisabled();
            }

            public void apply() {
                engine.setHitpoints(hitPoints);
                if (!disabled) {
                    engine.repair();
                }
            }

            public EngineData createTween(EngineData other, float amount) {
                if (engine != other.engine)
                    return null;

                if (amount <= 0)
                    return this;
                if (amount >= 1)
                    return other;

                EngineData result = new EngineData();

                result.engine = engine;
                result.hitPoints = ShmoMath.lerp(hitPoints, other.hitPoints, amount);
                result.disabled = amount > 0.5f ? other.disabled : disabled;

                return result;
            }
        }

        public float timeStamp;

        public ShipAPI ship;

        public float flux;
        public float hardFlux;
        public float overloadTime;

        public float[][] armorGrid;
        public float hitPoints;

        public WeaponData[] weapons;
        public EngineData[] engines;

        public float facing;
        public float xLocation;
        public float yLocation;
        public float xVelocity;
        public float yVelocity;

        public Frame(){}
        public Frame(ShipAPI ship, float timeStamp) {
            this.ship = ship;
            this.timeStamp = timeStamp;

            flux = ship.getFluxTracker().getCurrFlux();
            hardFlux = ship.getFluxTracker().getHardFlux();
            overloadTime = ship.getFluxTracker().getOverloadTimeRemaining();

            armorGrid = ShmoMath.copyMatrix(ship.getArmorGrid().getGrid());
            hitPoints = ship.getHitpoints();

            List<WeaponAPI> weaponList = ship.getAllWeapons();
            weapons = new WeaponData[weaponList.size()];
            for (int i = 0; i < weapons.length; i++) {
                weapons[i] = new WeaponData(weaponList.get(i));
            }

            List<ShipEngineControllerAPI.ShipEngineAPI> engineList = ship.getEngineController().getShipEngines();
            engines = new EngineData[engineList.size()];
            for (int i = 0; i < engines.length; i++) {
                engines[i] = new EngineData(engineList.get(i));
            }

            facing = ship.getFacing();
            xLocation = ship.getLocation().x;
            yLocation = ship.getLocation().y;
            xVelocity = ship.getVelocity().x;
            yVelocity = ship.getVelocity().y;
        }

        public void apply() {
            ship.getFluxTracker().setCurrFlux(flux);
            ship.getFluxTracker().setHardFlux(hardFlux);
            if (overloadTime > 0) {
                ship.getFluxTracker().beginOverloadWithTotalBaseDuration(overloadTime);
            }

            float[][] shipArmor = ship.getArmorGrid().getGrid();
            for (int i = 0; i < armorGrid.length; i++) {
                System.arraycopy(armorGrid[i], 0, shipArmor[i], 0, armorGrid[i].length);
            }
            ship.setHitpoints(hitPoints);
            ship.syncWithArmorGridState();
            ship.syncWeaponDecalsWithArmorDamage();

            for (WeaponData weapon : weapons) {
                weapon.apply();
            }

            for (EngineData engine : engines) {
                engine.apply();
            }

            ship.getLocation().set(xLocation, yLocation);
            ship.setFacing(facing);
            ship.getVelocity().set(xVelocity, yVelocity);
        }

        Frame createTween(Frame other, float amount) {
            if (ship != other.ship)
                return null;

            if (amount <= 0)
                return this;
            if (amount >= 1)
                return other;

            Frame result = new Frame();

            result.ship = ship;
            result.timeStamp = ShmoMath.lerp(timeStamp, other.timeStamp, amount);

            result.flux = ShmoMath.lerp(flux, other.flux, amount);
            result.hardFlux = ShmoMath.lerp(hardFlux, other.hardFlux, amount);
            result.overloadTime = ShmoMath.lerp(overloadTime, other.overloadTime, amount);

            result.armorGrid = ShmoMath.lerpMatrix(armorGrid, other.armorGrid, amount);
            result.hitPoints = ShmoMath.lerp(hitPoints, other.hitPoints, amount);

            result.weapons = new WeaponData[weapons.length];
            for (int i = 0; i < result.weapons.length; i++) {
                result.weapons[i] = weapons[i].createTween(other.weapons[i], amount);
            }

            result.engines = new EngineData[engines.length];
            for (int i = 0; i < result.engines.length; i++) {
                result.engines[i] = engines[i].createTween(other.engines[i], amount);
            }

            result.xLocation = ShmoMath.lerp(xLocation, other.xLocation, amount);
            result.yLocation = ShmoMath.lerp(yLocation, other.yLocation, amount);
            result.facing = amount > 0.5 ? facing : other.facing;
            result.xVelocity = ShmoMath.lerp(xVelocity, other.xVelocity, amount);
            result.yVelocity = ShmoMath.lerp(yVelocity, other.yVelocity, amount);

            return result;
        }
    }

    private static class Data {
        private final List<Frame> _keyframes;
        private final ShipAPI _ship;
        private float _currentPlayTime;
        private boolean _playable;

        public Data(ShipAPI ship) {
            _ship = ship;
            _keyframes = new ArrayList<>();
            _currentPlayTime = 0f;
            _playable = false;
        }

        public boolean isPlayable() {
            return _playable;
        }

        public List<Frame> getKeyframes() {
            return _keyframes;
        }

        private void advance(float deltaTime) {
            final float maxTimeSpan = ItCameFromBeyond.Global.getSettings().shipSystem.quantumInversionMaxTimeSpan;

            for (Frame keyframe : _keyframes) {
                keyframe.timeStamp += deltaTime;
            }
            if (!_keyframes.isEmpty() && _keyframes.get(0).timeStamp > maxTimeSpan) {
                _keyframes.remove(0);
            }
            _playable = true;
            _currentPlayTime = 0f;
        }

        public void record(float deltaTime) {
            advance(deltaTime);
            _keyframes.add(new Frame(_ship, 0));
        }

        public void clear() {
            _keyframes.clear();
            _currentPlayTime = 0f;
            _playable = false;
        }

        public boolean play(float deltaTime) {
            _playable = false;

            if (_keyframes.size() < 2) {
                clear();
                return false;
            }
            _currentPlayTime += deltaTime;

            Frame curr = _keyframes.get(_keyframes.size() - 1);
            Frame next = _keyframes.get(_keyframes.size() - 2);
            float diff = next.timeStamp - curr.timeStamp;

            float t = _currentPlayTime / (diff > 0 ? diff : 0.000001f);
            t = Math.min(Math.max(0f, t), 1f);
            Frame frame = curr.createTween(next, t);
            assert frame != null;
            frame.apply();

            if (_currentPlayTime >= diff) {
                _keyframes.remove(_keyframes.size() - 1);
                _currentPlayTime -= diff;
                if (_currentPlayTime > 0f) {
                    deltaTime = _currentPlayTime;
                    _currentPlayTime = 0f;
                    return play(deltaTime); // Recursive call to handle multiple frames
                }
            }
            return true;
        }
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        if (Global.getCombatEngine() == null)
            return;
        ShipAPI ship = (ShipAPI)stats.getEntity();
        if (ship == null)
            return;

        final boolean active = isActive(ship);
        if (state == State.IN || state == State.ACTIVE) {
            if (!active) {
                setActive(ship, true);
                startPlaying(ship);
            } else if (!isPlaying(ship)) {
                ship.getSystem().forceState(ShipSystemAPI.SystemState.OUT, 0f);
            }
            setEffectLevel(ship, effectLevel);
        } else if (active) {
            unapply(ship);
        }
        ship.setJitter(ID, Color.PINK, effectLevel * 0.75f, 2, 16f);
        ship.setJitterUnder(ID, Color.PINK, effectLevel * 0.5f, 2, 32f);
        ShmoRenderUtils.drawShipTrailEffect(ship, new Color(255,255,255, (int)(effectLevel * 50f)), true);
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        if (Global.getCombatEngine() == null)
            return;
        ShipAPI ship = (ShipAPI)stats.getEntity();
        if (ship == null)
            return;
        unapply(ship);
    }

    private static void unapply(ShipAPI ship) {
        setEffectLevel(ship, 0);
        setActive(ship, false);
        stopPlaying(ship);
    }

    public static boolean isActive(ShipAPI ship) {
        return Boolean.TRUE.equals(ship.getCustomData().get(ACTIVE_KEY));
    }

    private static List<Frame> getKeyframes(ShipAPI ship) {
        return getData(ship).getKeyframes();
    }

    private static Frame getFinalKeyframe(ShipAPI ship) {
        List<Frame> keyframes = getKeyframes(ship);
        if (keyframes.isEmpty())
            return new Frame(ship, 0);
        return keyframes.get(0);
    }

    public static Vector2f getFinalLocation(ShipAPI ship) {
        Frame frame = getFinalKeyframe(ship);
        return new Vector2f(frame.xLocation, frame.yLocation);
    }

    public static float getFinalFacing(ShipAPI ship) {
        Frame frame = getFinalKeyframe(ship);
        return frame.facing;
    }

    public static float getFinalHitPoints(ShipAPI ship) {
        Frame frame = getFinalKeyframe(ship);
        return frame.hitPoints;
    }

    public static float getFinalFlux(ShipAPI ship) {
        Frame frame = getFinalKeyframe(ship);
        return frame.flux;
    }

    public static float getFinalHardFlux(ShipAPI ship) {
        Frame frame = getFinalKeyframe(ship);
        return frame.hardFlux;
    }

    public static List<Frame.WeaponData> getFinalWeaponDataList(ShipAPI ship) {
        Frame frame = getFinalKeyframe(ship);
        return Arrays.asList(frame.weapons);
    }

    private static float getFinalAlphaMult(ShipAPI ship) {
        Frame frame = getFinalKeyframe(ship);
        final float maxTimeSpan = ItCameFromBeyond.Global.getSettings().shipSystem.quantumInversionMaxTimeSpan;
        return frame.timeStamp / maxTimeSpan;
    }

    public static float getFinalOverloadTime(ShipAPI ship) {
        Frame frame = getFinalKeyframe(ship);
        return frame.overloadTime;
    }

    public static void drawGhost(ShipAPI ship) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null)
            return;
        if (ship.isExpired() || ship.getHitpoints() <= 0f)
            return;
        if (!getData(ship).isPlayable() && !isPlaying(ship))
            return;

        final Vector2f location = getFinalLocation(ship);
        final List<Frame> keyframes = getKeyframes(ship);
        float facing = getFinalFacing(ship);
        float alphaMult = getFinalAlphaMult(ship);
        final Random random = new Random();

        final Color ghostColor = new Color(250, 200, 240, (int)(40f * alphaMult));
        final Color jitterColor = new Color(255, 150, 200, (int)(15f * alphaMult));
        final Color trailColor = new Color(250, 200, 240, (int)(7f * alphaMult));

        location.x += (random.nextFloat() - 0.5) * 1;
        location.y += (random.nextFloat() - 0.5) * 1;
        ShmoRenderUtils.drawShipSprite(ship, location, facing, ghostColor, true);

        location.x += (random.nextFloat() - 0.5) * 8;
        location.y += (random.nextFloat() - 0.5) * 8;
        facing += (random.nextFloat() - 0.5) * 15;
        ShmoRenderUtils.drawShipJitter(ship, location, facing, jitterColor, true, 2, 2);

        Vector2f lastLocation = new Vector2f();
        for (Frame keyframe : keyframes) {
            final Vector2f trailLocation = new Vector2f(keyframe.xLocation, keyframe.yLocation);
            if (lastLocation.equals(trailLocation))
                continue;
            final float trailFacing = keyframe.facing;
            ShmoRenderUtils.drawShipSprite(ship, trailLocation, trailFacing, trailColor, false);
            lastLocation.set(trailLocation.x, trailLocation.y);
        }
    }

    private static void setActive(ShipAPI ship, boolean active) {
        ship.setCustomData(ACTIVE_KEY, active);
    }

    public static boolean isPlaying(ShipAPI ship) {
        return ship.getCustomData().get(PLAYING_KEY) != null;
    }

    private static void startPlaying(ShipAPI ship) {
        ship.setCustomData(PLAYING_KEY, new Object());
    }

    private static void stopPlaying(ShipAPI ship) {
        ship.setCustomData(PLAYING_KEY, null);
        getData(ship).clear();
    }

    public static void record(ShipAPI ship, float deltaTime) {
        if (ship.isExpired() || ship.getHitpoints() <= 0)
            return;
        getData(ship).record(deltaTime);
    }

    public static void play(ShipAPI ship, float deltaTime) {
        if (ship.isExpired() || ship.getHitpoints() <= 0)
            return;
        final float maxTimeSpan = ItCameFromBeyond.Global.getSettings().shipSystem.quantumInversionMaxTimeSpan;
        Data data = getData(ship);
        deltaTime *= ShmoMath.easeInQuad(getEffectLevel(ship)) * maxTimeSpan * 1.5f;
        if (!data.play(deltaTime)) {
            stopPlaying(ship);
        }
    }

    private static Data getData(ShipAPI ship) {
        Data data = (Data)ship.getCustomData().get(DATA_KEY);
        if (data == null) {
            data = new Data(ship);
            ship.setCustomData(DATA_KEY, data);
        }
        return data;
    }

    public static void clearData(ShipAPI ship) {
        Data data = (Data)ship.getCustomData().get(DATA_KEY);
        if (data == null)
            return;
        data.clear();
        ship.setCustomData(DATA_KEY, null);
    }

    private static float getEffectLevel(ShipAPI ship) {
        Object resultObj = ship.getCustomData().get(EFFECT_LEVEL_KEY);
        if (resultObj == null)
            return 0f;
        return (float)resultObj;
    }

    private static void setEffectLevel(ShipAPI ship, float effectLevel) {
        ship.setCustomData(EFFECT_LEVEL_KEY, effectLevel);
    }

    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        if (!super.isUsable(system, ship))
            return false;
        return getData(ship).isPlayable();
    }
}
