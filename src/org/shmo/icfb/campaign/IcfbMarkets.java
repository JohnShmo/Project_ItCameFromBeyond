package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import org.magiclib.util.MagicCampaign;

import java.util.*;

public class IcfbMarkets {

    private static MarketAPI getMarket(SectorAPI sector, String marketId) {
        return sector.getEconomy().getMarket(marketId);
    }

    private static void addMarket(SectorAPI sector, MarketAPI market, boolean withJunkAndChatter) {
        market.reapplyIndustries();
        sector.getEconomy().addMarket(market, withJunkAndChatter);
    }

    private static void applyFactionToMarketEntities(String factionId, SectorEntityToken primaryEntity, Set<SectorEntityToken> connectedEntities) {
        primaryEntity.setFaction(factionId);
        if (connectedEntities != null) {
            for (SectorEntityToken connectedEntity : connectedEntities) {
                connectedEntity.setFaction(IcfbFactions.Boundless.ID);
            }
        }
    }

    private static void connectEntitiesToMarket(MarketAPI market, Set<SectorEntityToken> connectedEntities) {
        if (connectedEntities != null) {
            for (SectorEntityToken connectedEntity : connectedEntities) {
                market.getConnectedEntities().add(connectedEntity);
            }
        }
    }

    public static class WingsOfEnteria {
        public static final String ID = "icfb_wings_of_enteria_market";

        public static MarketAPI getMarket() {
            return IcfbMarkets.getMarket(Global.getSector(), ID);
        }

        public static MarketAPI createMarket(SectorAPI sector, SectorEntityToken entity, Set<SectorEntityToken> connectedEntities) {
            final MarketAPI market = MagicCampaign.addSimpleMarket(
                    entity,
                    ID,
                    "Wings of Enteria",
                    6,
                    IcfbFactions.Boundless.ID,
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
                            Industries.MILITARYBASE
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

            applyFactionToMarketEntities(IcfbFactions.Boundless.ID, entity, connectedEntities);
            connectEntitiesToMarket(market, connectedEntities);
            addMarket(sector, market, true);
            return market;
        }
    }

    public static class Lorelai {
        public static final String ID = "icfb_lorelai_market";

        public static MarketAPI getMarket() {
            return IcfbMarkets.getMarket(Global.getSector(), ID);
        }

        public static MarketAPI createMarket(SectorAPI sector, SectorEntityToken entity, Set<SectorEntityToken> connectedEntities) {
            MarketAPI market = MagicCampaign.addSimpleMarket(
                    entity,
                    ID,
                    "Lorelai",
                    5,
                    IcfbFactions.Boundless.ID,
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

            applyFactionToMarketEntities(IcfbFactions.Boundless.ID, entity, connectedEntities);
            connectEntitiesToMarket(market, connectedEntities);
            addMarket(sector, market, true);
            return market;
        }
    }
}
