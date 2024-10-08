package org.shmo.icfb.campaign.abilities;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.RepairTrackerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.shmo.icfb.IcfbGlobal;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.intel.ShiftJumpDamageIntel;
import org.shmo.icfb.campaign.scripts.IcfbShiftDriveManager;
import org.shmo.icfb.utilities.ShmoMath;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShiftJump {
    // CONSTANTS =======================================================================================================

    public static final float CR_USE_VARIANCE = 0.1f;
    public static final float CHANCE_FOR_DAMAGE_AT_0_CR = 0.5f;
    public static final float CHANCE_FOR_DISABLE_AT_0_CR = 0.1f;

    public static final String PRIME_PING_ID = "icfb_shift_jump_prime";
    public static final String ACTIVATE_PING_ID = "icfb_shift_jump_activate";

    public enum State {
        INACTIVE,
        CHOOSING_DESTINATION,
        PRIMED,
        FINISHED,
        CANCELED,
    }

    // FIELDS ==========================================================================================================

    private State _state = State.INACTIVE;
    private float _fuelCostMultiplier = 1.0f;
    private float _maxRangeMultiplier = 1.0f;
    private float _crUseMultiplier = 1.0f;
    private int _fuelToRefund = 0;
    private SectorEntityToken _targetStar = null;
    private EveryFrameScript _primedPing = null;

    // UTILITIES =======================================================================================================

    private static SectorEntityToken createDestinationToken(SectorEntityToken star) {
        // Don't appear within a star's corona effect!
        final PlanetAPI starPlanet = (PlanetAPI) star;
        final float distance = 2f * (star.getRadius()
                + starPlanet.getSpec().getCoronaSize())
                + IcfbGlobal.getSettings().shiftJump.arrivalDistanceFromDestination;
        final Vector2f offset = MathUtils.getRandomPointOnCircumference(null, distance);
        return starPlanet.getStarSystem().createToken(offset.x, offset.y);
    }

    private static void cleanupDestinationToken(SectorEntityToken token) {
        token.setExpired(true);
    }

    private static void applySlowdown(float activateSeconds, float amount, CampaignFleetAPI fleet) {
        float speed = fleet.getVelocity().length();
        float acc = java.lang.Math.max(speed, 200f)/activateSeconds + fleet.getAcceleration();
        float ds = acc * amount;
        if (ds > speed) ds = speed;
        Vector2f dv = Misc.getUnitVectorAtDegreeAngle(Misc.getAngleInDegrees(fleet.getVelocity()));
        dv.scale(ds);
        fleet.setVelocity(fleet.getVelocity().x - dv.x, fleet.getVelocity().y - dv.y);
    }

    private static void doJump(CampaignFleetAPI fleet, SectorEntityToken destination) {
        JumpPointAPI.JumpDestination dest = new JumpPointAPI.JumpDestination(destination, null);
        Global.getSector().doHyperspaceTransition(fleet, fleet, dest);
        fleet.setNoEngaging(2.0f);
        fleet.clearAssignments();
    }

    private static void spawnJumpPing(CampaignFleetAPI fleet) {
        Global.getSector().addPing(fleet, ACTIVATE_PING_ID);
    }

    private void showDestinationPicker() {
        ShiftJumpDestinationPicker.execute(this);
    }

    private void spawnPrimedPing(CampaignFleetAPI fleet) {
        despawnPrimedPing();
        _primedPing = Global.getSector().addPing(fleet, PRIME_PING_ID);
    }

    private void despawnPrimedPing() {
        if (_primedPing != null)
            Global.getSector().removeScript(_primedPing);
        _primedPing = null;
    }

    public int computeFuelCost(CampaignFleetAPI fleet, float distanceInLY) {
        final float normalCostPerLY = fleet.getLogistics().getFuelCostPerLightYear();
        final float costPerLY =
                normalCostPerLY * (1f +
                        (IcfbGlobal.getSettings().shiftJump.baseExtraFuelPercent *
                                getFuelCostMultiplier()));
        return (int)(costPerLY * distanceInLY);
    }

    public int computeFuelCost(CampaignFleetAPI fleet, SectorEntityToken target) {
        final float distance = Misc.getDistanceLY(fleet, target);
        return computeFuelCost(fleet, distance);
    }

    private void spendFuel(CampaignFleetAPI fleet, SectorEntityToken target) {
        final int cost = computeFuelCost(fleet, target);
        IcfbLog.info("Shift Jump: Consumed { " + cost + " } fuel.");
        fleet.getCargo().removeFuel(cost);
        setFuelToRefund(cost);
    }

    private void refundFuel(CampaignFleetAPI fleet) {
        fleet.getCargo().addFuel(getFuelToRefund());
        IcfbLog.info("Shift Jump: Refunded { " + getFuelToRefund() + " } fuel.");
        resetFuelToRefund();
    }

    public int getMaxRangeLY() {
        return (int)(IcfbGlobal.getSettings().shiftJump.baseMaxRangeLY * getMaxRangeMultiplier());
    }

    public float computeCRCostFractional(float t) {
        return ShmoMath.lerp(0,
                IcfbGlobal.getSettings().shiftJump.crPenaltyAtMaxRange * getCRUseMultiplier(),
                IcfbMisc.computeShiftJumpCRPenalty(
                        IcfbGlobal.getSettings().shiftJump.crPenaltyCurve, t
                )
        );
    }

    public float computeCRCost(float distanceInLY) {
        return computeCRCostFractional(distanceInLY / getMaxRangeLY());
    }

    public float computeCRCost(CampaignFleetAPI fleet, SectorEntityToken target) {
        final float distance = Misc.getDistanceLY(fleet, target);
        return computeCRCost(distance);
    }

    private void applyCRCost(CampaignFleetAPI fleet, SectorEntityToken target) {
        float crCost = computeCRCost(fleet, target);
        if (crCost < 0.01)
            return;

        Random rng = new Random();
        crCost += crCost * (((rng.nextFloat() - 0.5f) * 2f) * CR_USE_VARIANCE);
        final List<FleetMemberAPI> fleetMembers = fleet.getMembersWithFightersCopy();

        for (final FleetMemberAPI member : fleetMembers) {
            if (member.isFighterWing())
                continue;
            final RepairTrackerAPI repairTracker = member.getRepairTracker();
            final float crAfterUse = java.lang.Math.max(repairTracker.getCR() - crCost, 0);
            String eventMessage = "Combat readiness reduced after Shift Jump.";
            if (crAfterUse == 0) {
                if (tryApplyDamage(member, rng)) {
                    eventMessage = member.getShipName() + " was damaged due to complications during Shift Jump.";
                    Global.getSector().getIntelManager().addIntel(new ShiftJumpDamageIntel(eventMessage));
                } else if (tryDisable(member, rng)) {
                    eventMessage = member.getShipName() + " was destroyed due to complications during Shift Jump.";
                    Global.getSector().getIntelManager().addIntel(new ShiftJumpDamageIntel(eventMessage));
                }
            }
            repairTracker.applyCREvent(-crCost, eventMessage);
        }
    }

    private boolean tryApplyDamage(FleetMemberAPI fleetMember, Random rng) {
        final float rngResult = rng.nextFloat();
        if (rngResult <= CHANCE_FOR_DAMAGE_AT_0_CR) {
            final float damageTaken = rng.nextFloat();
            fleetMember.getStatus().applyDamage(fleetMember.getHullSpec().getHitpoints() * damageTaken);
            return true;
        }
        return false;
    }

    private boolean tryDisable(FleetMemberAPI fleetMember, Random rng) {
        final float rngResult = rng.nextFloat();
        if (rngResult <= CHANCE_FOR_DISABLE_AT_0_CR) {
            fleetMember.getStatus().disable();
            return true;
        }
        return false;
    }

    private int getFuelToRefund() {
        return _fuelToRefund;
    }

    private void setFuelToRefund(int amount) {
        _fuelToRefund = amount;
    }

    private void resetFuelToRefund() {
        _fuelToRefund = 0;
    }

    public List<SectorEntityToken> getValidDestinationList(CampaignFleetAPI fleet) {
        final List<StarSystemAPI> starSystems = Global.getSector().getStarSystems();
        final List<SectorEntityToken> validStars = new ArrayList<>();
        for (final StarSystemAPI starSystem : starSystems) {
            if (!starSystem.isEnteredByPlayer())
                continue;
            if (starSystem == fleet.getContainingLocation())
                continue;
            SectorEntityToken star = starSystem.getStar();
            if (star == null)
                continue;
            final float distance = Misc.getDistanceLY(fleet, star);
            if (distance > (float)getMaxRangeLY())
                continue;
            validStars.add(star);
        }
        return validStars;
    }

    // STATE MACHINE ===================================================================================================

    public void activate(CampaignFleetAPI fleet) {
        IcfbLog.info("Shift Jump: Activating...");
        setState(State.CHOOSING_DESTINATION, fleet);
    }

    public void cancel(CampaignFleetAPI fleet) {
        setState(State.CANCELED, fleet);
    }

    public void advance(CampaignFleetAPI fleet, float activateSeconds, float amount, float level) {
        switch (getState()) {
            case INACTIVE: return;
            case CHOOSING_DESTINATION:
                doChoosingDestinationState(fleet);
                break;
            case PRIMED:
                doPrimedState(fleet, activateSeconds, amount, level);
                break;
            case FINISHED:
                doFinishedState(fleet);
                break;
            case CANCELED:
                doCanceledState(fleet);
                break;
        }
    }

    private void setState(State state, CampaignFleetAPI fleet) {
        switch (state) {
            case INACTIVE: break;
            case CHOOSING_DESTINATION:
                gotToChoosingDestinationState(fleet);
                break;
            case PRIMED:
                goToPrimedState(fleet);
                break;
            case FINISHED:
                goToFinishedState(fleet);
                break;
            case CANCELED:
                goToCanceledState(fleet);
                break;
        }
        _state = state;
    }

    private void gotToChoosingDestinationState(CampaignFleetAPI fleet) {
        IcfbLog.info("Shift Jump: Choosing destination...");

        if (fleet.isPlayerFleet())
            showDestinationPicker();
    }

    private void goToPrimedState(CampaignFleetAPI fleet) {
        IcfbLog.info("Shift Jump: Primed and charging...");

        spawnPrimedPing(fleet);
        if (fleet.isPlayerFleet())
            spendFuel(fleet, getTarget());
    }

    private void goToFinishedState(CampaignFleetAPI fleet) {
        IcfbLog.info("Shift Jump: Activation complete!");
        IcfbLog.info("Shift Jump: Jumped to { " + getTarget().getName() + " }!");

        applyCRCost(fleet, getTarget());
        resetFuelToRefund();

        final float distance = Misc.getDistanceLY(fleet, getTarget());
        SectorEntityToken destination = createDestinationToken(getTarget());
        doJump(fleet, destination);
        cleanupDestinationToken(destination);

        despawnPrimedPing();
        spawnJumpPing(fleet);
        resetTarget();

        if (fleet.isPlayerFleet()) {
            IcfbShiftDriveManager.getInstance().notifyShiftJumpUsed(fleet, distance);
        }
    }

    private void goToCanceledState(CampaignFleetAPI fleet) {
        IcfbLog.info("Shift Jump: Canceled.");

        if (isState(State.PRIMED)) {
            refundFuel(fleet);
        }
        resetTarget();
    }

    private void doChoosingDestinationState(CampaignFleetAPI fleet) {
        CampaignUIAPI ui = Global.getSector().getCampaignUI();
        if (hasTarget()) {
            setState(State.PRIMED, fleet);
        } else if (ui.getCurrentInteractionDialog() == null) {
            setState(State.CANCELED, fleet);
        }
    }

    private void doPrimedState(CampaignFleetAPI fleet, float activateSeconds, float amount, float level) {
        if (level > 0 && level < 1 && amount > 0) {
            applySlowdown(activateSeconds, amount, fleet);
            return;
        }
        if (level == 1) {
            setState(State.FINISHED, fleet);
        }
    }

    private void doFinishedState(CampaignFleetAPI fleet) {
        setState(State.INACTIVE, fleet);
    }

    private void doCanceledState(CampaignFleetAPI fleet) {
        setState(State.INACTIVE, fleet);
    }

    // VARIOUS GETTERS AND SETTERS =====================================================================================

    public State getState() {
        return _state;
    }

    public boolean isState(State state) {
        return this._state == state;
    }

    public void setTarget(SectorEntityToken targetStar) {
        this._targetStar = targetStar;
    }

    public SectorEntityToken getTarget() {
        return _targetStar;
    }

    public void resetTarget() {
        _targetStar = null;
    }

    public boolean hasTarget() {
        return _targetStar != null;
    }

    public float getFuelCostMultiplier() {
        return _fuelCostMultiplier;
    }

    public void setFuelCostMultiplier(float multiplier) {
        _fuelCostMultiplier = multiplier;
    }

    public float getMaxRangeMultiplier() {
        return _maxRangeMultiplier;
    }

    public void setMaxRangeMultiplier(float multiplier) {
        _maxRangeMultiplier = multiplier;
    }

    public float getCRUseMultiplier() {
        return _crUseMultiplier;
    }

    public void setCRUseMultiplier(float multiplier) {
        _crUseMultiplier = multiplier;
    }
}
