package org.shmo.icfb.campaign.quests.impl.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import org.magiclib.campaign.MagicFleetBuilder;
import org.shmo.icfb.campaign.quests.impl.TestQuest;
import org.shmo.icfb.campaign.quests.scripts.DefeatFleetQuestStepScript;
import org.shmo.icfb.campaign.factories.FleetFactory;

public class TestQuestStepScript extends DefeatFleetQuestStepScript {
    public TestQuestStepScript() {
        setSpawnLocation(TestQuest.getPirateSpawnLocation());
        setWinCondition(WinCondition.DEFEAT_FLAGSHIP);
        setFleetFactory(new FleetFactory() {
            @Override
            public CampaignFleetAPI create(SectorEntityToken spawnLocation) {
                MagicFleetBuilder fleetBuilder = new MagicFleetBuilder();
                fleetBuilder.setFleetFaction(Factions.PIRATES);
                fleetBuilder.setMinFP(20);
                fleetBuilder.setFleetName("Big Bad Pirates");
                fleetBuilder.setFlagshipName("Killmonger");
                fleetBuilder.setSpawnLocation(spawnLocation);
                fleetBuilder.setAssignmentTarget(spawnLocation);
                fleetBuilder.setAssignment(FleetAssignment.ORBIT_AGGRESSIVE);
                fleetBuilder.setTransponderOn(false);
                fleetBuilder.setCaptain(Global.getSector().getFaction(Factions.PIRATES).createRandomPerson());
                fleetBuilder.setFleetType(FleetTypes.PERSON_BOUNTY_FLEET);
                CampaignFleetAPI fleet = fleetBuilder.create();

                fleet.getCommander().setName(new FullName("Jack", "Sparrow", FullName.Gender.MALE));
                fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PIRATE, true);
                fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_NO_MILITARY_RESPONSE, true);
                fleet.setNoFactionInName(true);

                return fleet;
            }
        });
    }
}
