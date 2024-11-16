package org.shmo.icfb.campaign.quests.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import javafx.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.magiclib.campaign.MagicCaptainBuilder;
import org.magiclib.campaign.MagicFleetBuilder;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.intel.BaseQuestStepIntel;
import org.shmo.icfb.campaign.quests.scripts.BaseQuestScript;
import org.shmo.icfb.campaign.quests.scripts.BaseQuestStepScript;
import org.shmo.icfb.campaign.quests.scripts.QuestStepScript;
import org.shmo.icfb.campaign.scripts.IcfbQuestManager;
import org.shmo.icfb.utilities.ShmoGuiUtils;

import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class BaseIcfbMission implements IcfbMission {
    public static class Data {
        public PersonAPI missionGiver = null;
        public StarSystemAPI targetStarSystem = null;
        public SectorEntityToken targetLocation = null;
        public FactionAPI targetFaction = null;
        public MarketAPI targetMarket = null;
        public CampaignFleetAPI targetFleet = null;
        public int creditReward = 0;
        public int xpReward = 0;
        public float repReward = 0;
        public float repPenalty = 0;
        public boolean repAppliesToFaction = false;
        public float timeLimitDays = 0;
        public long startTimeStamp = 0;
        public boolean failed = false;
        public boolean valid = true;
        public boolean canAbandon = true;
    }

    private final Data _data = new Data();
    private String _questId;
    private boolean _cleanedUp = false;
    private final List<CampaignFleetAPI> _fleets = new ArrayList<>();

    public Data getData() {
        return _data;
    }

    @Override
    public boolean callEvent(String ruleId, String action, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        if ("showMap".equals(action)) {
            SectorEntityToken mapLoc = getMapLocation();

            if (mapLoc != null) {
                String title = params.get(0).getStringWithTokenReplacement(ruleId, dialog, memoryMap);
                String text = "";
                Set<String> tags = getIntelTags();
                String icon = getIcon();

                Color color = getData().missionGiver.getFaction().getBaseUIColor();

                if (mapLoc.getFaction() != null && !mapLoc.getFaction().isNeutralFaction()) {
                    color = mapLoc.getFaction().getBaseUIColor();
                } else if (mapLoc instanceof PlanetAPI) {
                    PlanetAPI planet = (PlanetAPI) mapLoc;
                    if (planet.getStarSystem() != null && planet.getFaction().isNeutralFaction()) {
                        StarSystemAPI system = planet.getStarSystem();
                        if (system.getStar() == planet || system.getCenter() == planet) {
                            if (planet.getMarket() != null) {
                                color = planet.getMarket().getTextColorForFactionOrPlanet();
                            } else {
                                color = Misc.setAlpha(planet.getSpec().getIconColor(), 255);
                                color = Misc.setBrightness(color, 235);
                            }
                        } else {
                            color = Misc.setAlpha(planet.getSpec().getIconColor(), 255);
                            color = Misc.setBrightness(color, 235);
                        }
                    }
                }

                dialog.getVisualPanel().showMapMarker(mapLoc,
                        title, color,
                        true, icon, text, tags);
            }
            return true;
        }

        if ("hideMap".equals(action)) {
            dialog.getVisualPanel().removeMapMarkerFromPersonInfo();
            return true;
        }

        return false;
    }

    private Quest initQuest() {
        _questId = _data.missionGiver.getId() + ":" + getId();
        final Quest quest = new Quest(_questId);
        quest.setName(getName());
        quest.setIcon(getIcon());
        final Set<String> tags = getIntelTags();
        if (tags != null) {
            for (String tag : tags) {
                quest.addTag(tag);
            }
        }
        quest.setScript(
                new BaseQuestScript() {
                    @Override
                    public void start() {
                        getData().startTimeStamp = Global.getSector().getClock().getTimestamp();
                    }

                    @Override
                    public void advance(float deltaTime) {
                        if (isTimeUp()) {
                            fail();
                        }
                        if (isFailed() && !quest.isOnFinalStep()) {
                            quest.progressToFinalStep();
                        }
                    }

                    @Override
                    public void end() {
                        if (!_cleanedUp)
                            BaseIcfbMission.this.cleanup();
                    }
                }
        );
        return quest;
    }

    protected abstract void createMission(Quest quest);

    protected void addStep(Quest quest, int stageIndex, QuestStepScript script) {
        quest.addStep(new IcfbMissionStepIntel(this, stageIndex), script);
    }

    private void addFinalStep(Quest quest) {
        quest.addStep(
                new BaseQuestStepIntel() {
                    @Override
                    public void addNotificationBody(TooltipMakerAPI info) {
                        final Data data = getData();
                        if (!data.failed) {
                            info.addPara("Complete!", Misc.getStoryOptionColor(), 0);
                        } else {
                            info.addPara("Failed", Misc.getNegativeHighlightColor(), 0);
                        }
                    }

                    @Override
                    public void addNotificationBulletPoints(TooltipMakerAPI info) {
                        final Data data = getData();
                        final Color per = data.missionGiver.getFaction().getBaseUIColor();
                        final Color hl = Misc.getHighlightColor();
                        final Color pos = Misc.getStoryOptionColor();

                        if (!data.failed) {
                            if (data.creditReward != 0)
                                info.addPara("Received %s", 0, hl, Misc.getDGSCredits(data.creditReward));
                            if (data.xpReward != 0)
                                info.addPara("Gained %s XP", 0, hl, Misc.getWithDGS(data.xpReward));
                            if (data.repReward != 0) {
                                info.addPara(
                                        "Reputation with %s improved by %s to %s",
                                        0, new Color[]{per, hl, data.missionGiver.getRelToPlayer().getRelColor()},
                                        data.missionGiver.getName().getFullName(),
                                        Misc.getRoundedValue(data.repReward * 100),
                                        Misc.getRoundedValue(data.missionGiver.getRelToPlayer().getRepInt())
                                );
                                if (data.repAppliesToFaction) {
                                    info.addPara(
                                            "Reputation with %s improved by %s to %s",
                                            0, new Color[]{per, hl, data.missionGiver.getRelToPlayer().getRelColor()},
                                            data.missionGiver.getFaction().getDisplayName(),
                                            Misc.getRoundedValue(data.repReward * 100),
                                            Misc.getRoundedValue(data.missionGiver.getFaction().getRelToPlayer().getRepInt())
                                    );
                                }
                            }
                        } else {
                            if (data.repPenalty != 0) {
                                info.addPara(
                                        "Reputation with %s decreased by %s to %s",
                                        0, new Color[]{per, hl, data.missionGiver.getRelToPlayer().getRelColor()},
                                        data.missionGiver.getName().getFullName(),
                                        Misc.getRoundedValue(data.repPenalty * 100),
                                        Misc.getRoundedValue(data.missionGiver.getRelToPlayer().getRepInt())
                                );
                                if (data.repAppliesToFaction) {
                                    info.addPara(
                                            "Reputation with %s decreased by %s to %s",
                                            0, new Color[]{per, pos, data.missionGiver.getRelToPlayer().getRelColor()},
                                            data.missionGiver.getFaction().getDisplayName(),
                                            Misc.getRoundedValue(data.repPenalty * 100),
                                            Misc.getRoundedValue(data.missionGiver.getFaction().getRelToPlayer().getRepInt())
                                    );
                                }
                            }
                        }
                    }

                    @Override
                    public void addDescriptionBody(TooltipMakerAPI info) {
                        final Data data = getData();

                        if (getDescriptionImage() != null) {
                            ShmoGuiUtils.addCenteredImage(info, getDescriptionImage(), getBodyPanelWidth(), 0);
                        }
                        if (!data.failed)
                            info.addPara("Mission complete!", 10);
                        else
                            info.addPara("Mission failed.", 10);
                    }

                    @Override
                    public void addDescriptionBulletPoints(TooltipMakerAPI info) {
                        addNotificationBulletPoints(info);
                    }

                    @Override
                    public SectorEntityToken getMapLocation(SectorMapAPI map) {
                        final Data data = getData();

                        if (data.targetLocation != null)
                            return data.targetLocation;
                        else if (data.targetStarSystem != null)
                            return data.targetStarSystem.getCenter();

                        return null;
                    }

                    @Override
                    public List<IntelInfoPlugin.ArrowData> getArrowData(SectorMapAPI map) {
                        return null;
                    }
                },
                new BaseQuestStepScript() {
                    public long timeStamp = 0;
                    public float daysUntilDone = 0;

                    @Override
                    public void start() {
                        timeStamp = Global.getSector().getClock().getTimestamp();
                        daysUntilDone = 3;
                        if (!_cleanedUp)
                            BaseIcfbMission.this.cleanup();

                        final Data data = getData();
                        final CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

                        if (!data.failed) {
                            Global.getSoundPlayer().playUISound("ui_rep_raise", 1.0f, 1.0f);

                            if (data.creditReward != 0) {
                                playerFleet.getCargo().getCredits().add(data.creditReward);
                            }
                            if (data.xpReward != 0) {
                                playerFleet.getCommanderStats().addXP(data.xpReward);
                            }
                            if (data.repReward != 0) {
                                data.missionGiver.getRelToPlayer().adjustRelationship(data.repReward, null);
                                if (data.repAppliesToFaction) {
                                    data.missionGiver.getFaction().getRelToPlayer().adjustRelationship(data.repReward, null);
                                }
                            }
                        } else {
                            Global.getSoundPlayer().playUISound("ui_rep_drop", 1.0f, 1.0f);

                            if (data.repPenalty != 0) {
                                data.missionGiver.getRelToPlayer().adjustRelationship(-data.repPenalty, null);
                                if (data.repAppliesToFaction) {
                                    data.missionGiver.getFaction().getRelToPlayer().adjustRelationship(-data.repPenalty, null);
                                }
                            }
                        }
                    }

                    @Override
                    public void advance(float deltaTime) {}

                    @Override
                    public void end() {}

                    @Override
                    public boolean isComplete() {
                        return Global.getSector().getClock().getElapsedDaysSince(timeStamp) > daysUntilDone;
                    }
                }
        );
    }

    @Override
    public String getDescriptionImage() {
        return null;
    }

    @Override
    public void addBulletPoints(TooltipMakerAPI info, int stageIndex) {

    }

    @Override
    public void addDescriptionBody(TooltipMakerAPI info, int stageIndex) {

    }

    @Override
    public SectorEntityToken getMapLocation() {
        final Data data = getData();

        if (data.targetLocation != null)
            return data.targetLocation;
        else if (data.targetMarket != null)
            return data.targetMarket.getPrimaryEntity();
        else if (data.targetStarSystem != null)
            return data.targetStarSystem.getCenter();

        return null;
    }

    @Override
    public String getLocationName() {
        if (_data.targetStarSystem != null) {
            return _data.targetStarSystem.getName();
        }
        else if (_data.targetLocation != null && _data.targetLocation.getContainingLocation() != null && !_data.targetLocation.isInHyperspace()) {
            return _data.targetLocation.getStarSystem().getName();
        }
        else if (_data.targetMarket != null) {
            return _data.targetMarket.getContainingLocation().getName();
        }
        return null;
    }

    @Override
    public int getCreditReward() {
        return _data.creditReward;
    }

    @Override
    public String getTargetFactionName() {
        if (_data.targetFaction != null)
            return _data.targetFaction.getDisplayName();
        else if (_data.targetMarket != null) {
            return _data.targetMarket.getFaction().getDisplayName();
        } else if (_data.targetLocation != null && _data.targetLocation.getFaction() != null) {
            return _data.targetLocation.getFaction().getDisplayName();
        }
        return null;
    }

    @Override
    public String getTargetMarketName() {
        if (_data.targetMarket != null) {
            return _data.targetMarket.getName();
        }
        return null;
    }

    @Override
    public float getTimeLimitDays() {
        return _data.timeLimitDays;
    }

    @Override
    public boolean isFailed() {
        return _data.failed;
    }

    public void fail() {
        _data.failed = true;
        Quest quest = IcfbQuestManager.getInstance().getQuest(getData().missionGiver.getId() + ":" + getId());
        if (quest == null)
            return;
        quest.progressToFinalStep();
    }

    public boolean isTimeUp() {
        if (_data.timeLimitDays <= 0)
            return false;
        return Global.getSector().getClock().getElapsedDaysSince(_data.startTimeStamp) > _data.timeLimitDays;
    }

    @Override
    public float getMinimumRepLevel() {
        return -1;
    }

    @Override
    public boolean isValid() {
        return _data.valid && !_data.failed && isValidImpl();
    }

    protected boolean isValidImpl() {
        return true;
    }

    @Override
    public void cleanup() {
        _cleanedUp = true;
        cleanupFleets();
        cleanupImpl();
    }

    protected void cleanupImpl() {}

    protected CampaignFleetAPI createFleet(MagicFleetBuilder fleetBuilder) {
        CampaignFleetAPI fleet = fleetBuilder.create();
        _fleets.add(fleet);
        return fleet;
    }

    protected CampaignFleetAPI createFleet(
            String factionId,
            String fleetType,
            String fleetName,
            int fleetPoints,
            boolean important,
            boolean lowRepImpact
    ) {
        return createFleet(
                factionId,
                fleetType,
                fleetName,
                fleetPoints,
                important,
                lowRepImpact,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null
        );
    }

    protected CampaignFleetAPI createFleet(
            String factionId,
            String fleetType,
            String fleetName,
            int fleetPoints,
            boolean important,
            boolean lowRepImpact,
            @Nullable SectorEntityToken spawnLocation,
            @Nullable SectorEntityToken despawnLocation,
            @Nullable FleetAssignment assignment,
            @Nullable SectorEntityToken assignmentTarget
    ) {
        return createFleet(
                factionId,
                fleetType,
                fleetName,
                fleetPoints,
                important,
                lowRepImpact,
                spawnLocation,
                despawnLocation,
                assignment,
                assignmentTarget,
                null,
                null,
                null,
                false,
                null
        );
    }

    protected CampaignFleetAPI createFleet(
            String factionId,
            String fleetType,
            String fleetName,
            int fleetPoints,
            boolean important,
            boolean lowRepImpact,
            @Nullable PersonAPI captain,
            @Nullable String flagshipVariant,
            @Nullable String flagshipName,
            boolean flagshipAlwaysRecoverable,
            @Nullable Map<String, Integer> supportFleet
    ) {
        return createFleet(
                factionId,
                fleetType,
                fleetName,
                fleetPoints,
                important,
                lowRepImpact,
                null,
                null,
                null,
                null,
                captain,
                flagshipVariant,
                fleetName,
                flagshipAlwaysRecoverable,
                supportFleet
        );
    }

    protected CampaignFleetAPI createFleet(
            String factionId,
            String fleetType,
            String fleetName,
            int fleetPoints,
            boolean important,
            boolean lowRepImpact,
            @Nullable SectorEntityToken spawnLocation,
            @Nullable SectorEntityToken despawnLocation,
            @Nullable FleetAssignment assignment,
            @Nullable SectorEntityToken assignmentTarget,
            @Nullable PersonAPI captain,
            @Nullable String flagshipVariant,
            @Nullable String flagshipName,
            boolean flagshipAlwaysRecoverable,
            @Nullable Map<String, Integer> supportFleet
    ) {
        MagicFleetBuilder builder = new MagicFleetBuilder();
        builder.setFleetFaction(factionId);
        builder.setFleetType(fleetType);
        builder.setFleetName(fleetName);
        builder.setIsImportant(important);
        builder.setMinFP(fleetPoints);

        if (spawnLocation != null) {
            builder.setSpawnLocation(spawnLocation);
        }
        if (assignment != null) {
            builder.setAssignment(assignment);
            builder.setAssignmentTarget(assignmentTarget);
        }
        if (captain != null) {
            builder.setCaptain(captain);
        } else {
            MagicCaptainBuilder captainBuilder = new MagicCaptainBuilder(factionId);
            builder.setCaptain(captainBuilder.create());
        }
        if (flagshipVariant != null && !flagshipVariant.isEmpty()) {
            builder.setFlagshipVariant(flagshipVariant);
            if (flagshipName != null && !flagshipName.isEmpty()) {
                builder.setFleetName(flagshipName);
            }
            builder.setFlagshipAlwaysRecoverable(flagshipAlwaysRecoverable);
        }
        if (supportFleet != null && !supportFleet.isEmpty()) {
            builder.setSupportFleet(supportFleet);
        }

        CampaignFleetAPI fleet = builder.create();
        fleet.getMemoryWithoutUpdate().set("$icfbQuestId", _questId);
        fleet.getMemoryWithoutUpdate().set("$" + _questId + "_despawnLocation", despawnLocation);
        if (important) {
            Misc.makeImportant(fleet, null);
        }
        if (lowRepImpact) {
            fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_LOW_REP_IMPACT, true);
        }
        _fleets.add(fleet);
        return fleet;
    }

    protected void despawnFleet(CampaignFleetAPI fleet, SectorEntityToken despawnLocation) {
        fleet.getMemoryWithoutUpdate().set("$" + _questId + "_despawnLocation", despawnLocation);
        despawnFleet(fleet);
    }

    protected void despawnFleet(CampaignFleetAPI fleet) {
        if (fleet.getContainingLocation() == null || fleet.isExpired())
            return;
        MemoryAPI memory = fleet.getMemory();
        if (memory.contains("$" + _questId + "_despawned"))
            return;

        SectorEntityToken despawnLocation = memory.getEntity("$" + _questId + "_despawnLocation");
        if (despawnLocation == null) {
            MarketAPI market = Misc.getBiggestMarketInLocation(fleet.getContainingLocation());
            if (market != null && market.getFactionId().equals(fleet.getFaction().getId()))
                despawnLocation = market.getPrimaryEntity();
        }
        if (despawnLocation == null) {
            MarketAPI market = IcfbMisc.pickMarket( 0, fleet.getFaction().getId());
            if (market != null)
                despawnLocation = market.getPrimaryEntity();
        }
        if (despawnLocation == null) {
            despawnLocation = IcfbMisc.pickSystem().getCenter();
        }
        memory.unset(MemFlags.MEMORY_KEY_MAKE_HOSTILE);
        memory.unset(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE);
        memory.unset(MemFlags.FLEET_DO_NOT_IGNORE_PLAYER);
        memory.unset(MemFlags.FLEET_BUSY);
        memory.unset(MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT);
        memory.unset(MemFlags.MEMORY_KEY_PURSUE_PLAYER);
        memory.unset(MemFlags.MEMORY_KEY_MAKE_ALWAYS_PURSUE);
        memory.unset(MemFlags.MEMORY_KEY_NEVER_AVOID_PLAYER_SLOWLY);
        memory.unset(MemFlags.MEMORY_KEY_MAKE_PREVENT_DISENGAGE);
        memory.unset(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE_ONE_BATTLE_ONLY);
        memory.unset(MemFlags.ENTITY_MISSION_IMPORTANT);
        Misc.makeUnimportant(fleet, null);
        memory.unset("$icfbQuestId");
        memory.unset("$" + _questId + "_despawnLocation");
        memory.set("$" + _questId + "_despawned", true);
        fleet.clearAssignments();
        fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, despawnLocation, 100000);
    }

    private void cleanupFleets() {
        for (CampaignFleetAPI fleet : _fleets) {
            despawnFleet(fleet);
        }
        _fleets.clear();
    }

    @Override
    public Quest create() {
        final Quest quest = initQuest();
        createMission(quest);
        addFinalStep(quest);
        return quest;
    }
}
