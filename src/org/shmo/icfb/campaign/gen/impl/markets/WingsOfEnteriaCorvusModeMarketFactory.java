package org.shmo.icfb.campaign.gen.impl.markets;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import org.magiclib.util.MagicCampaign;
import org.shmo.icfb.campaign.gen.MarketFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class WingsOfEnteriaCorvusModeMarketFactory implements MarketFactory {
    public MarketAPI createMarket(SectorAPI sector, String id, String factionId, SectorEntityToken entity) {
        final MarketAPI market = MagicCampaign.addSimpleMarket(
                entity,
                id,
                "Wings of Enteria",
                6,
                factionId,
                false,
                false,
                new ArrayList<>(Collections.singletonList(
                        Conditions.POPULATION_6
                )),
                new ArrayList<>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
                        Industries.WAYSTATION,
                        Industries.STARFORTRESS_MID,
                        Industries.HEAVYBATTERIES,
                        Industries.REFINING,
                        Industries.ORBITALWORKS,
                        Industries.HIGHCOMMAND
                )),
                true,
                true,
                true,
                true,
                true,
                false
        );
        market.setHasSpaceport(true);
        SpecialItemData pristineNano = new SpecialItemData("pristine_nanoforge", null);
        market.getIndustry(Industries.ORBITALWORKS).setSpecialItem(pristineNano);

        return market;
    }
}
