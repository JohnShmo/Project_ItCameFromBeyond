package org.shmo.icfb.campaign.intel.majorevents;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.impl.campaign.intel.events.EventFactor;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.IcfbGlobal;
import org.shmo.icfb.campaign.events.Incursion;
import org.shmo.icfb.campaign.scripts.IcfbIncursionManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IncursionEventIntel extends BaseEventIntel {
    public enum Stage {
        INCURSION
    }

    public static class FactorWrapper extends BaseEventFactor {
        IcfbIncursionManager.FactorInstance _instance;

        @Override
        public boolean shouldShow(BaseEventIntel intel) {
            return true;
        }

        public FactorWrapper(IcfbIncursionManager.FactorInstance instance) {
            _instance = instance;
        }

        @Override
        public int getProgress(BaseEventIntel intel) {
            return _instance.getPoints();
        }

        @Override
        public String getProgressStr(BaseEventIntel intel) {
            if (_instance.getFactor().equals(IcfbIncursionManager.Factor.MONTHLY_BUILDUP))
                return "-";
            return super.getProgressStr(intel);
        }

        @Override
        public String getDesc(BaseEventIntel intel) {
            return _instance.getFactor().getName();
        }

        @Override
        public void addExtraRows(TooltipMakerAPI info, BaseEventIntel intel) {
            final List<String> causes = new ArrayList<>();
            final List<Integer> points = new ArrayList<>();
            final IcfbIncursionManager im = IcfbIncursionManager.getInstance();

            if (_instance.getFactor().equals(IcfbIncursionManager.Factor.MONTHLY_BUILDUP)) {
                String row1 = "Overall event progress";
                int row1Points = im.getCurrentPoints() / 20;
                causes.add(row1);
                points.add(row1Points);

                String row2 = (im.isNerfed() ? "Weakened Shifter fleets" : "Shifter fleets");
                int row2Points = (int)(IcfbGlobal.getSettings().incursions.basePointsPerMonth.get()
                        * (im.isNerfed() ? 0.25f : 1.0f));
                causes.add(row2);
                points.add(row2Points);

                List<Incursion> incursions = im.getIncursionsCopy();
                if (!incursions.isEmpty()) {
                    String row3 = "Active incursion";
                    if (incursions.size() > 1)
                        row3 += "s";
                    int row3Points = 0;
                    for (Incursion incursion : incursions) {
                        row3Points += incursion.getPointsContributed();
                    }
                    row3Points = Math.min(
                            row3Points,
                            IcfbGlobal.getSettings().incursions.maxTotalIncursionContribution.get()
                    );
                    causes.add(row3);
                    points.add(row3Points);
                }
            }

            if (causes.isEmpty())
                return;
            int i = 0;
            for (String cause : causes) {
                String pointString = "-";
                Color pointColor = Misc.getGrayColor();
                if (points.size() > i && points.get(i) != null) {
                    int p = points.get(i);
                    if (p != 0) {
                        pointString = (p > 0 ? "+" : "") + Misc.getWithDGS(points.get(i));
                        pointColor = Misc.getHighlightColor();
                    }
                }
                info.addRow(Alignment.LMID, Misc.getGrayColor(), "    " + cause, Alignment.RMID, pointColor, pointString);
                i++;
            }
        }

        @Override
        public Color getProgressColor(BaseEventIntel intel) {
            if (_instance.getFactor().equals(IcfbIncursionManager.Factor.MONTHLY_BUILDUP))
                return Misc.getGrayColor();
            return super.getProgressColor(intel);
        }

        @Override
        public TooltipMakerAPI.TooltipCreator getMainRowTooltip() {
            if (_instance.hasTooltipDesc()) {
                return new BaseFactorTooltip() {
                    @Override
                    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                        _instance.getFactor().addTooltipDesc(_instance, tooltip);
                    }
                };
            }
            return super.getMainRowTooltip();
        }

        @Override
        public boolean isOneTime() {
            return _instance.getFactor().isOneTime();
        }

        @Override
        public void addBulletPointForOneTimeFactor(BaseEventIntel intel, TooltipMakerAPI info, Color tc, float initPad) {
            info.addPara(getDesc(intel), initPad);
            info.addPara("%s event progress", 0, getProgressColor(intel), getProgressStr(intel));
        }
    }

    public IncursionEventIntel() {
        super();

        setMaxProgress(IcfbIncursionManager.MAX_POINTS);
        addStage(Stage.INCURSION, IcfbIncursionManager.MAX_POINTS, false, StageIconSize.LARGE);
        Global.getSector().getIntelManager().addIntel(this);
    }

    @Override
    public String getIcon() {
        if (getProgressFraction() < 0.33f)
            return Global.getSettings().getSpriteName("icfb_events", "incursion_event_00");
        else if (getProgressFraction() < 0.66f)
            return Global.getSettings().getSpriteName("icfb_events", "incursion_event_01");
        else
            return Global.getSettings().getSpriteName("icfb_events", "incursion_event_02");
    }

    @Override
    protected String getName() {
        return "Shifter Activity";
    }

    @Override
    public int getProgress() {
        return IcfbIncursionManager.getInstance().getCurrentPoints();
    }

    @Override
    public int getMaxProgress() {
        return IcfbIncursionManager.MAX_POINTS;
    }

    @Override
    public void setProgress(int progress) {
        IcfbIncursionManager.getInstance().setCurrentPoints(progress);
    }

    @Override
    public Color getBarColor() {
        return Global.getSettings().getDesignTypeColor("Shifter");
    }

    @Override
    public float getProgressFraction() {
        float p = (float)IcfbIncursionManager.getInstance().getCurrentPoints() / (float)IcfbIncursionManager.MAX_POINTS;
        if (p < 0.0F) {
            p = 0.0F;
        }

        if (p > 1.0F) {
            p = 1.0F;
        }

        return p;
    }

    @Override
    public void reportEconomyTick(int index) {}

    @Override
    public boolean isEventProgressANegativeThingForThePlayer() {
        return true;
    }

    public void addFactor(EventFactor factor) {
        this.addFactor(factor, null);
    }

    public void addFactor(EventFactor factor, InteractionDialogAPI dialog) {
        this.factors.add(factor);
    }

    @Override
    public void removeFactor(EventFactor factor) {
        this.factors.remove(factor);
    }

    @Override
    protected void advanceImpl(float amount) {
        if (Global.getSector().isPaused())
            return;

        progress = IcfbIncursionManager.getInstance().getCurrentPoints();
        maxProgress = IcfbIncursionManager.MAX_POINTS;
        List<EventStageData> stages = getStages();
        for (EventStageData stageData : stages) {
            if (stageData.id.equals(Stage.INCURSION)) {
                if (stageData.progress != maxProgress)
                    stageData.progress = maxProgress;
            }
        }

        getFactors().clear();
        List<IcfbIncursionManager.FactorInstance> otInstances = IcfbIncursionManager.getInstance().getOneTimeFactorsCopy();
        List<IcfbIncursionManager.FactorInstance> mtInstances = IcfbIncursionManager.getInstance().getMonthlyFactorsCopy();

        for (IcfbIncursionManager.FactorInstance instance : otInstances) {
            final FactorWrapper factor = new FactorWrapper(instance);
            addFactor(factor);
            if (Global.getSector().getClock().getElapsedDaysSince(instance.getTimeStamp()) < 1 && !instance.getSentIntel()) {
                instance.setSentIntel(true);
                if (!isHidden()) {
                    listInfoParam = factor;
                    sendUpdateIfPlayerHasIntel(factor, false);
                    listInfoParam = null;
                }
            }
        }

        for (IcfbIncursionManager.FactorInstance instance : mtInstances) {
            final FactorWrapper factor = new FactorWrapper(instance);
            addFactor(factor);
        }

    }

    @Override
    protected String getStageLabel(Object stageId) {
        if (stageId.equals(Stage.INCURSION))
            return "Incursion";
        return super.getStageLabel(stageId);
    }

    @Override
    protected String getStageIcon(Object stageId) {
        if (stageId.equals(Stage.INCURSION))
            return Global.getSettings().getSpriteName("icfb_events", "incursion");
        return super.getStageLabel(stageId);
    }

    @Override
    public boolean isEnded() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean isHidden() {
        if (!IcfbGlobal.getSettings().incursions.isEnabled.get())
            return true;
        return super.isHidden();
    }

    @Override
    protected void addBulletPoints(TooltipMakerAPI info, ListInfoMode mode, boolean isUpdate, Color tc, float initPad) {
        addEventFactorBulletPoints(info, mode, isUpdate, tc, initPad);
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        if (tags == null)
            tags = new HashSet<>();
        tags.add("Incursions");
        return tags;
    }
}
