package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.campaign.gen.StarSystemFactory;
import org.shmo.icfb.campaign.gen.impl.starsystems.KatoCorvusModeStarSystemFactory;
import org.shmo.icfb.campaign.gen.impl.starsystems.NewEnteriaCorvusModeStarSystemFactory;


public class IcfbStarSystems {
    public static final StarSystemData NEW_ENTERIA = new StarSystemData("New Enteria");
    public static final StarSystemData KATO = new StarSystemData("Kato");

    public static void generateForCorvusMode(SectorAPI sector) {
        IcfbLog.info("- Initializing star systems...");

        NEW_ENTERIA.createStarSystem(
                new NewEnteriaCorvusModeStarSystemFactory(),
                sector,
                1300,
                -22200
        );

        KATO.createStarSystem(
                new KatoCorvusModeStarSystemFactory(),
                sector,
                -500,
                -22200
        );
    }

    public static class StarSystemData {
        private final String _name;

        public StarSystemData(String name) {
            _name = name;
        }

        private void createStarSystem(StarSystemFactory factory, SectorAPI sector, float x, float y) {
            factory.createStarSystem(sector, _name, x, y);
        }

        public StarSystemAPI getStarSystem() {
            return Global.getSector().getStarSystem(_name);
        }
    }
}
