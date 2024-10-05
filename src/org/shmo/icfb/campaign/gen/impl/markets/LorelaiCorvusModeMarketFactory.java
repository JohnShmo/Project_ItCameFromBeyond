package org.shmo.icfb.campaign.gen.impl.markets;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import org.magiclib.util.MagicCampaign;
import org.shmo.icfb.campaign.gen.MarketFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class LorelaiCorvusModeMarketFactory implements MarketFactory {
    public MarketAPI createMarket(SectorAPI sector, String id, String factionId, SectorEntityToken entity) {
        MarketAPI market = MagicCampaign.addSimpleMarket(
                entity,
                id,
                "Lorelai",
                5,
                factionId,
                false,
                false,
                new ArrayList<>(Arrays.asList(
                        Conditions.POPULATION_5,
                        Conditions.FARMLAND_RICH,
                        Conditions.EXTREME_WEATHER,
                        Conditions.TECTONIC_ACTIVITY,
                        Conditions.HABITABLE,
                        Conditions.ORGANICS_COMMON,
                        Conditions.ORE_MODERATE,
                        Conditions.RARE_ORE_MODERATE
                )),
                new ArrayList<>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
                        Industries.WAYSTATION,
                        Industries.PATROLHQ,
                        Industries.GROUNDDEFENSES,
                        Industries.FARMING,
                        Industries.FUELPROD,
                        Industries.MINING,
                        Industries.BATTLESTATION_MID
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
