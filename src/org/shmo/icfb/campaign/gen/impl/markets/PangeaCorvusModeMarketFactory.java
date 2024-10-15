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

public class PangeaCorvusModeMarketFactory implements MarketFactory {
    @Override
    public MarketAPI createMarket(SectorAPI sector, String id, String factionId, SectorEntityToken entity) {
        MarketAPI market = MagicCampaign.addSimpleMarket(
                entity,
                id,
                "Pangea",
                6,
                factionId,
                false,
                false,
                new ArrayList<>(Arrays.asList(
                        Conditions.POPULATION_6,
                        Conditions.FARMLAND_BOUNTIFUL,
                        Conditions.HABITABLE,
                        Conditions.INIMICAL_BIOSPHERE
                )),
                new ArrayList<>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
                        Industries.WAYSTATION,
                        Industries.PATROLHQ,
                        Industries.HEAVYBATTERIES,
                        Industries.FARMING,
                        Industries.LIGHTINDUSTRY,
                        Industries.REFINING,
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
