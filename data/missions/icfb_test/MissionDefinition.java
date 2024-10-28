package data.missions.icfb_test;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.BattleObjectives;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {

	public void defineMission(MissionDefinitionAPI api) {

		api.initFleet(FleetSide.PLAYER, "ISS", FleetGoal.ATTACK, false);
		api.initFleet(FleetSide.ENEMY, "ISS", FleetGoal.ATTACK, true);
		api.setFleetTagline(FleetSide.PLAYER, "Good guys");
		api.setFleetTagline(FleetSide.ENEMY, "Bad guys");

		api.addToFleet(FleetSide.PLAYER, "icfb_kaldur_patrol", FleetMemberType.SHIP, "Kaldur", true);
		api.addToFleet(FleetSide.PLAYER, "icfb_froskur_attack", FleetMemberType.SHIP, "Froskur", false);
		api.addToFleet(FleetSide.PLAYER, "icfb_skjoldr_balanced", FleetMemberType.SHIP, "Skjoldr", false);
		api.addToFleet(FleetSide.PLAYER, "icfb_karta_close_support", FleetMemberType.SHIP, "Karta", false);
		api.addToFleet(FleetSide.PLAYER, "icfb_hakarl_standard", FleetMemberType.SHIP, "Hakarl", false);
		api.addToFleet(FleetSide.PLAYER, "icfb_karta_pirates_custom", FleetMemberType.SHIP, "Karta (P)", false);

		api.addToFleet(FleetSide.ENEMY, "icfb_kaldur_patrol", FleetMemberType.SHIP, "Kaldur Badguy", false);
		api.addToFleet(FleetSide.ENEMY, "icfb_froskur_attack", FleetMemberType.SHIP, "Froskur Badguy", false);
		api.addToFleet(FleetSide.ENEMY, "icfb_skjoldr_balanced", FleetMemberType.SHIP, "Skjoldr Badguy", false);
		api.addToFleet(FleetSide.ENEMY, "icfb_karta_close_support", FleetMemberType.SHIP, "Karta Badguy", false);
		api.addToFleet(FleetSide.ENEMY, "icfb_hakarl_standard", FleetMemberType.SHIP, "Hakarl Badguy", false);
		api.addToFleet(FleetSide.ENEMY, "icfb_karta_pirates_custom", FleetMemberType.SHIP, "Karta (P) Badguy", false);

		api.addObjective(0, 4000, BattleObjectives.SENSOR_JAMMER);
		api.addObjective(4000, 0, BattleObjectives.COMM_RELAY);
		api.addObjective(-3000, -2000, BattleObjectives.NAV_BUOY);

		// Set up the map.
		float width = 12000f;
		float height = 12000f;
		
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		float minX = -width/2;
		float minY = -height/2;
		
		// Add an asteroid field
		api.addAsteroidField(minX, minY + height / 2, 0, 8000f,
							 20f, 70f, 100);
		
		api.addPlanet(0, 0, 50f, StarTypes.RED_GIANT, 250f, true);
		
	}

}
