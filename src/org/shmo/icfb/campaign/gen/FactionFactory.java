package org.shmo.icfb.campaign.gen;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;

public interface FactionFactory {
    FactionAPI createFaction(SectorAPI sector, String id);
}
