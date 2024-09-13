package org.shmo.icfb.combat.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import org.shmo.icfb.utilities.ShmoMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuantumInversionSystem extends BaseShipSystemScript {
    public static final String ID = "icfb_quantum_inversion";
    public static final String DATA_KEY = "$ICFB_QUANTUM_INVERSION_DATA";
    public static final String EFFECT_LEVEL_KEY = "$ICFB_QUANTUM_INVERSION_LEVEL";
    public static final String ACTIVE_KEY = "$ICFB_QUANTUM_INVERSION_ACTIVE";
    public static final String PLAYING_KEY = "$ICFB_QUANTUM_INVERSION_PLAYING";

    private static class Keyframe {

        private static class WeaponData {
            WeaponAPI weapon;
            int ammo;
            float health;
            float angle;
            float remainingCooldown;
            boolean disabled;

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
                if (disabled) {
                    weapon.disable();
                } else {
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
                result.disabled = amount >= 0.5f ? other.disabled : disabled;

                return result;
            }
        }

        public float timeStamp;

        ShipAPI ship;

        public float flux;
        public float hardFlux;
        float overloadTime;

        public float[][] armorGrid;
        public float hitPoints;

        public WeaponData[] weapons;

        float xLocation;
        float yLocation;
        float facing;
        float xVelocity;
        float yVelocity;

        public Keyframe(){}
        public Keyframe(ShipAPI ship, float timeStamp) {
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

            xLocation = ship.getLocation().x;
            yLocation = ship.getLocation().y;
            facing = ship.getFacing();
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

            for (WeaponData weapon : weapons) {
                weapon.apply();
            }

            ship.getLocation().set(xLocation, yLocation);
            ship.setFacing(facing);
            ship.getVelocity().set(xVelocity, yVelocity);
        }

        Keyframe createTween(Keyframe other, float amount) {
            if (ship != other.ship)
                return null;

            if (amount <= 0)
                return this;
            if (amount >= 1)
                return other;

            Keyframe result = new Keyframe();

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

            result.xLocation = ShmoMath.lerp(xLocation, other.xLocation, amount);
            result.yLocation = ShmoMath.lerp(yLocation, other.yLocation, amount);
            result.facing = ShmoMath.lerp(facing, other.facing, amount);
            result.xVelocity = ShmoMath.lerp(xVelocity, other.xVelocity, amount);
            result.yVelocity = ShmoMath.lerp(yVelocity, other.yVelocity, amount);

            return result;
        }
    }

    public static class Data {
        private static final float MAX_TIME_STAMP = 4f; // 3 seconds
        private final List<Keyframe> _keyframes;
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

        private void advance(float deltaTime) {
            for (Keyframe keyframe : _keyframes) {
                keyframe.timeStamp += deltaTime;
            }
            if (!_keyframes.isEmpty() && _keyframes.get(0).timeStamp > MAX_TIME_STAMP) {
                _keyframes.remove(0);
            }
            _playable = true;
            _currentPlayTime = 0f;
        }

        public void record(float deltaTime) {
            advance(deltaTime);
            _keyframes.add(new Keyframe(_ship, 0));
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

            Keyframe curr = _keyframes.get(_keyframes.size() - 1);
            Keyframe next = _keyframes.get(_keyframes.size() - 2);
            float diff = next.timeStamp - curr.timeStamp;

            float t = _currentPlayTime / (diff > 0 ? diff : 0.000001f);
            t = Math.min(Math.max(0f, t), 1f);
            Keyframe frame = curr.createTween(next, t);
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
                ship.getSystem().forceState(ShipSystemAPI.SystemState.OUT, 1f);
            }
            setEffectLevel(ship, effectLevel);
        } else if (active) {
            unapply(ship);
        }
        ship.setJitter(ID, Color.PINK, effectLevel * 0.5f, 2, 16f);
        ship.setJitterUnder(ID, Color.CYAN, effectLevel, 3, 32f);
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
        getData(ship).record(deltaTime);
    }

    public static void play(ShipAPI ship, float deltaTime) {
        Data data = getData(ship);
        deltaTime *= getEffectLevel(ship) * 2.5f;
        if (!data.play(deltaTime)) {
            stopPlaying(ship);
        }
    }

    public static Data getData(ShipAPI ship) {
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
