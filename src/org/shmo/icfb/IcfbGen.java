package org.shmo.icfb;

import com.fs.starfarer.api.campaign.*;
import org.shmo.icfb.campaign.*;

public class IcfbGen {

    public static void generateForCorvusMode(SectorAPI sector) {
        IcfbLog.info("Generating for Corvus mode...");

        IcfbFactions.generateForCorvusMode(sector);
        IcfbStarSystems.generateForCorvusMode(sector);
        IcfbEntities.generateForCorvusMode(sector);
        IcfbMarkets.generateForCorvusMode(sector);
        IcfbPeople.generateForCorvusMode(sector);

        sector.getMemoryWithoutUpdate().set(IcfbMemFlags.GENERATED_FOR_CORVUS, true);
        IcfbLog.info("Finished generating for Corvus mode!");
    }

    public static boolean hasAlreadyGeneratedForCorvus(SectorAPI sector) {
        return sector.getMemoryWithoutUpdate().contains(IcfbMemFlags.GENERATED_FOR_CORVUS);
    }
}
