package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.campaign.gen.MarketFactory;
import org.shmo.icfb.campaign.gen.impl.markets.CeladonCorvusModeMarketFactory;
import org.shmo.icfb.campaign.gen.impl.markets.LorelaiCorvusModeMarketFactory;
import org.shmo.icfb.campaign.gen.impl.markets.PangeaCorvusModeMarketFactory;
import org.shmo.icfb.campaign.gen.impl.markets.WingsOfEnteriaCorvusModeMarketFactory;

import java.util.*;

public class IcfbMarkets {
    public static MarketData WINGS_OF_ENTERIA = new MarketData("icfb_wings_of_enteria_market");
    public static MarketData LORELAI = new MarketData("icfb_lorelai_market");
    public static MarketData CELADON = new MarketData("icfb_celadon_market");
    public static MarketData PANGEA = new MarketData("icfb_pangea_market");

    public static void generateForCorvusMode(SectorAPI sector) {
        IcfbLog.info("  Initializing markets...");

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
                IcfbPlanets.LORELAI.getPlanet(),
                null,
                true
        );

        CELADON.createMarket(
                new CeladonCorvusModeMarketFactory(),
                sector,
                Factions.INDEPENDENT,
                IcfbPlanets.CELADON.getPlanet(),
                null,
                false
        );

        PANGEA.createMarket(
                new PangeaCorvusModeMarketFactory(),
                sector,
                IcfbFactions.BOUNDLESS.getId(),
                IcfbPlanets.PANGEA.getPlanet(),
                null,
                true
        );
    }

    public static class MarketData {
        private final String _id;

        public MarketData(String id) {
            _id = id;
        }

        public void createMarket(MarketFactory factory, SectorAPI sector, String factionId, SectorEntityToken entity, Set<SectorEntityToken> connectedEntities, boolean withJunkAndChatter) {
            IcfbLog.info("    Creating market: { " + _id + " }...");
            if (isGenerated()) {
                IcfbLog.info("      Skipped!");
                return;
            }

            entity.setFaction(factionId);
            if (connectedEntities != null) {
                for (SectorEntityToken connectedEntity : connectedEntities) {
                    connectedEntity.setFaction(factionId);
                }
            }

            MarketAPI market = factory.createMarket(sector, _id, factionId, entity);

            if (connectedEntities != null) {
                for (SectorEntityToken connectedEntity : connectedEntities) {
                    market.getConnectedEntities().add(connectedEntity);
                }
            }

            market.reapplyIndustries();
            sector.getEconomy().addMarket(market, withJunkAndChatter);
            IcfbLog.info("      Done");

            markAsGenerated();
        }

        public String getId() {
            return _id;
        }

        public MarketAPI getMarket() {
            return Global.getSector().getEconomy().getMarket(_id);
        }

        public boolean isGenerated() {
            return Global.getSector().getMemoryWithoutUpdate().getBoolean(getIsGeneratedKey());
        }

        private void markAsGenerated() {
            Global.getSector().getMemoryWithoutUpdate().set(getIsGeneratedKey(), true);
        }

        private String getKey() {
            return "$IcfbMarkets:" + _id;
        }

        private String getIsGeneratedKey() {
            return getKey() + ":isGenerated";
        }
    }
}
