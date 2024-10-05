package org.shmo.icfb.campaign.gen.impl.entities;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import org.shmo.icfb.campaign.gen.EntityFactory;

public class WingsOfEnteriaCorvusModeEntityFactory implements EntityFactory {

    public SectorEntityToken createEntity(SectorAPI sector, String id, SectorEntityToken orbitFocus, float angle, float orbitDistance, float orbitDays) {
        final SectorEntityToken entity = orbitFocus.getContainingLocation().addCustomEntity(
                id,
                "Wings of Enteria",
                "icfb_wings_of_enteria",
                Factions.NEUTRAL
        );
        entity.setCircularOrbit(orbitFocus, angle, orbitDistance, orbitDays);
        entity.setCustomDescriptionId("icfb_wings_of_enteria");
        entity.getMemoryWithoutUpdate().set(MemFlags.STORY_CRITICAL, true);

        return entity;
    }
}
