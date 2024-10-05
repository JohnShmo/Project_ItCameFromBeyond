package org.shmo.icfb.campaign.gen;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;

public interface PersonFactory {
    PersonAPI createPerson(SectorAPI sector, String id, MarketAPI market);
}
