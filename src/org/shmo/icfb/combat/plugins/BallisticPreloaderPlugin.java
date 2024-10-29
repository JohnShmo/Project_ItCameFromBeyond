package org.shmo.icfb.combat.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;
import org.shmo.icfb.IcfbGlobal;
import org.shmo.icfb.combat.systems.BallisticPreloader;

import java.util.List;
import java.util.Random;

public class BallisticPreloaderPlugin extends BaseEveryFrameCombatPlugin {
    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine == null)
            return;
        Random random = new Random();
        final List<DamagingProjectileAPI> projectiles = engine.getProjectiles();
        for (DamagingProjectileAPI projectile : projectiles) {
            ShipAPI ship = projectile.getSource();
            if (ship == null)
                continue;
            ShipSystemAPI system = ship.getSystem();
            if (system == null || !system.getId().equals(BallisticPreloader.ID))
                continue;
            BallisticPreloader.Data data = BallisticPreloader.getDataForShip(ship);
            WeaponAPI weapon = projectile.getWeapon();
            if (!data.hasEntryForWeapon(weapon))
                continue;
            BallisticPreloader.Data.Entry entry = data.getEntryForWeapon(weapon);
            if (projectile.getCustomData().get(BallisticPreloader.LATCH_KEY) != null) {
                if (projectile.getCustomData().get(BallisticPreloader.ID) == null)
                    continue;
                SpriteAPI sprite = Global.getSettings().getSprite(projectile.getProjectileSpec().getBulletSpriteName());
                Vector2f size = new Vector2f(
                        projectile.getProjectileSpec().getLength() + 10,
                        projectile.getProjectileSpec().getWidth() + 10
                );
                Vector2f loc = new Vector2f(
                        projectile.getLocation().x + (5 - (random.nextFloat() * 10)),
                        projectile.getLocation().y + (5 - (random.nextFloat() * 10))
                );
                MagicRender.singleframe(
                        sprite,
                        loc,
                        size,
                        projectile.getFacing(),
                        projectile.getProjectileSpec().getCoreColor(),
                        true
                );
                continue;
            }
            projectile.setCustomData(BallisticPreloader.LATCH_KEY, new Object());
            if (!entry.hasCharges())
                continue;
            entry.expendCharge();
            DamageAPI damage = projectile.getDamage();
            damage.getModifier().modifyPercent(BallisticPreloader.ID, IcfbGlobal.getSettings().shipSystem.ballisticPreloaderBonusDamagePercent);
            projectile.setCustomData(BallisticPreloader.ID, new Object());
        }
    }
}
