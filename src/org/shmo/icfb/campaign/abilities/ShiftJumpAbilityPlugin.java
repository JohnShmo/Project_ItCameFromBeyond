package org.shmo.icfb.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.ItCameFromBeyond;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ShiftJumpAbilityPlugin extends BaseDurationAbility {
    public static final String ID = "icfb_shift_jump";

    private final ShiftJump _impl = new ShiftJump();

    public static ShiftJumpAbilityPlugin getPlayerInstance() {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet == null)
            return null;
        if (!playerFleet.hasAbility(ID))
            return null;
        return (ShiftJumpAbilityPlugin) playerFleet.getAbility(ID);
    }

    public ShiftJump getImpl() {
        return _impl;
    }

    @Override
    protected String getActivationText() {
        return "Initiating Shift Jump";
    }

    @Override
    protected void activateImpl() {
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null)
            return;
        if (fleet.isInHyperspaceTransition())
            return;
        getImpl().activate(fleet);
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }

    @Override
    protected void applyEffect(float amount, float level) {
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null)
            return;
        getImpl().advance(fleet, getActivationDays() * Global.getSector().getClock().getSecondsPerDay(), amount, level);
        if (getImpl().isState(ShiftJump.State.CANCELED)) {
            deactivate();
            resetCooldown();
        }
    }

    private void resetCooldown() {
        activeDaysLeft = 0;
        cooldownLeft = 0;
    }

    @Override
    protected void deactivateImpl() {
        cleanupImpl();
    }

    @Override
    protected void cleanupImpl() {}

    @Override
    public boolean isUsable() {
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null)
            return false;
        if (!super.isUsable())
            return false;
        return !fleet.isInHyperspaceTransition();
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null)
            return;

        final Color highlight = Misc.getHighlightColor();
        final Color fuel = Global.getSettings().getColor("progressBarFuelColor");
        final int fuelPerLY = getImpl().computeFuelCost(fleet, 1);
        final int extraFuelPercent =
                (int)((ItCameFromBeyond.Global.getSettings().shiftJump.baseExtraFuelPercent *
                    getImpl().getFuelCostMultiplier()) * 100f);
        final int crPercentShort = (int)(getImpl().computeCRCostFractional(0.25f) * 100f);
        final int crPercentMedium = (int)(getImpl().computeCRCostFractional(0.5f) * 100f);
        final int crPercentMax = (int)(getImpl().computeCRCostFractional(1.0f) * 100f);
        final float pad = 10f;

        tooltip.addTitle("Shift Jump");

        tooltip.addPara("Jump across the sector to a star system you've visited before using your %s. "
                        + "This comes at a great cost in fuel, and will impact the physical "
                        + "(and mental) well-being of your ships and crew.",
                pad, highlight, "Shift Drive"
        );

        tooltip.addPara("%s per light year traveled: %s"
                + "\nCompared to flying through hyperspace, this consumes an extra %s for the distance traveled.",
                pad, fuel,
                "Fuel cost", String.valueOf(fuelPerLY), extraFuelPercent + "%"
        );

        tooltip.addPara("The %s for using Shift Jump across short distances is roughly %s."
                + " Across medium distances it is about %s. At maximum range it is %s.",
                pad, highlight,
                "combat readiness penalty", crPercentShort + "%", crPercentMedium + "%", crPercentMax + "%"
        );
    }

    private List<FleetMemberAPI> getNonReadyShips() {
        List<FleetMemberAPI> result = new ArrayList<>();
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null) return result;

        for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
            if (Math.round(member.getRepairTracker().getCR() * 100) < 25) {
                result.add(member);
            }
        }
        return result;
    }

    @Override
    public void fleetLeftBattle(BattleAPI battle, boolean engagedInHostilities) {
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null)
            return;
        if (engagedInHostilities) {
            getImpl().cancel(fleet);
            deactivate();
            resetCooldown();
        }
    }

    private boolean showAlarm() {
        return !getNonReadyShips().isEmpty() && !isOnCooldown() && !isActiveOrInProgress() && isUsable();
    }

    @Override
    public Color getCooldownColor() {
        if (showAlarm()) {
            Color color = Misc.getNegativeHighlightColor();
            return Misc.scaleAlpha(color, Global.getSector().getCampaignUI().getSharedFader().getBrightness() * 0.5f);
        }
        return super.getCooldownColor();
    }

    @Override
    public String getOffSoundUI() {
        if (getImpl().isState(ShiftJump.State.CANCELED))
            return null;
        return super.getOffSoundUI();
    }

    @Override
    public String getOffSoundWorld() {
        if (getImpl().isState(ShiftJump.State.CANCELED))
            return null;
        return super.getOffSoundWorld();
    }
}
