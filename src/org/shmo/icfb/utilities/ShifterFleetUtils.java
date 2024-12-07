package org.shmo.icfb.utilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.loading.AbilitySpecAPI;
import com.fs.starfarer.api.util.Misc;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.campaign.MagicFleetBuilder;
import org.shmo.icfb.IcfbGlobal;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.IcfbFactions;
import org.shmo.icfb.campaign.abilities.ShiftJump;
import org.shmo.icfb.campaign.abilities.ShiftJumpAbilityPlugin;
import org.shmo.icfb.campaign.entities.plugins.ShifterRiftCloud;

public class ShifterFleetUtils {
    public static CampaignFleetAPI createFleet(int fleetPoints, boolean nerfed) {
        final MagicFleetBuilder fleetBuilder = new MagicFleetBuilder();
        fleetBuilder.setFleetFaction(IcfbFactions.SHIFTERS.getId());
        fleetBuilder.setFleetType(determineFleetType(fleetPoints));
        fleetBuilder.setFleetName("Shifter " + IcfbFactions.SHIFTERS.getFaction().getFleetTypeName(determineFleetType(fleetPoints)));
        fleetBuilder.setQualityOverride(determineQualityOverride(nerfed));
        fleetBuilder.setMinFP(computeFinalFleetPoints(fleetPoints, nerfed));
        fleetBuilder.setTransponderOn(false);

        return fleetBuilder.create();
    }

    public static void spawnFleet(CampaignFleetAPI fleet, LocationAPI location, float x, float y) {
        if (fleet == null)
            return;
        location.addEntity(fleet);
        fleet.setLocation(x, y);
        Misc.fadeIn(fleet, 2);
        fleet.setNoEngaging(2);
        final AbilitySpecAPI shiftJumpSpec = Global.getSettings().getAbilitySpec(ShiftJumpAbilityPlugin.ID);
        ShifterRiftCloud.create(location, x, y, fleet.getRadius(), 5);
        Global.getSector().addPing(fleet, ShiftJump.ACTIVATE_PING_ID);
        Global.getSoundPlayer().playSound(shiftJumpSpec.getWorldOff(), 1, 1, new Vector2f(x, y), new Vector2f());
        if (!location.isHyperspace()) {
            StarSystemAPI system = (StarSystemAPI)location;
            if (system.getStar() != null) {
                fleet.addAssignment(FleetAssignment.RAID_SYSTEM, system.getStar(), 100000f);
            }
        }
    }

    public static void despawnFleet(final CampaignFleetAPI fleet, SectorEntityToken location) {
        if (fleet == null || !fleet.isAlive() || fleet.isExpired() || fleet.getMemoryWithoutUpdate().getBoolean("$icfbShifterFleetDespawning"))
            return;
        fleet.getMemoryWithoutUpdate().set("$icfbShifterFleetDespawning", true);
        final AbilitySpecAPI shiftJumpSpec = Global.getSettings().getAbilitySpec(ShiftJumpAbilityPlugin.ID);

        fleet.clearAssignments();
        if (location != null) {
            fleet.addAssignment(FleetAssignment.GO_TO_LOCATION, location, 15f, new Script() {
                @Override
                public void run() {
                    fleet.addAbility(ShiftJumpAbilityPlugin.ID);
                    ShiftJumpAbilityPlugin shiftJump = (ShiftJumpAbilityPlugin) fleet.getAbility(ShiftJumpAbilityPlugin.ID);
                    if (shiftJump != null) {
                        StarSystemAPI system = IcfbMisc.pickSystem(new IcfbMisc.SystemPickerPredicate() {
                            @Override
                            public boolean isValid(StarSystemAPI starSystem) {
                                return !starSystem.isCurrentLocation() && starSystem.getStar() != null;
                            }
                        });
                        shiftJump.getImpl().setTarget(system.getStar());
                        shiftJump.activate();
                    }
                }
            });
        } else {
            fleet.addAbility(ShiftJumpAbilityPlugin.ID);
            ShiftJumpAbilityPlugin shiftJump = (ShiftJumpAbilityPlugin) fleet.getAbility(ShiftJumpAbilityPlugin.ID);
            if (shiftJump != null) {
                StarSystemAPI system = IcfbMisc.pickSystem(new IcfbMisc.SystemPickerPredicate() {
                    @Override
                    public boolean isValid(StarSystemAPI starSystem) {
                        return !starSystem.isCurrentLocation() && starSystem.getStar() != null;
                    }
                });
                shiftJump.getImpl().setTarget(system.getStar());
                shiftJump.activate();
            }
        }
        fleet.addAssignment(FleetAssignment.HOLD, null, shiftJumpSpec.getActivationDays());
        fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, fleet, 1000000f);
    }

    @NotNull
    public static String determineFleetType(int fleetPoints) {
        final String fleetType;
        if (fleetPoints < 50)
            fleetType = FleetTypes.PATROL_SMALL;
        else if (fleetPoints < 120)
            fleetType = FleetTypes.PATROL_MEDIUM;
        else
            fleetType = FleetTypes.PATROL_LARGE;
        return fleetType;
    }

    public static float determineQualityOverride(boolean nerfed) {
        return nerfed ? 0.5f : 2f;
    }

    public static int computeFinalFleetPoints(int fleetPoints, boolean nerfed) {
        return nerfed ? (int)(fleetPoints * 0.75f) : fleetPoints;
    }
}
