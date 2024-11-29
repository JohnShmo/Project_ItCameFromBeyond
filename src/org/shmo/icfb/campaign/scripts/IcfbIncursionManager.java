package org.shmo.icfb.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.jetbrains.annotations.Nullable;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.listeners.ShiftDriveListener;
import org.shmo.icfb.factories.ScriptFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IcfbIncursionManager extends BaseCampaignEventListener implements EveryFrameScript, ShiftDriveListener {
    public static final String KEY = "$IcfbIncursionManager";
    private static final String POINTS_KEY = "$points";
    private static final String TARGET_SYSTEM_KEY = "$targetSystem";
    private static final String INCURSION_ACTIVE_KEY = "$incursionActive";
    private static final String ONE_TIME_FACTORS_KEY = "$oneTimeFactors";
    private static final String MONTHLY_FACTORS_KEY = "$monthlyFactors";
    private static final String ACTIVATED_KEY = "$activated";
    private static final String NERFED_KEY = "$nerfed";
    private static final String INCURSION_TIMESTAMP_KEY = "$incursionTimestamp";
    private static final String ONE_TIME_TIMESTAMP_KEY = "$oneTimeTimestamp";
    private static final String SEEN_FIRST_INCURSION_KEY = "$seenFirstIncursion";

    public static final float DURATION_OF_INCURSIONS = 60f;
    public static final float DURATION_OF_ONE_TIME_FACTORS = 5f;
    public static final int MAX_POINTS = 600;

    public interface FactorTooltipMaker {
        void addTooltipDesc(Factor factor, TooltipMakerAPI dialog);
    }

    public enum Factor {
        MONTHLY_BUILDUP(
                "monthly_buildup",
                "Shifter Activity",
                50,
                false,
                new FactorTooltipMaker() {
                    @Override
                    public void addTooltipDesc(Factor factor, TooltipMakerAPI dialog) {

                    }
                }
        ),
        MONTHLY_BUILDUP_NERFED(
                "monthly_buildup_nerfed",
                "Shifter Activity",
                15,
                false,
                new FactorTooltipMaker() {
                    @Override
                    public void addTooltipDesc(Factor factor, TooltipMakerAPI dialog) {

                    }
                }
        ),
        SHIFT_JUMP_USE(
                "shift_jump_use",
                "Shift Jump Used",
                90,
                true,
                new FactorTooltipMaker() {
                    @Override
                    public void addTooltipDesc(Factor factor, TooltipMakerAPI dialog) {

                    }
                }
        ),
        SHIFT_JUMP_USE_NERFED(
                "shift_jump_use_nerfed",
                "Shift Jump Used",
                40,
                true,
                new FactorTooltipMaker() {
                    @Override
                    public void addTooltipDesc(Factor factor, TooltipMakerAPI dialog) {

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
                    public void addTooltipDesc(Factor factor, TooltipMakerAPI dialog) {

                    }
                }
        )
        ;

        public final String id;
        public final String name;
        public final int points;
        public final boolean oneTime;
        public final FactorTooltipMaker tooltipMaker;

        Factor(String id, String name, int points, boolean oneTime, FactorTooltipMaker tooltipMaker) {
            this.id = id;
            this.name = name;
            this.points = points;
            this.oneTime = oneTime;
            this.tooltipMaker = tooltipMaker;
        }

        public String getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public int getPoints() {
            return points;
        }
        public boolean isOneTime() {
            return oneTime;
        }
        public boolean hasTooltipDesc() {
            return tooltipMaker != null;
        }
        public void addTooltipDesc(TooltipMakerAPI dialog) {
            if (hasTooltipDesc())
                tooltipMaker.addTooltipDesc(this, dialog);
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

    private MemoryAPI getMemory() {
        if (_memory == null)
            _memory = Global.getFactory().createMemory();
        return _memory;
    }

    public int getCurrentPoints() {
        return getMemory().getInt(POINTS_KEY);
    }

    public void setCurrentPoints(int points) {
        getMemory().set(POINTS_KEY, Math.min(Math.max(0, points), MAX_POINTS));
    }

    public void addOrRemovePoints(int points) {
        setCurrentPoints(getCurrentPoints() + points);
    }

    public StarSystemAPI getTargetSystem() {
        return (StarSystemAPI)getMemory().get(TARGET_SYSTEM_KEY);
    }

    private void setTargetSystem(StarSystemAPI system) {
        getMemory().set(TARGET_SYSTEM_KEY, system);
    }

    private void unsetTargetStarSystem() {
        getMemory().unset(TARGET_SYSTEM_KEY);
    }

    public boolean isNerfed() {
        return getMemory().getBoolean(NERFED_KEY);
    }

    public void setNerfed(boolean nerfed) {
        getMemory().set(NERFED_KEY, nerfed);
    }

    public boolean hasSeenFirstIncursion() {
        return getMemory().getBoolean(SEEN_FIRST_INCURSION_KEY);
    }

    public void setSeenFirstIncursion(boolean seenFirstIncursion) {
        getMemory().set(SEEN_FIRST_INCURSION_KEY, seenFirstIncursion);
    }

    public boolean isActivated() {
        return getMemory().getBoolean(ACTIVATED_KEY);
    }

    public void setActivated(boolean activated) {
        getMemory().set(ACTIVATED_KEY, activated);
    }

    private long getIncursionTimestamp() {
        return getMemory().getLong(INCURSION_TIMESTAMP_KEY);
    }

    private void setIncursionTimestamp(long timestamp) {
        getMemory().set(INCURSION_TIMESTAMP_KEY, timestamp);
    }

    private void updateIncursionTimestamp() {
        setIncursionTimestamp(Global.getSector().getClock().getTimestamp());
    }

    private boolean isIncursionTimestampExpired() {
        return Global.getSector().getClock().getElapsedDaysSince(getIncursionTimestamp()) >= DURATION_OF_INCURSIONS;
    }

    private long getOneTimeTimestamp() {
        return getMemory().getLong(ONE_TIME_TIMESTAMP_KEY);
    }

    private void setOneTimeTimestamp(long timestamp) {
        getMemory().set(ONE_TIME_TIMESTAMP_KEY, timestamp);
    }

    private void updateOneTimeTimestamp() {
        setOneTimeTimestamp(Global.getSector().getClock().getTimestamp());
    }

    private boolean isOneTimeTimestampExpired() {
        return Global.getSector().getClock().getElapsedDaysSince(getOneTimeTimestamp()) >= DURATION_OF_ONE_TIME_FACTORS;
    }

    @SuppressWarnings("unchecked")
    private List<Factor> getOneTimeFactors() {
        if (getMemory().get(ONE_TIME_FACTORS_KEY) == null)
            getMemory().set(ONE_TIME_FACTORS_KEY, new ArrayList<Factor>());
        return (List<Factor>)getMemory().get(ONE_TIME_FACTORS_KEY);
    }

    public List<Factor> getOneTimeFactorsCopy() {
        return new ArrayList<>(getOneTimeFactors());
    }

    @SuppressWarnings("unchecked")
    private Set<Factor> getMonthlyFactors() {
        if (getMemory().get(MONTHLY_FACTORS_KEY) == null)
            getMemory().set(MONTHLY_FACTORS_KEY, new HashSet<Factor>());
        return (Set<Factor>)getMemory().get(MONTHLY_FACTORS_KEY);
    }

    public Set<Factor> getMonthlyFactorsCopy() {
        return new HashSet<>(getMonthlyFactors());
    }

    public boolean hasMonthlyFactor(Factor factor) {
        if (factor == null)
            return false;

        return getMonthlyFactors().contains(factor);
    }

    private void addFactor(Factor factor) {
        if (factor == null)
            return;

        if (factor.isOneTime()) {
            if (getOneTimeFactors().isEmpty())
                updateOneTimeTimestamp();
            getOneTimeFactors().add(factor);
            addOrRemovePoints(factor.getPoints());
            IcfbLog.info("Added one time factor for Incursions: { " + factor.name + " } with { " + factor.points + " } points");
        } else if (!hasMonthlyFactor(factor)) {
            getMonthlyFactors().add(factor);
            IcfbLog.info("Added monthly factor for Incursions: { " + factor.name + " } with { " + factor.points + " } points");
        }
    }

    private void removeMonthlyFactor(Factor factor) {
        if (factor == null)
            return;

        getMonthlyFactors().remove(factor);
    }

    public boolean isIncursionActive() {
        return getMemory().getBoolean(INCURSION_ACTIVE_KEY);
    }

    private void setIncursionActive(boolean incursionActive) {
        if (incursionActive)
            getMemory().set(INCURSION_ACTIVE_KEY, true);
        else
            getMemory().unset(INCURSION_ACTIVE_KEY);
    }

    private void startIncursion(StarSystemAPI system) {
        if (system == null)
            return;
        endIncursion();
        setTargetSystem(system);
        setIncursionActive(true);
        setIncursionTimestamp(Global.getSector().getClock().getTimestamp());
        IcfbLog.info("Incursion started in { " + system.getName() + " }");
    }

    private void endIncursion() {
        if (!isIncursionActive())
            return;
        unsetTargetStarSystem();
        setIncursionActive(false);
        IcfbLog.info("Incursion ended");
    }

    private void ensureSubscribed() {
        if (!_subscribed) {
            IcfbShiftDriveManager.getInstance().addListener(this);
            Global.getSector().addListener(this);
            _subscribed = true;
        }
    }

    private void monthlyUpdate() {
        int totalPoints = 0;
        for (Factor factor : getMonthlyFactors()) {
            totalPoints += factor.getPoints();
            addOrRemovePoints(factor.getPoints());
        }
        getMonthlyFactors().clear();
        if (totalPoints > 0) {
            IcfbLog.info("Monthly factors for Incursions added { " + totalPoints + " } points in total");
        }

        if (isActivated()) {
            if (!isNerfed())
                addFactor(Factor.MONTHLY_BUILDUP);
            else
                addFactor(Factor.MONTHLY_BUILDUP_NERFED);
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
    public void advance(float v) {
        ensureSubscribed();
        updateIncursions();
        updateMonthlyBuildup();
        updateOneTimeFactors();
    }

    public void updateIncursions() {
        if (isIncursionActive() && isIncursionTimestampExpired()) {
            endIncursion();
        } else if (!isIncursionActive() && getCurrentPoints() >= MAX_POINTS) {
            prepareIncursion();
        }
    }

    private void prepareIncursion() {
        final int pointsToRemove = (MAX_POINTS / 2) + Misc.random.nextInt(MAX_POINTS / 2);
        final StarSystemAPI system;
        if (hasSeenFirstIncursion()) {
            system = IcfbMisc.pickSystem(Global.getSector().getEconomy().getStarSystemsWithMarkets());
        } else {
            system = getPlayerFleetSystemIfItHasMarkets();
        }
        if (system == null)
            return;

        addOrRemovePoints(-pointsToRemove);
        IcfbLog.info("Preparing Incursion, and removed { " + pointsToRemove + " } points");
        startIncursion(system);
        updateIncursionTimestamp();
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
        if (isNerfed() && hasMonthlyFactor(Factor.MONTHLY_BUILDUP)) {
            removeMonthlyFactor(Factor.MONTHLY_BUILDUP);
            addFactor(Factor.MONTHLY_BUILDUP_NERFED);
        }

        if (!isActivated()) {
            if (hasMonthlyFactor(Factor.MONTHLY_BUILDUP))
                removeMonthlyFactor(Factor.MONTHLY_BUILDUP);
            if (hasMonthlyFactor(Factor.MONTHLY_BUILDUP_NERFED))
                removeMonthlyFactor(Factor.MONTHLY_BUILDUP_NERFED);
        }
    }

    private void updateOneTimeFactors() {
        final List<Factor> oneTimeFactors = getOneTimeFactors();
        if (!oneTimeFactors.isEmpty() && isOneTimeTimestampExpired()) {
            oneTimeFactors.remove(oneTimeFactors.size() - 1);
            updateOneTimeTimestamp();
        }
    }

    @Override
    public void notifyShiftJumpUsed(CampaignFleetAPI fleet, float distanceLY) {
        if (!fleet.isPlayerFleet())
            return;
        if (!isActivated() && getCurrentPoints() >= Factor.SHIFT_JUMP_USE.points) {
            setActivated(true);
            addFactor(Factor.MONTHLY_BUILDUP);
        }
        if (!isNerfed())
            addFactor(Factor.SHIFT_JUMP_USE);
        else
            addFactor(Factor.SHIFT_JUMP_USE_NERFED);
    }

    @Override
    public void reportEconomyMonthEnd() {
        monthlyUpdate();
    }
}
