package org.shmo.icfb.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.fleet.FleetAPI;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.impl.campaign.ids.Pings;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.console.Console;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.icfb.ItCameFromBeyond;

import java.awt.*;

public class ShiftDrive_AbilityPlugin extends BaseDurationAbility {
    public static final float CR_COST_MULT = 1f;
    public static final float FUEL_USE_MULT = 1f;
    public static final String PING_ID = "icfb_shift_drive";
    public static final String ACTIVATE_PING_ID = "icfb_shift_drive_activate";
    public static final String ABILITY_ID = "icfb_shift_drive";

    protected boolean primed = false;
    protected boolean soundPrimed = false;
    protected EveryFrameScript ping = null;
    protected StarSystemAPI target = null;

    public static ShiftDrive_AbilityPlugin getInstance() {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet == null)
            return null;
        if (!playerFleet.hasAbility(ABILITY_ID))
            return null;
        return (ShiftDrive_AbilityPlugin) playerFleet.getAbility(ABILITY_ID);
    }

    protected SectorEntityToken calculateDestination() {
        if (!hasTarget())
            return null;

        SectorEntityToken destination = null;
        destination = target.getStar();
        if (destination == null) {
            destination = target.createToken(0f, 0f);
        }
        else {
            // Don't appear within a star's corona effect!
            final PlanetAPI star = (PlanetAPI) destination;
            final float distance = 2f * (star.getRadius()
                    + star.getSpec().getCoronaSize()) + 200f;
            final Vector2f offset = MathUtils.getRandomPointOnCircumference(null, distance);
            destination = target.createToken(offset.x, offset.y);
        }
        return destination;
    }

    @Override
    protected String getActivationText() {
        return "Activating Shift Drive";
    }

    @Override
    protected void activateImpl() {
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null)
            return;
        if (fleet.isInHyperspaceTransition())
            return;

        ItCameFromBeyond.Log.info("Activating Shift Drive...");
        if (!hasTarget()) {
            ItCameFromBeyond.Log.info("No target for Shift Drive! Deactivating...");
            reset();
            return;
        }
        ping = Global.getSector().addPing(fleet, PING_ID);
        prime();
        primeSound();
    }

    @Override
    public void deactivate() {
        if (ping != null) {
            Global.getSector().removeScript(ping);
            ping = null;
        }
        super.deactivate();
    }

    @Override
    protected void applyEffect(float amount, float level) {
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null)
            return;

        if (level > 0 && level < 1 && amount > 0) {
            applySlowdown(amount, fleet);
            return;
        }

        if (level == 1 && isPrimed()) {
            SectorEntityToken destination = calculateDestination();
            if (destination == null || target == null) {
                ItCameFromBeyond.Log.warn("Shift Drive failed to find its target!");
                return;
            }
            doJump(fleet, destination, target.getName());
            resetTarget();
            unprime();
        }
    }

    protected void applySlowdown(float amount, CampaignFleetAPI fleet) {
        float activateSeconds = getActivationDays() * Global.getSector().getClock().getSecondsPerDay();
        float speed = fleet.getVelocity().length();
        float acc = Math.max(speed, 200f)/activateSeconds + fleet.getAcceleration();
        float ds = acc * amount;
        if (ds > speed) ds = speed;
        Vector2f dv = Misc.getUnitVectorAtDegreeAngle(Misc.getAngleInDegrees(fleet.getVelocity()));
        dv.scale(ds);
        fleet.setVelocity(fleet.getVelocity().x - dv.x, fleet.getVelocity().y - dv.y);
    }

    protected void doJump(CampaignFleetAPI fleet, SectorEntityToken destination, String destinationName) {
        JumpPointAPI.JumpDestination dest = new JumpPointAPI.JumpDestination(destination, null);
        Global.getSector().doHyperspaceTransition(fleet, fleet, dest);
        fleet.setNoEngaging(2.0f);
        fleet.clearAssignments();
        fleet.addAssignment(FleetAssignment.GO_TO_LOCATION, destination, 1f);
        Global.getSector().addPing(fleet, ACTIVATE_PING_ID);
        destination.setExpired(true);
        ItCameFromBeyond.Log.info("Shift Drive jumped to { " + destinationName + " }!");
    }

    protected void primeSound() {
        soundPrimed = true;
    }

    protected void unprimeSound() {
        soundPrimed = false;
    }

    protected boolean isSoundPrimed() {
        return soundPrimed;
    }

    protected void reset() {
        unprime();
        resetTarget();
        unprimeSound();
        deactivate();
        resetCooldown();
    }

    protected void resetCooldown() {
        activeDaysLeft = 0;
        cooldownLeft = 0;
    }

    public void setTarget(StarSystemAPI target) {
        this.target = target;
    }

    public void resetTarget() {
        target = null;
    }

    public StarSystemAPI getTarget() {
        return target;
    }

    public boolean hasTarget() {
        return target != null;
    }

    protected boolean isPrimed() {
        return primed;
    }

    protected void prime() {
        primed = true;
    }

    protected void unprime() {
        primed = false;
    }

    @Override
    protected void deactivateImpl() {
        cleanupImpl();
    }

    @Override
    protected void cleanupImpl() {

    }

    @Override
    public String getOffSoundUI() {
        if (isSoundPrimed()) {
            return super.getOffSoundUI();
        }
        return null;
    }

    @Override
    public String getOffSoundWorld() {
        if (isSoundPrimed()) {
            return super.getOffSoundWorld();
        }
        return null;
    }

    @Override
    public boolean isUsable() {
        if (!super.isUsable()) return false;
        if (getFleet() == null) return false;

        CampaignFleetAPI fleet = getFleet();

        if (fleet.isInHyperspaceTransition())
            return false;

        return true;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        CampaignFleetAPI fleet = getFleet();
        if (fleet == null)
            return;

        Color gray = Misc.getGrayColor();
        Color highlight = Misc.getHighlightColor();
        Color fuel = Global.getSettings().getColor("progressBarFuelColor");
        Color bad = Misc.getNegativeHighlightColor();

        LabelAPI title = tooltip.addTitle("Shift Drive");

        float pad = 10f;

        tooltip.addPara("-- TODO --", pad);
    }

    @Override
    public void fleetLeftBattle(BattleAPI battle, boolean engagedInHostilities) {
        if (engagedInHostilities) {
            reset();
        }
    }
}
