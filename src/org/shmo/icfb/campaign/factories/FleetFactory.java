package org.shmo.icfb.campaign.factories;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;

public interface FleetFactory {
    CampaignFleetAPI create(SectorEntityToken spawnLocation);
}
