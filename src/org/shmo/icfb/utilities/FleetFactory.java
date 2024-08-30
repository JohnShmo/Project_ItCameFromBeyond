package org.shmo.icfb.utilities;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;

public interface FleetFactory {
    CampaignFleetAPI create(SectorEntityToken spawnLocation);
}
