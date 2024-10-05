package org.shmo.icfb.campaign.gen;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import org.shmo.icfb.campaign.IcfbFactions;

import java.util.Set;

public interface MarketFactory {
    MarketAPI createMarket(SectorAPI sector, String id, String factionId, SectorEntityToken entity);
}
