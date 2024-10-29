package org.shmo.icfb.combat.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;
import org.shmo.icfb.IcfbGlobal;
import org.shmo.icfb.combat.systems.BallisticPreloader;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class BallisticPreloaderHullmod extends BaseHullMod {
    public static final String ID = "icfb_ballistic_preloader_hullmod";

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (ship == null)
            return;
        final List<WeaponAPI> weapons = ship.getAllWeapons();
        final BallisticPreloader.Data data = BallisticPreloader.getDataForShip(ship);
        for (WeaponAPI weapon : weapons) {
            data.tryRegisterWeapon(weapon);
        }
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (ship == null)
            return;
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null)
            return;

        Random random = new Random();
        final BallisticPreloader.Data data = BallisticPreloader.getDataForShip(ship);
        final List<WeaponAPI> weapons = data.getWeaponsWithEntries();

        for (WeaponAPI weapon : weapons) {
            BallisticPreloader.Data.Entry entry = data.getEntryForWeapon(weapon);
            if (!entry.hasCharges())
                continue;
            SpriteAPI sprite;
            if (weapon.getSlot().isTurret()) {
                sprite = Global.getSettings().getSprite(weapon.getSpec().getTurretSpriteName());
            } else {
                sprite = Global.getSettings().getSprite(weapon.getSpec().getHardpointSpriteName());
            }
            if (sprite == null)
                continue;
            Vector2f size = new Vector2f(
                    sprite.getWidth(),
                    sprite.getHeight()
            );
            Vector2f loc = new Vector2f(
                    weapon.getLocation().x + (1f - (random.nextFloat() * 2)),
                    weapon.getLocation().y + (1f - (random.nextFloat() * 2))
            );
            MagicRender.singleframe(
                    sprite,
                    loc,
                    size,
                    weapon.getCurrAngle() - 90,
                    new Color(250, 200, 100, 60),
                    true
            );
            loc = new Vector2f(
                    weapon.getLocation().x + (1.5f - (random.nextFloat() * 3)),
                    weapon.getLocation().y + (1.5f - (random.nextFloat() * 3))
            );
            MagicRender.singleframe(
                    sprite,
                    loc,
                    size,
                    weapon.getCurrAngle() - 90,
                    new Color(255, 150, 100, 60),
                    true
            );
        }
    }
}
