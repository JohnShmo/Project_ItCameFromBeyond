package org.shmo.icfb.campaign.generation.entities;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import org.magiclib.util.MagicCampaign;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondEntities;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondFactions;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondMarkets;

import java.util.ArrayList;
import java.util.Arrays;

public class WingsOfEnteria {
    public static SectorEntityToken generate(SectorAPI sector, SectorEntityToken orbitFocus, float orbitDistance, float orbitDays) {
        final SectorEntityToken wingsOfEnteria = orbitFocus.getContainingLocation().addCustomEntity(
                ItCameFromBeyondEntities.WINGS_OF_ENTERIA,
                "Wings of Enteria",
                ItCameFromBeyondEntities.WINGS_OF_ENTERIA,
                ItCameFromBeyondFactions.BOUNDLESS
        );
        wingsOfEnteria.setCircularOrbit(orbitFocus, 90, orbitDistance, orbitDays);
        wingsOfEnteria.setCustomDescriptionId(ItCameFromBeyondEntities.WINGS_OF_ENTERIA);

        final MarketAPI wingsOfEnteriaMarket = MagicCampaign.addSimpleMarket(
                wingsOfEnteria,
                ItCameFromBeyondMarkets.WINGS_OF_ENTERIA,
                "Wings of Enteria",
                6,
                ItCameFromBeyondFactions.BOUNDLESS,
                false,
                false,
                new ArrayList<>(Arrays.asList(
                        Conditions.POPULATION_6,
                        Conditions.HABITABLE
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

        wingsOfEnteriaMarket.setHasSpaceport(true);
        wingsOfEnteria.getMemoryWithoutUpdate().set(MemFlags.STORY_CRITICAL, true);
        SpecialItemData pristineNano = new SpecialItemData("pristine_nanoforge", null);
        wingsOfEnteriaMarket.getIndustry(Industries.ORBITALWORKS).setSpecialItem(pristineNano);
        wingsOfEnteriaMarket.reapplyIndustries();
        sector.getEconomy().addMarket(wingsOfEnteriaMarket, true);
        return wingsOfEnteria;
    }
}
