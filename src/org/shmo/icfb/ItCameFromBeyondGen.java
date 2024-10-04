package org.shmo.icfb;

import com.fs.starfarer.api.campaign.*;
import org.shmo.icfb.campaign.*;

public class ItCameFromBeyondGen {

    public static void generateForCorvusMode(SectorAPI sector) {
        final float newEnteriaLocationX = 1300;
        final float newEnteriaLocationY = -22200;
        final float katoLocationX = -500;
        final float katoLocationY = -22200;
        final float wingsOfEnteriaOrbitDistance = 600;
        final float wingsOfEnteriaOrbitDays = 20;

        ItCameFromBeyond.Log.info("Generating for Corvus mode...");

        // Initialize factions
        IcfbFactions.Boundless.initFaction(sector);

        // Generate systems
        final StarSystemAPI newEnteria = IcfbStarSystems.NewEnteria.createSystem(sector, newEnteriaLocationX, newEnteriaLocationY);
        final StarSystemAPI kato = IcfbStarSystems.Kato.createSystem(sector, katoLocationX, katoLocationY);

        // Generate entities
        SectorEntityToken wingsOfEnteria = IcfbEntities.WingsOfEnteria.createEntity(
                sector,
                newEnteria.getEntityById(IcfbStarSystems.NewEnteria.LUMINARU_MAJOR),
                wingsOfEnteriaOrbitDistance,
                wingsOfEnteriaOrbitDays
        );
        IcfbEntities.ChariotOfHope.createEntity(
                sector,
                kato.getEntityById(IcfbStarSystems.Kato.MOLLY),
                -90,
                170,
                15
        );

        // Generate markets
        IcfbMarkets.WingsOfEnteria.createMarket(
                sector,
                wingsOfEnteria,
                null
        );
        IcfbMarkets.Lorelai.createMarket(
                sector,
                newEnteria.getEntityById(IcfbStarSystems.NewEnteria.LORELAI),
                null
        );

        // Generate people
        IcfbPeople.generateForCorvus(sector);

        sector.getMemoryWithoutUpdate().set(IcfbMemFlags.GENERATED_FOR_CORVUS, true);
        ItCameFromBeyond.Log.info("Finished generating for Corvus mode!");
    }

    public static boolean hasAlreadyGeneratedForCorvus(SectorAPI sector) {
        return sector.getMemoryWithoutUpdate().contains(IcfbMemFlags.GENERATED_FOR_CORVUS);
    }
}
