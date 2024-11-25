package org.shmo.icfb.campaign.quests.impl.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.IcfbFactions;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.missions.BaseIcfbMission;
import org.shmo.icfb.campaign.quests.missions.IcfbMissions;
import org.shmo.icfb.campaign.quests.scripts.BaseQuestStepScript;

import java.awt.*;
import java.util.Set;

public class ExtractScientist extends BaseIcfbMission {

    public static final String MARINES_REQUIRED_KEY = "$icfbSci_marinesRequired";
    public static final String CREDITS_REQUIRED_KEY = "$icfbSci_creditsRequired";
    public static final String CREDITS_STRING_KEY = "$icfbSci_creditsString";
    public static final String IS_MARKET_KEY = "$icfbSci_isMarket";
    public static final String COMPLETE_KEY = "$icfbSci_complete";
    public static final String REF_KEY = "$icfbSci_ref";

    public ExtractScientist(PersonAPI person) {
        final Data data = getData();
        data.missionGiver = person;

        MarketAPI market = IcfbMisc.pickMarket(
                0,
                IcfbFactions.BOUNDLESS.getId(),
                Factions.TRITACHYON,
                Factions.PERSEAN
        );
        if (market == null) {
            data.valid = false;
            return;
        }

        data.targetMarket = market;
        data.targetStarSystem = market.getStarSystem();
        data.targetLocation = market.getPrimaryEntity();
        data.targetFaction = market.getFaction();
        data.repReward = 0.05f;
        data.repPenalty = 0.02f;
        data.xpReward = 2500;
        data.timeLimitDays = 60;
        data.creditReward = 52500 + (market.getSize() * 2500);

        MemoryAPI memory = person.getMemoryWithoutUpdate();
        memory.set(MARINES_REQUIRED_KEY, calculateMarinesRequired());
        memory.set(CREDITS_REQUIRED_KEY, calculateCreditsRequired());
        memory.set(CREDITS_STRING_KEY, Misc.getDGSCredits(calculateCreditsRequired()));
    }

    @Override
    protected void createMission(Quest quest) {
        addStep(quest, 0, new BaseQuestStepScript() {
            @Override
            public void start() {
                initStage0();
            }

            @Override
            public void advance(float deltaTime) {
                ensureMarketIsValid();
            }

            @Override
            public void end() {
                cleanupStage0();
            }
        });

        addStep(quest, 1, new BaseQuestStepScript() {
            @Override
            public void start() {
                initStage1();
            }

            @Override
            public void end() {
                cleanupStage1();
            }
        });
    }

    private void initStage0() {
        final Data data = getData();
        Misc.makeImportant(data.targetMarket.getPrimaryEntity(), getReasonId());
        Misc.setFlagWithReason(data.targetMarket.getPrimaryEntity().getMemoryWithoutUpdate(), IS_MARKET_KEY, getReasonId(), true, data.timeLimitDays);
        ensureMarketIsValid();
    }

    private void initStage1() {
        final Data data = getData();
        data.targetLocation = data.missionGiver.getMarket().getPrimaryEntity();
        data.targetMarket = data.missionGiver.getMarket();
        Misc.makeImportant(data.targetLocation, getReasonId());
        Misc.makeImportant(data.missionGiver, getReasonId());
        data.missionGiver.getMemoryWithoutUpdate().set(COMPLETE_KEY, true);
        data.missionGiver.getMemoryWithoutUpdate().set(REF_KEY, this);
    }

    private void ensureMarketIsValid() {
        final Data data = getData();

        final SectorEntityToken entity = data.targetMarket.getPrimaryEntity();
        final MemoryAPI memory = entity.getMemoryWithoutUpdate();

        memory.set(MARINES_REQUIRED_KEY, calculateCreditsRequired());
        memory.set(CREDITS_REQUIRED_KEY, calculateCreditsRequired());
        memory.set(CREDITS_STRING_KEY, Misc.getDGSCredits(calculateCreditsRequired()));
    }

    private void cleanupStage0() {
        final Data data = getData();
        Misc.makeUnimportant(data.targetMarket.getPrimaryEntity(), getReasonId());
        Misc.setFlagWithReason(data.targetMarket.getPrimaryEntity().getMemoryWithoutUpdate(), IS_MARKET_KEY, getReasonId(), false, 0);
    }

