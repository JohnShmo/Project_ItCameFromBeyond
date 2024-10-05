package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.campaign.gen.MarketFactory;
import org.shmo.icfb.campaign.gen.impl.markets.LorelaiCorvusModeMarketFactory;
import org.shmo.icfb.campaign.gen.impl.markets.WingsOfEnteriaCorvusModeMarketFactory;

import java.util.*;

public class IcfbMarkets {
    public static MarketData WINGS_OF_ENTERIA = new MarketData("icfb_wings_of_enteria_market");
    public static MarketData LORELAI = new MarketData("icfb_lorelai_market");

    public static void generateForCorvusMode(SectorAPI sector) {
        IcfbLog.info("- Initializing markets...");

        WINGS_OF_ENTERIA.createMarket(
                new WingsOfEnteriaCorvusModeMarketFactory(),
                sector,
                IcfbFactions.BOUNDLESS.getId(),
                IcfbEntities.WINGS_OF_ENTERIA.getEntity(),
                null,
                true
        );

        LORELAI.createMarket(
                new LorelaiCorvusModeMarketFactory(),
                sector,
                IcfbFactions.BOUNDLESS.getId(),
                IcfbPlanets.NEW_ENTERIA.LORELAI.getPlanet(),
                null,
                true
        );
    }

    public static class MarketData {
        private final String _id;

        public MarketData(String id) {
            _id = id;
        }

        private void createMarket(MarketFactory factory, SectorAPI sector, String factionId, SectorEntityToken entity, Set<SectorEntityToken> connectedEntities, boolean withJunkAndChatter) {
            MarketAPI market = factory.createMarket(sector, _id, factionId, entity);

            entity.setFaction(factionId);
            if (connectedEntities != null) {
                for (SectorEntityToken connectedEntity : connectedEntities) {
                    connectedEntity.setFaction(factionId);
                    market.getConnectedEntities().add(connectedEntity);
                }
            }
            market.reapplyIndustries();
            sector.getEconomy().addMarket(market, withJunkAndChatter);
        }

        public String getId() {
            return _id;
        }

        public MarketAPI getMarket() {
            return Global.getSector().getEconomy().getMarket(_id);
        }
    }
}
