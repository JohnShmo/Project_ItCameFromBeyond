package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.campaign.gen.StarSystemFactory;
import org.shmo.icfb.campaign.gen.impl.starsystems.AzruulCorvusModeStarSystemFactory;
import org.shmo.icfb.campaign.gen.impl.starsystems.KatoCorvusModeStarSystemFactory;
import org.shmo.icfb.campaign.gen.impl.starsystems.NewEnteriaCorvusModeStarSystemFactory;


public class IcfbStarSystems {
    public static final StarSystemData NEW_ENTERIA = new StarSystemData("New Enteria");
    public static final StarSystemData KATO = new StarSystemData("Kato");
    public static final StarSystemData AZRUUL = new StarSystemData("Azruul");

    public static void generateForCorvusMode(SectorAPI sector) {
        IcfbLog.info("  Initializing star systems...");

        NEW_ENTERIA.createStarSystem(
                new NewEnteriaCorvusModeStarSystemFactory(),
                sector,
                1300,
                -19200
        );

        AZRUUL.createStarSystem(
                new AzruulCorvusModeStarSystemFactory(),
                sector,
                -1000,
                -20500
        );

        KATO.createStarSystem(
                new KatoCorvusModeStarSystemFactory(),
                sector,
                38000,
                24200
        );
    }

    public static class StarSystemData {
        private final String _name;

        public StarSystemData(String name) {
            _name = name;
        }

        public void createStarSystem(StarSystemFactory factory, SectorAPI sector, float x, float y) {
            IcfbLog.info("    Creating star system: { " + _name + " }...");
            if (isGenerated()) {
                IcfbLog.info("      Skipped!");
                return;
            }

            factory.createStarSystem(sector, _name, x, y);
            IcfbLog.info("      Done");

            markAsGenerated();
        }

        public StarSystemAPI getStarSystem() {
            return Global.getSector().getStarSystem(_name);
        }

        public boolean isGenerated() {
            return Global.getSector().getMemoryWithoutUpdate().getBoolean(getIsGeneratedKey());
        }

        private void markAsGenerated() {
            Global.getSector().getMemoryWithoutUpdate().set(getIsGeneratedKey(), true);
        }

        private String getKey() {
            return "$IcfbStarSystems:" + _name;
        }

        private String getIsGeneratedKey() {
            return getKey() + ":isGenerated";
        }
    }
}
