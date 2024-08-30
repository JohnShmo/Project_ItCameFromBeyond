package org.shmo.icfb.campaign.quests.impl.builders;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import org.magiclib.campaign.MagicFleetBuilder;
import org.shmo.icfb.campaign.intel.events.ShiftDriveEvent;
import org.shmo.icfb.campaign.quests.*;
import org.shmo.icfb.utilities.FleetFactory;

public class OddOccurrencesBuilder implements QuestBuilder {
    @Override
    public void build(Quest quest) {
        quest.setName(QuestName.ODD_OCCURRENCES);
        quest.addTag("Shift Drive");
        quest.addTag(Tags.INTEL_ACCEPTED);
        quest.addTag(Tags.INTEL_MISSIONS);
        quest.setIcon(Global.getSettings().getSpriteName(ShiftDriveEvent.SHIFT_JUMP_ICON_CATEGORY, ShiftDriveEvent.SHIFT_JUMP_ICON_ID));

        SectorEntityToken step1Target = Global.getSector().getStarSystem("Penelope's Star").getPlanets().get(2);

        LocationQuestStepIntel step1Intel = new LocationQuestStepIntel();
        step1Intel.setTarget(step1Target);
        step1Intel.setLocationHint("near a planet of");

        DefeatFleetQuestStepScript step1Script = new DefeatFleetQuestStepScript();
        step1Script.setSpawnLocation(step1Target);
        step1Script.setFleetFactory(new FleetFactory() {
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

                fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PIRATE, true);
                fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_NO_MILITARY_RESPONSE, true);
                fleet.setNoFactionInName(true);

                return fleet;
            }
        });

        quest.addStep(step1Intel, step1Script);

        quest.addStep(new QuestCompleteIntel(), null);
    }
}
