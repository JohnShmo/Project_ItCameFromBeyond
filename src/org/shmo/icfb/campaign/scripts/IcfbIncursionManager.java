package org.shmo.icfb.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.jetbrains.annotations.Nullable;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.events.Incursion;
import org.shmo.icfb.campaign.intel.majorevents.IncursionEventIntel;
import org.shmo.icfb.campaign.listeners.ShiftDriveListener;
import org.shmo.icfb.factories.ScriptFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class IcfbIncursionManager extends BaseCampaignEventListener implements EveryFrameScript, ShiftDriveListener {
    public static final String KEY = "$IcfbIncursionManager";
    public static final String POINTS_KEY = "$points";
    public static final String TARGET_SYSTEM_KEY = "$targetSystem";
    public static final String INCURSION_ACTIVE_KEY = "$incursionActive";
    public static final String ONE_TIME_FACTORS_KEY = "$oneTimeFactorInstances";
    public static final String MONTHLY_FACTORS_KEY = "$monthlyFactorInstances";
    public static final String ACTIVATED_KEY = "$activated";
    public static final String NERFED_KEY = "$nerfed";
    public static final String INCURSION_TIMESTAMP_KEY = "$incursionTimestamp";
    public static final String ONE_TIME_TIMESTAMP_KEY = "$oneTimeTimestamp";
    public static final String SEEN_FIRST_INCURSION_KEY = "$seenFirstIncursion";
    public static final String INCURSION_KEY = "$incursion";
    public static final String COUNTDOWN_TO_FIRST_INCURSION_KEY = "$countdownToFirstIncursion";
    public static final String EVENT_INTEL_KEY = "$eventIntel";

    public static final float DURATION_OF_INCURSIONS = 60f;
    public static final float DURATION_OF_ONE_TIME_FACTORS = 5f;
    public static final float TIME_BEFORE_FIRST_INCURSION = 20f;
    public static final int MAX_POINTS = 500;

    public interface FactorTooltipMaker {
        void addTooltipDesc(FactorInstance factorInstance, TooltipMakerAPI dialog);
    }

    public enum Factor {
        MONTHLY_BUILDUP(
                "monthly_buildup",
                "Shifter Activity",
                40,
                false,
                new FactorTooltipMaker() {
                    @Override
                    public void addTooltipDesc(FactorInstance instance, TooltipMakerAPI dialog) {
                        final Color sh = Global.getSettings().getDesignTypeColor("Shifter");
                        final Color hl = Misc.getHighlightColor();

                        dialog.addPara(
                                "Unchecked %s activity eventually culminates into a larger incursion of civilized space. " +
                                        "Currently this factor accounts for %s points per month, and this increases with the overall " +
                                        "event progress.",
                                0,
                                new Color[] { sh, hl },
                                "Shifter", Misc.getWithDGS(instance.getPoints())
                        );

                        if (!getInstance().isNerfed())
                            dialog.addPara(
                                    "Hostile %s fleets appear more commonly as event progress increases. " +
                                            "Their strength seems to be bolstered by some inexplicable outside force, " +
                                            "and they hunt you relentlessly.",
                                    10,
                                    sh,
                                    "Shifter"
                            );
                        else
                            dialog.addPara(
                                    "Hostile %s fleets appear more commonly as event progress increases. " +
                                            "Due to your actions, their strength pales in comparison to the " +
                                            "threat they posed before.",
                                    10,
                                    sh,
                                    "Shifter"
                            );

                        if (getInstance().isIncursionActive())
                            dialog.addPara(
                                    "A currently active incursion represents a large contribution to overall %s activity. " +
                                            "Without being dealt with, the current incursion may rapidly result in another one elsewhere.",
                                    10,
                                    sh,
                                    "Shifter"
                            );
                    }
                }
        ),
        SHIFT_JUMP_USE(
                "shift_jump_use",
                "Shift Jump Used",
                75,
                true,
                new FactorTooltipMaker() {
                    @Override
                    public void addTooltipDesc(FactorInstance instance, TooltipMakerAPI dialog) {
                        final Color sh = Global.getSettings().getDesignTypeColor("Shifter");
                        final Color hl = Misc.getHighlightColor();

                        dialog.addPara(
                                "Recent use of your %s has stirred %s activity significantly. " +
                                        "They seem attracted to it like moths to a flame. Take care, " +
                                        "as it's not unlikely one of their fleets will intercept you right after " +
                                        "you make a jump.",
                                0,
                                new Color[]{ hl, sh },
                                "Shift Drive",
                                "Shifter"
                        );
                    }
                }
        ),
        DEFEAT_SHIFTER_FLEET(
                "defeat_shifter_fleet",
                "Defeated Shifters",
                -20,
                true,
                new FactorTooltipMaker() {
                    @Override
                    public void addTooltipDesc(FactorInstance instance, TooltipMakerAPI dialog) {
                        final Color sh = Global.getSettings().getDesignTypeColor("Shifter");
                        final Color hl = Misc.getHighlightColor();

                        // TODO: Tooltip
                    }
                }
        )
        ;

        public final String id;
        public final String name;
        public final int defaultPoints;
        public final boolean oneTime;
        public final FactorTooltipMaker tooltipMaker;

        Factor(String id, String name, int defaultPoints, boolean oneTime, FactorTooltipMaker tooltipMaker) {
            this.id = id;
            this.name = name;
            this.defaultPoints = defaultPoints;
            this.oneTime = oneTime;
            this.tooltipMaker = tooltipMaker;
        }

        public String getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public int getDefaultPoints() {
            return defaultPoints;
        }
        public boolean isOneTime() {
            return oneTime;
        }
        public boolean hasTooltipDesc() {
            return tooltipMaker != null;
        }
        public void addTooltipDesc(FactorInstance instance, TooltipMakerAPI dialog) {
            if (hasTooltipDesc())
                tooltipMaker.addTooltipDesc(instance, dialog);
        }
    }

    public static class FactorInstance {
        public final Factor factor;
        public int points;
        public Object params;
        public boolean sentIntel;
        public long timeStamp;
        public FactorInstance(Factor factor, int points, Object params) {
            this.factor = factor;
            this.points = points;
            this.params = params;
            this.sentIntel = false;
            this.timeStamp = Global.getSector().getClock().getTimestamp();
        }
        public FactorInstance(Factor factor, int points) {
            this.factor = factor;
            this.points = points;
            this.params = null;
            this.sentIntel = false;
            this.timeStamp = Global.getSector().getClock().getTimestamp();
        }
        public FactorInstance(Factor factor, Object params) {
            this.factor = factor;
            this.points = factor.getDefaultPoints();
            this.params = params;
            this.sentIntel = false;
            this.timeStamp = Global.getSector().getClock().getTimestamp();
        }
        public FactorInstance(Factor factor) {
            this.factor = factor;
            this.points = factor.getDefaultPoints();
            this.params = null;
            this.sentIntel = false;
            this.timeStamp = Global.getSector().getClock().getTimestamp();
        }

        public boolean hasTooltipDesc() {
            return factor != null && factor.hasTooltipDesc();
        }

        public void addTooltipDesc(TooltipMakerAPI dialog) {
            if (factor != null)
                factor.addTooltipDesc(this, dialog);
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }

        public Object getParams() {
            return params;
        }

        public void setParams(Object params) {
            this.params = params;
        }

        public Factor getFactor() {
            return factor;
        }

        public boolean getSentIntel() {
            return sentIntel;
        }

        public void setSentIntel(boolean hasIntel) {
            this.sentIntel = hasIntel;
        }

        public long getTimeStamp() {
            return timeStamp;
        }
    }

    public static class Factory implements ScriptFactory {
        @Override
        public EveryFrameScript createOrGetInstance() {
            EveryFrameScript script = IcfbIncursionManager.getInstance();
            if (script == null)
                script = new IcfbIncursionManager();
            return script;
        }
    }

    public static IcfbIncursionManager getInstance() {
        return (IcfbIncursionManager) Global.getSector().getMemoryWithoutUpdate().get(KEY);
    }

    private MemoryAPI _memory;
    private boolean _subscribed = false;

    public IcfbIncursionManager() {
        super(false);
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
    }

    public MemoryAPI getMemoryWithoutUpdate() {
        if (_memory == null)
            _memory = Global.getFactory().createMemory();
        return _memory;
    }

    public int getCurrentPoints() {
        return getMemoryWithoutUpdate().getInt(POINTS_KEY);
    }

    public void setCurrentPoints(int points) {
        getMemoryWithoutUpdate().set(POINTS_KEY, Math.min(Math.max(0, points), MAX_POINTS));
    }

    public void addOrRemovePoints(int points) {
        setCurrentPoints(getCurrentPoints() + points);
    }

    public StarSystemAPI getTargetSystem() {
        return (StarSystemAPI) getMemoryWithoutUpdate().get(TARGET_SYSTEM_KEY);
    }

    private void setTargetSystem(StarSystemAPI system) {
        getMemoryWithoutUpdate().set(TARGET_SYSTEM_KEY, system);
    }

    private void unsetTargetStarSystem() {
        getMemoryWithoutUpdate().unset(TARGET_SYSTEM_KEY);
    }

    public boolean isNerfed() {
        return getMemoryWithoutUpdate().getBoolean(NERFED_KEY);
    }

    public void setNerfed(boolean nerfed) {
        getMemoryWithoutUpdate().set(NERFED_KEY, nerfed);
    }

    public boolean hasSeenFirstIncursion() {
        return getMemoryWithoutUpdate().getBoolean(SEEN_FIRST_INCURSION_KEY);
    }

    public void setSeenFirstIncursion(boolean seenFirstIncursion) {
        getMemoryWithoutUpdate().set(SEEN_FIRST_INCURSION_KEY, seenFirstIncursion);
    }

    public boolean isActivated() {
        return getMemoryWithoutUpdate().getBoolean(ACTIVATED_KEY);
    }

    public void setActivated(boolean activated) {
        getMemoryWithoutUpdate().set(ACTIVATED_KEY, activated);
    }

    private long getIncursionTimestamp() {
        return getMemoryWithoutUpdate().getLong(INCURSION_TIMESTAMP_KEY);
    }

    private void setIncursionTimestamp(long timestamp) {
        getMemoryWithoutUpdate().set(INCURSION_TIMESTAMP_KEY, timestamp);
    }

    private void updateIncursionTimestamp() {
        setIncursionTimestamp(Global.getSector().getClock().getTimestamp());
    }

    private boolean isIncursionTimestampExpired() {
        return Global.getSector().getClock().getElapsedDaysSince(getIncursionTimestamp()) >= DURATION_OF_INCURSIONS;
    }

    private long getOneTimeTimestamp() {
        return getMemoryWithoutUpdate().getLong(ONE_TIME_TIMESTAMP_KEY);
    }

    private void setOneTimeTimestamp(long timestamp) {
        getMemoryWithoutUpdate().set(ONE_TIME_TIMESTAMP_KEY, timestamp);
    }

    private void updateOneTimeTimestamp() {
        setOneTimeTimestamp(Global.getSector().getClock().getTimestamp());
    }

    private boolean isOneTimeTimestampExpired() {
        return Global.getSector().getClock().getElapsedDaysSince(getOneTimeTimestamp()) >= DURATION_OF_ONE_TIME_FACTORS;
    }

    private float getCountdownToFirstIncursion() {
        if (!getMemoryWithoutUpdate().contains(COUNTDOWN_TO_FIRST_INCURSION_KEY))
            resetCountdownToFirstIncursion();
        return getMemoryWithoutUpdate().getFloat(COUNTDOWN_TO_FIRST_INCURSION_KEY);
    }

    private void setCountdownToFirstIncursion(float value) {
        getMemoryWithoutUpdate().set(COUNTDOWN_TO_FIRST_INCURSION_KEY, value);
    }

    private void progressCountdownToFirstIncursion(float amount) {
        setCountdownToFirstIncursion(Math.max(getCountdownToFirstIncursion() - amount, 0f));
    }

    private void resetCountdownToFirstIncursion() {
        setCountdownToFirstIncursion(TIME_BEFORE_FIRST_INCURSION);
    }

    @Nullable
    public Incursion getCurrentIncursion() {
        if (!getMemoryWithoutUpdate().contains(INCURSION_KEY))
            return null;
        return (Incursion) getMemoryWithoutUpdate().get(INCURSION_KEY);
    }

    private void setCurrentIncursion(Incursion incursion) {
        getMemoryWithoutUpdate().set(INCURSION_KEY, incursion);
    }

    public IncursionEventIntel getEventIntel() {
        return (IncursionEventIntel)getMemoryWithoutUpdate().get(EVENT_INTEL_KEY);
    }

    public void createEventIntelIfNeeded() {
        if (getEventIntel() == null)
            getMemoryWithoutUpdate().set(EVENT_INTEL_KEY, new IncursionEventIntel());
    }

    @SuppressWarnings("unchecked")
    private List<FactorInstance> getOneTimeFactors() {
        if (getMemoryWithoutUpdate().get(ONE_TIME_FACTORS_KEY) == null)
            getMemoryWithoutUpdate().set(ONE_TIME_FACTORS_KEY, new ArrayList<FactorInstance>());
        return (List<FactorInstance>) getMemoryWithoutUpdate().get(ONE_TIME_FACTORS_KEY);
    }

    public List<FactorInstance> getOneTimeFactorsCopy() {
        return new ArrayList<>(getOneTimeFactors());
    }

    @SuppressWarnings("unchecked")
    private List<FactorInstance> getMonthlyFactors() {
        if (getMemoryWithoutUpdate().get(MONTHLY_FACTORS_KEY) == null)
            getMemoryWithoutUpdate().set(MONTHLY_FACTORS_KEY, new ArrayList<FactorInstance>());
        return (List<FactorInstance>) getMemoryWithoutUpdate().get(MONTHLY_FACTORS_KEY);
    }

    public List<FactorInstance> getMonthlyFactorsCopy() {
        return new ArrayList<>(getMonthlyFactors());
    }

    public boolean hasFactorInstance(FactorInstance instance) {
        return getMonthlyFactors().contains(instance) || getOneTimeFactors().contains(instance);
    }

    public boolean hasMonthlyFactor(Factor factor) {
        return getMonthlyFactorInstance(factor) != null;
    }

    @Nullable
    public FactorInstance getMonthlyFactorInstance(Factor factor) {
        if (factor == null)
            return null;
        for (FactorInstance instance : getMonthlyFactors()) {
            if (instance.factor.equals(factor))
                return instance;
        }
        return null;
    }

    private void addFactor(Factor factor, int points) {
        if (factor == null)
            return;
        if (factor.isOneTime()) {
            if (getOneTimeFactors().isEmpty())
                updateOneTimeTimestamp();
            FactorInstance factorInstance = new FactorInstance(factor, points);
            getOneTimeFactors().add(factorInstance);
            addOrRemovePoints(factorInstance.points);
            IcfbLog.info("Added one time factor for Incursions: { " + factor.name + " } with { " + points + " } points");
        } else {
            FactorInstance instance = getMonthlyFactorInstance(factor);
            if (instance != null) {
                instance.points = points;
                IcfbLog.info("Modified monthly factor for Incursions: { " + factor.name + " } with { " + points + " } points");
            } else {
                getMonthlyFactors().add(new FactorInstance(factor, points));
                IcfbLog.info("Added monthly factor for Incursions: { " + factor.name + " } with { " + points + " } points");
            }
        }
    }

    private void addFactor(Factor factor) {
        if (factor != null)
            addFactor(factor, factor.defaultPoints);
    }

    private void removeMonthlyFactor(Factor factor) {
        if (factor == null)
            return;
        List<FactorInstance> monthlyFactors = getMonthlyFactors();
        for (int i = monthlyFactors.size() - 1; i >= 0; i--) {
            if (monthlyFactors.get(i).factor.equals(factor)) {
                monthlyFactors.remove(i);
                break;
            }
        }
    }

    public boolean isIncursionActive() {
        return getMemoryWithoutUpdate().getBoolean(INCURSION_ACTIVE_KEY);
    }

    private void setIncursionActive(boolean incursionActive) {
        if (incursionActive)
            getMemoryWithoutUpdate().set(INCURSION_ACTIVE_KEY, true);
        else
            getMemoryWithoutUpdate().unset(INCURSION_ACTIVE_KEY);
    }

    private void startIncursion(StarSystemAPI system) {
        if (system == null)
            return;

        final Incursion incursion = new Incursion(system);
        setCurrentIncursion(incursion);
        setTargetSystem(system);
        setIncursionActive(true);
        incursion.start();

        IcfbLog.info("Incursion started in { " + system.getName() + " }");
    }

    private void endIncursion() {
        if (!isIncursionActive())
            return;
        if (getCurrentIncursion() != null)
            getCurrentIncursion().end();

        setCurrentIncursion(null);
        unsetTargetStarSystem();
        setIncursionActive(false);

        IcfbLog.info("Incursion ended");
    }

    private void monthlyUpdate() {
        int totalPoints = 0;
        for (FactorInstance factor : getMonthlyFactors()) {
            totalPoints += factor.points;
            addOrRemovePoints(factor.points);
        }
        if (totalPoints > 0) {
            IcfbLog.info("Monthly factors for Incursions added { " + totalPoints + " } points in total");
        }
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float deltaTime) {
        if (Global.getSector().isPaused())
            return;
        try {
            if (hasSeenFirstIncursion())
                createEventIntelIfNeeded();
            ensureSubscribed();
            updateIncursions(deltaTime);
            updateMonthlyBuildup();
            updateOneTimeFactors();
        } catch (Exception ignored) {}
    }

    private void ensureSubscribed() {
        if (!_subscribed) {
            IcfbShiftDriveManager.getInstance().addListener(this);
            Global.getSector().addListener(this);
            _subscribed = true;
        }
    }

    private void updateIncursions(float deltaTime) {
        Incursion incursion = getCurrentIncursion();
        if (incursion != null) {
            if (incursion.isDone())
                endIncursion();
            else
                incursion.advance(deltaTime);
        }
        if (isIncursionActive() && isIncursionTimestampExpired())
            endIncursion();
        if (!isIncursionActive() && getCurrentPoints() >= MAX_POINTS)
            prepareIncursion(deltaTime);
    }

    private void prepareIncursion(float deltaTime) {
        final int pointsToRemove = (MAX_POINTS / 2) + Misc.random.nextInt(MAX_POINTS / 2);
        final StarSystemAPI system;
        boolean needsCountdown = false;
        if (hasSeenFirstIncursion()) {
            system = IcfbMisc.pickSystem(Global.getSector().getEconomy().getStarSystemsWithMarkets());
        } else {
            system = getPlayerFleetSystemIfItHasMarkets();
            needsCountdown = true;
            if (system != null)
                progressCountdownToFirstIncursion(deltaTime);
            else
                resetCountdownToFirstIncursion();
        }
        if (system == null || (needsCountdown && getCountdownToFirstIncursion() > 0)) {
            return;
        }

        setSeenFirstIncursion(true);
        addOrRemovePoints(-pointsToRemove);
        IcfbLog.info("Preparing Incursion, and removed { " + pointsToRemove + " } points");
        updateIncursionTimestamp();
        startIncursion(system);
    }

    @Nullable
    private StarSystemAPI getPlayerFleetSystemIfItHasMarkets() {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet == null)
            return null;
        StarSystemAPI system = playerFleet.getStarSystem();
        if (system == null)
            return null;
        List<MarketAPI> markets = Misc.getMarketsInLocation(system);
        boolean valid = false;
        for (MarketAPI market : markets) {
            if (!market.isHidden() && market.isInEconomy() && !market.isInvalidMissionTarget()) {
                valid = true;
                break;
            }
        }
        if (!valid)
            return null;
        return system;
    }

    private void updateMonthlyBuildup() {
        FactorInstance instance = getMonthlyFactorInstance(Factor.MONTHLY_BUILDUP);
        if (isActivated() && instance == null) {
            addFactor(Factor.MONTHLY_BUILDUP);
            instance = getMonthlyFactorInstance(Factor.MONTHLY_BUILDUP);
        }
        if (instance != null) {
            int extraPoints = getCurrentPoints() / 20;
            Incursion incursion = getCurrentIncursion();
            extraPoints += incursion != null ? getCurrentIncursion().getPointsContributed() : 0;
            if (isNerfed()) {
                instance.points = (Factor.MONTHLY_BUILDUP.defaultPoints / 4) + extraPoints;
            } else {
                instance.points = Factor.MONTHLY_BUILDUP.defaultPoints + extraPoints;
            }
        }
    }

    private void updateOneTimeFactors() {
        final List<FactorInstance> oneTimeFactors = getOneTimeFactors();
        if (!oneTimeFactors.isEmpty() && isOneTimeTimestampExpired()) {
            oneTimeFactors.remove(0);
            updateOneTimeTimestamp();
        }
    }

    @Override
    public void notifyShiftJumpUsed(CampaignFleetAPI fleet, float distanceLY) {
        if (!fleet.isPlayerFleet())
            return;
        if (!isActivated() && getCurrentPoints() >= Factor.SHIFT_JUMP_USE.defaultPoints)
            setActivated(true);
        if (!isNerfed())
            addFactor(Factor.SHIFT_JUMP_USE);
        else
            addFactor(Factor.SHIFT_JUMP_USE, Factor.SHIFT_JUMP_USE.defaultPoints / 2);
    }

    @Override
    public void reportEconomyMonthEnd() {
        monthlyUpdate();
    }
}
