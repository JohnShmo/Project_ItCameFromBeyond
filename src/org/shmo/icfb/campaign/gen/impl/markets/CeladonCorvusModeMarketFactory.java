package org.shmo.icfb.campaign.gen.impl.markets;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.util.Misc;
import org.magiclib.util.MagicCampaign;
import org.shmo.icfb.campaign.gen.MarketFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class CeladonCorvusModeMarketFactory implements MarketFactory {
    @Override
    public MarketAPI createMarket(SectorAPI sector, String id, String factionId, SectorEntityToken entity) {
        MarketAPI market = MagicCampaign.addSimpleMarket(
                entity,
                id,
                "Celadon",
                4,
                factionId,
                false,
                false,
                new ArrayList<>(Arrays.asList(
                        Conditions.POPULATION_4,
                        Conditions.VOLATILES_ABUNDANT,
                        Conditions.RARE_ORE_ABUNDANT,
                        Conditions.ORE_ABUNDANT,
                        Conditions.HOT,
                        Conditions.NO_ATMOSPHERE
                )),
                new ArrayList<>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
                        Industries.WAYSTATION,
                        Industries.GROUNDDEFENSES,
                        Industries.MINING,
                        Industries.REFINING
                )),
                true,
                true,
                true,
                true,
                false,
                false
        );
        market.setHasSpaceport(true);

        return market;
    }
}
