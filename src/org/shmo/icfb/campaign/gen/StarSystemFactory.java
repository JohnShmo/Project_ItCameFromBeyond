package org.shmo.icfb.campaign.gen;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;

public interface StarSystemFactory {
    StarSystemAPI createStarSystem(SectorAPI sector, String name, float x, float y);
}
