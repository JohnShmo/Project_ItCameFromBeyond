package org.shmo.icfb.combat.entities;

import com.fs.starfarer.api.combat.CombatEntityAPI;
import org.lazywizard.lazylib.combat.entities.AnchoredEntity;
import org.lwjgl.util.vector.Vector2f;

public class DeflectorEntity extends AnchoredEntity {

    private float _facing = 0;

    public DeflectorEntity(CombatEntityAPI anchor, Vector2f location, float facing) {
        super(anchor, location);
        setFacing(anchor.getFacing() + facing);
    }

    @Override
    public float getFacing() {
        if (anchor == null)
            return _facing;
        return anchor.getFacing() + _facing;
    }

    @Override
    public void setFacing(float facing) {
        _facing = facing;
    }

    @Override
    public float getCollisionRadius() {
        if (anchor == null)
            return 0f;
        return anchor.getCollisionRadius();
    }
}
