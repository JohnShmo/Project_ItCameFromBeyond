package org.shmo.icfb.commands;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import org.jetbrains.annotations.NotNull;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommandUtils;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;
import org.lazywizard.console.commands.Home;
import org.lazywizard.lazylib.CollectionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.campaign.ShiftDrive_AbilityPlugin;

import java.util.ArrayList;
import java.util.List;

public class ShiftDriveTarget implements BaseCommand {
    @Override
    public CommandResult runCommand(@NotNull String args, @NotNull BaseCommand.CommandContext context) {
        if (context != CommandContext.CAMPAIGN_MAP)
            return CommandResult.WRONG_CONTEXT;

        if (args.isEmpty())
        {
            List<StarSystemAPI> systems = Global.getSector().getStarSystems();
            List<String> systemNames = new ArrayList<>(systems.size());

            // Player has used SetHome command
            if (Global.getSector().getPersistentData()
                    .get(CommonStrings.DATA_HOME_ID) != null)
            {
                systemNames.add("Home");
            }

            // Add the names of every star system currently loaded
            for (StarSystemAPI system : systems)
            {
                systemNames.add(system.getName().substring(0,
                        system.getName().lastIndexOf(" Star System")));
            }

            Console.showMessage("Available destinations:\n"
                    + CollectionUtils.implode(systemNames));
            return CommandResult.SUCCESS;
        }

        if ("home".equalsIgnoreCase(args))
        {
            return (new Home().runCommand("", context));
        }

        CampaignFleetAPI player = Global.getSector().getPlayerFleet();


        if ("hyperspace".equalsIgnoreCase(args))
        {
            Console.showMessage("Hyperspace isn't a valid spot to shift to!");
            return CommandResult.ERROR;
        }
        else
        {
            StarSystemAPI system = CommandUtils.findBestSystemMatch(args);
            if (system == null)
            {
                Console.showMessage("No system found with the name '" + args + "'!");
                return CommandResult.ERROR;
            }
            ShiftDrive_AbilityPlugin shiftDrive = ItCameFromBeyond.getShiftDrivePlugin();
            if (shiftDrive == null) {
                Console.showMessage("Player fleet doesn't have the Shift Drive ability!");
                return CommandResult.ERROR;
            }
            shiftDrive.setTarget(system);
            return CommandResult.SUCCESS;
        }
    }
}