    private void cleanupStage1() {
        final Data data = getData();
        Misc.makeUnimportant(data.targetMarket.getPrimaryEntity(), getReasonId());
        Misc.makeUnimportant(data.missionGiver, getReasonId());
        data.missionGiver.getMemoryWithoutUpdate().unset(COMPLETE_KEY);
        data.missionGiver.getMemoryWithoutUpdate().unset(REF_KEY);
    }

    @Override
    protected void cleanupImpl() {
        cleanupStage0();
        cleanupStage1();
    }

    @Override
    public Set<String> getIntelTags() {
        return null;
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("icfb_intel", "sci");
    }

    @Override
    public String getId() {
        return IcfbMissions.EXTRACT_SCIENTIST;
    }

    @Override
    public String getName() {
        return "Extract Scientist";
    }

    @Override
    public void addBulletPoints(TooltipMakerAPI info, int stageIndex) {
        if (stageIndex == 0) {
            info.addPara("Obtain the notable scientist from %s",
                    0,
                    getData().targetMarket.getTextColorForFactionOrPlanet(),
                    getData().targetMarket.getName()
            );
        }

        if (stageIndex == 1) {
            info.addPara("Return to %s with the scientist",
                    0,
                    getData().missionGiver.getMarket().getTextColorForFactionOrPlanet(),
                    getData().missionGiver.getMarket().getName()
            );
        }
    }

    private int calculateMarinesRequired() {
        return 50 + (getData().targetMarket.getSize() * 25);
    }

    private int calculateCreditsRequired() {
        return getCreditReward() / 2;
    }

    @Override
    public void addDescriptionBody(TooltipMakerAPI info, int stageIndex) {
        final Data data = getData();
        final Color hl = Misc.getHighlightColor();
        final Color fc = data.targetMarket.getTextColorForFactionOrPlanet();
        final Color ng = Misc.getNegativeHighlightColor();
        final int marinesRequired = calculateMarinesRequired();
        final int playerMarines = Global.getSector().getPlayerFleet().getCargo().getMarines();
        final int creditsRequired = calculateCreditsRequired();
        final int playerCredits = (int)Global.getSector().getPlayerFleet().getCargo().getCredits().get();

        if (stageIndex == 0) {
            info.addPara(
                    "You must extract the notable scientist from %s through whatever means are necessary, " +
                            "and return to %s. Your potential options are as follows:",
                    0,
                    new Color[] { fc, data.missionGiver.getMarket().getTextColorForFactionOrPlanet() },
                    data.targetMarket.getName(),
                    data.missionGiver.getMarket().getName()
            );
            info.addPara(
                    "  1. If you choose to abduct them with a raid, you'll need at least %s marines. " +
                            "your fleet is currently carrying %s marines.",
                    5,
                    new Color[] { hl, marinesRequired > playerMarines ? ng : hl },
                    Misc.getWithDGS(marinesRequired),
                    Misc.getWithDGS(playerMarines)
            );
            info.addPara(
                    "  2. Bribing them will likely cost at least %s. You have %s.",
                    5,
                    new Color[] { hl, creditsRequired > playerCredits ? ng : hl },
                    Misc.getDGSCredits(creditsRequired),
                    Misc.getDGSCredits(playerCredits)
            );
            info.addPara(
                    "  3. %s",
                    5,
                    Misc.getStoryOptionColor(),
                    "There may be some other way to convince them to come with you."
            );
            info.addPara(
                    "Consider your options carefully, and avoid the ensuing wrath of %s " +
                            "once they find one of their prized researchers missing.",
                    10,
                    fc,
                    data.targetFaction.getDisplayNameWithArticle()
            );
        }

        if (stageIndex == 1) {
            info.addPara(
                    "You've acquired the scientist. Return to %s, and avoid the ensuing wrath of %s " +
                            "once they find one of their prized researchers missing.",
                    0,
                    new Color[] { data.missionGiver.getMarket().getTextColorForFactionOrPlanet(), fc },
                    data.missionGiver.getMarket().getName(),
                    data.targetFaction.getDisplayNameWithArticle()
            );
        }
    }
}
