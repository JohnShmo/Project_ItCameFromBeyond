package org.shmo.icfb.campaign.quests.impl.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.jetbrains.annotations.NotNull;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.missions.BaseIcfbMission;
import org.shmo.icfb.campaign.quests.missions.IcfbMissions;
import org.shmo.icfb.campaign.quests.scripts.BaseQuestStepScript;

import java.awt.*;
import java.util.Set;

public class AcquireResource extends BaseIcfbMission {

    public static final String COMMODITY_TYPE_KEY = "$icfbRes_commodityType";
    public static final String COMMODITY_NAME_KEY = "$icfbRes_commodityName";
    public static final String COMMODITY_COUNT_KEY = "$icfbRes_commodityCount";
    public static final String COMMODITY_COUNT_DGS_KEY = "$icfbRes_commodityCountDGS";
    public static final String CAN_COMPLETE_KEY = "$icfbRes_canComplete";
    public static final String REF_KEY = "$icfbRes_ref";

    public AcquireResource(PersonAPI person) {
        Data data = getData();
        data.missionGiver = person;
        final String type = generateCommodityType();
        CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(type);

        final String name = spec.getName();
        int count = generateCommodityCount();
        final int creditsPerUnit = Math.round(spec.getBasePrice() * (2f + Misc.random.nextFloat()));
        while (count * creditsPerUnit > 200000) {
            count--;
        }
        while (count * creditsPerUnit < 25000) {
            count++;
        }

        data.creditReward = creditsPerUnit * count;
        data.repReward = 0.05f;
        data.xpReward = 2500;
        data.timeLimitDays = 90;
        data.targetMarket = person.getMarket();
        data.targetStarSystem = person.getMarket().getStarSystem();
        data.targetLocation = person.getMarket().getPrimaryEntity();

        MemoryAPI memory = person.getMemoryWithoutUpdate();
        memory.set(COMMODITY_TYPE_KEY, type);
        memory.set(COMMODITY_NAME_KEY, name);
        memory.set(COMMODITY_COUNT_KEY, count);
        memory.set(COMMODITY_COUNT_DGS_KEY, Misc.getWithDGS(count));
    }

    private static String generateCommodityType() {
        String[] commodities = new String[] {
                Commodities.HEAVY_MACHINERY,
                Commodities.SUPPLIES,
                Commodities.METALS,
                Commodities.ORGANICS,
                Commodities.RARE_METALS,
                Commodities.VOLATILES,
                Commodities.HAND_WEAPONS
        };
        final int count = commodities.length;
        final int index = Misc.random.nextInt(count);
        return commodities[index];
    }

    private static int generateCommodityCount() {
        final int base = (int)Global.getSector().getPlayerFleet().getCargo().getMaxCapacity() / 3;
        final int variation = (int)Global.getSector().getPlayerFleet().getCargo().getMaxCapacity() / 2;
        return Math.max(base + (int)(Misc.random.nextFloat() * variation), 100 + (int)(Misc.random.nextFloat() * variation));
    }

    @Override
    protected void createMission(Quest quest) {
        addStep(quest, 0, new BaseQuestStepScript() {
            @Override
            public void start() {
                initStage0();
            }

            @Override
            public void end() {
                cleanupImpl();
            }
        });
    }

    private void initStage0() {
        Data data = getData();
        data.missionGiver.getMemoryWithoutUpdate().set(CAN_COMPLETE_KEY, true);
        data.missionGiver.getMemoryWithoutUpdate().set(REF_KEY, this);
        Misc.makeImportant(data.missionGiver, getReasonId());
        Misc.makeImportant(data.targetLocation, getReasonId());
    }

    @Override
    public Set<String> getIntelTags() {
        return IcfbMisc.setOf(Tags.INTEL_TRADE, Tags.INTEL_SMUGGLING);
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("icfb_intel", "res");
    }

    @Override
    public String getId() {
        return IcfbMissions.ACQUIRE_RESOURCE;
    }

    @Override
    public String getName() {
        return "Acquire Resource: " + getData().missionGiver.getMemoryWithoutUpdate().getString(COMMODITY_NAME_KEY);
    }

    @Override
    protected void cleanupImpl() {
        Data data = getData();
        data.missionGiver.getMemoryWithoutUpdate().unset(CAN_COMPLETE_KEY);
        data.missionGiver.getMemoryWithoutUpdate().unset(REF_KEY);
        Misc.makeUnimportant(data.missionGiver, getReasonId());
        Misc.makeUnimportant(data.targetLocation, getReasonId());
    }

    @Override
    public void addDescriptionBody(TooltipMakerAPI info, int stageIndex) {
        if (stageIndex == 0) {
            Data data = getData();
            PersonAPI person = data.missionGiver;
            MemoryAPI memory = person.getMemoryWithoutUpdate();

            final String type = memory.getString(COMMODITY_TYPE_KEY);
            final String name = memory.getString(COMMODITY_NAME_KEY);
            final int count = memory.getInt(COMMODITY_COUNT_KEY);
            final int withPlayer = Math.round(Global.getSector().getPlayerFleet().getCargo().getCommodityQuantity(type));
            final Color hl = Misc.getHighlightColor();
            final Color tx = Misc.getTextColor();
            final Color ng = Misc.getNegativeHighlightColor();
            final Color fc = person.getFaction().getBaseUIColor();

            info.addPara(
                    "%s tasked you with collecting and returning with %s %s. Once the commodity is procured, " +
                            "return to %s to claim your reward.",
                    0,
                    new Color[] { fc, hl, tx, data.targetMarket.getFaction().getBaseUIColor() },
                    person.getName().getFullName(),
                    Misc.getWithDGS(count),
                    name,
                    data.targetMarket.getName()
            );

            info.addPara(
                    "Your fleet is currently carrying %s units of %s.",
                    10,
                    new Color[] { count > withPlayer ? ng : hl, tx },
                    Misc.getWithDGS(withPlayer),
                    name
            );
        }
    }

    @Override
    public void addBulletPoints(TooltipMakerAPI info, int stageIndex) {
        if (stageIndex == 0) {
            Data data = getData();
            PersonAPI person = data.missionGiver;
            MemoryAPI memory = person.getMemoryWithoutUpdate();

            info.addPara(
                    "Return to %s with %s " + memory.getString(COMMODITY_NAME_KEY),
                    0,
                    new Color[] { person.getFaction().getBaseUIColor(), Misc.getHighlightColor() },
                    person.getName().getFullName(),
                    Misc.getWithDGS(memory.getInt(COMMODITY_COUNT_KEY))
            );
        }
    }

    @NotNull
    private String getReasonId() {
        return getData().missionGiver.getId() + ":" + getId();
    }
}
