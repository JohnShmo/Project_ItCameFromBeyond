package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.campaign.gen.impl.factions.BoundlessCorvusModeFactionFactory;
import org.shmo.icfb.campaign.gen.FactionFactory;

public class IcfbFactions {
    public static final FactionData BOUNDLESS = new FactionData( "icfb_boundless");

    public static void generateForCorvusMode(SectorAPI sector) {
        IcfbLog.info("  Initializing factions...");

        BOUNDLESS.createFaction(new BoundlessCorvusModeFactionFactory(), sector);
    }

    public static class FactionData {
        private final String _id;

        public FactionData(String id) {
            _id = id;
        }

        public void createFaction(FactionFactory factory, SectorAPI sector) {
            IcfbLog.info("    Creating faction: { " + _id + " }...");
            if (isGenerated()) {
                IcfbLog.info("      Skipped!");
                return;
            }

            factory.createFaction(sector, _id);
            IcfbLog.info("      Done");

            markAsGenerated();
        }

        public String getId() {
            return _id;
        }

        public FactionAPI getFaction() {
            return Global.getSector().getFaction(_id);
        }

        public boolean isGenerated() {
            return Global.getSector().getMemoryWithoutUpdate().getBoolean(getIsGeneratedKey());
        }

        private void markAsGenerated() {
            Global.getSector().getMemoryWithoutUpdate().set(getIsGeneratedKey(), true);
        }

        private String getKey() {
            return "$IcfbFactions:" + _id;
        }

        private String getIsGeneratedKey() {
            return getKey() + ":isGenerated";
        }
    }

}
