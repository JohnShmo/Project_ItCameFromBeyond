package org.shmo.icfb.campaign.gen;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;

public interface EntityFactory {
    SectorEntityToken createEntity(SectorAPI sector, String id, SectorEntityToken orbitFocus, float angle, float orbitDistance, float orbitDays);
}
