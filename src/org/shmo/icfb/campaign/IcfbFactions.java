package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.campaign.gen.impl.factions.BoundlessCorvusModeFactionFactory;
import org.shmo.icfb.campaign.gen.FactionFactory;

public class IcfbFactions {
    public static final FactionData BOUNDLESS = new FactionData( "icfb_boundless");

    public static void generateForCorvusMode(SectorAPI sector) {
        IcfbLog.info("- Initializing factions...");

        BOUNDLESS.createFaction(new BoundlessCorvusModeFactionFactory(), sector);
    }

    public static class FactionData {
        private final String _id;

        public FactionData(String id) {
            _id = id;
        }

        private void createFaction(FactionFactory factory, SectorAPI sector) {
            factory.createFaction(sector, _id);
        }

        public String getId() {
            return _id;
        }

        public FactionAPI getFaction() {
            return Global.getSector().getFaction(_id);
        }
    }

}
