package org.shmo.icfb.campaign.quests.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.characters.AbilityPlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.missions.hub.BaseMissionHub;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.input.Keyboard;
import org.magiclib.campaign.MagicFleetBuilder;
import org.magiclib.util.MagicCampaign;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.IcfbEntities;
import org.shmo.icfb.campaign.IcfbFactions;
import org.shmo.icfb.campaign.IcfbMemFlags;
import org.shmo.icfb.campaign.IcfbPeople;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;
import org.shmo.icfb.campaign.quests.intel.BaseQuestStepIntel;
import org.shmo.icfb.campaign.quests.intel.QuestStepIntelPlugin;
import org.shmo.icfb.campaign.quests.scripts.BaseQuestStepScript;
import org.shmo.icfb.utilities.ShmoGuiUtils;

import java.util.ArrayList;
import java.util.List;

public class JumpstartRequiredQuest implements QuestFactory {
    public static final String ID = "jumpstart_required";
    public static final String NAME = "Jumpstart Required";

    public static final String SHOW_OR_HIDE_MESSAGE_BUTTON = "show_message";

    @Override
    public Quest create() {
        final Quest quest = new Quest(ID);
        quest.setName(NAME);
        quest.setIcon(Global.getSettings().getSpriteName("icfb_portraits", "shifter_01_static"));

        final PersonAPI xent = IcfbPeople.XENT_ALABASTER.getPerson();

        quest.addStep(
                new BaseQuestStepIntel() {
                    private transient boolean _showTransmission = false;

                    @Override
                    public void addNotificationBody(TooltipMakerAPI info) {
                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_notifBody_00"),
                                0,
                                Misc.getTextColor(),
                                xent.getName().getFullName()
                        );
                    }

                    @Override
                    public void addNotificationBulletPoints(TooltipMakerAPI info) {
                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_bulletPoint_00"),
                                0,
                                xent.getFaction().getBaseUIColor(),
                                xent.getName().getFullName()
                        );
                    }

                    @Override
                    public void addDescriptionBody(TooltipMakerAPI info) {
                        final float width = getBodyPanelWidth();
                        ShmoGuiUtils.addPersonDetails(info, xent, width, 0, true);

                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_descBody_00"),
                                10,
                                xent.getMarket().getTextColorForFactionOrPlanet(),
                                xent.getMarket().getName()
                        );

                        ButtonAPI button = ShmoGuiUtils.addGenericButton(
                                info,
                                width,
                                (!_showTransmission) ?
                                        IcfbMisc.getQuestIntelString("jumpstartRequired_button_00") :
                                        IcfbMisc.getQuestIntelString("jumpstartRequired_button_01"),
                                SHOW_OR_HIDE_MESSAGE_BUTTON
                        );
                        button.setShortcut(Keyboard.KEY_T, true);

                        if (!_showTransmission)
                            return;

                        LabelAPI message = info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_transmissionMessage"),
                                10,
                                Misc.getTextColor(),
                                Global.getSector().getPlayerPerson().getName().getFullName(),
                                xent.getName().getFullName()
                        );
                        message.italicize();
                    }

                    @Override
                    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui, QuestStepIntelPlugin plugin) {
                        if (buttonId.equals(SHOW_OR_HIDE_MESSAGE_BUTTON)) {
                            _showTransmission = !_showTransmission;
                            ui.updateUIForItem(plugin);
                            Global.getSector().getMemoryWithoutUpdate().set(IcfbMemFlags.CAN_SELECT_CORRECT_XENT_GREETING, true);
                        }
                    }

                    @Override
                    public void addDescriptionBulletPoints(TooltipMakerAPI info) {

                    }

                    @Override
                    public SectorEntityToken getMapLocation(SectorMapAPI map) {
                        return xent.getMarket().getPrimaryEntity();
                    }

                    @Override
                    public List<IntelInfoPlugin.ArrowData> getArrowData(SectorMapAPI map) {
                        return null;
                    }
                },
                new BaseQuestStepScript() {
                    @Override
                    public void start() {
                        xent.getMarket().getCommDirectory().getEntryForPerson(xent).setHidden(false);
                        BaseMissionHub.set(xent, new XentMissionHub(xent));
                        Misc.makeImportant(xent, ID);
                        Misc.makeImportant(xent.getMarket().getPrimaryEntity(), ID);
                    }

                    @Override
                    public void advance(float deltaTime) {

                    }

                    @Override
                    public void end() {
                        Misc.makeUnimportant(xent, ID);
                        Misc.makeUnimportant(xent.getMarket().getPrimaryEntity(), ID);
                        Global.getSector().getMemoryWithoutUpdate().unset(IcfbMemFlags.AWAITING_AGENT_CONTACT);
                    }

                    @Override
                    public boolean isComplete() {
                        return false;
                    }
                }
        );

        quest.addStep(
                new BaseQuestStepIntel() {

                    @Override
                    public void addNotificationBody(TooltipMakerAPI info) {
                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_notifBody_10"),
                                0
                        );
                    }

                    @Override
                    public void addNotificationBulletPoints(TooltipMakerAPI info) {
                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_bulletPoint_10"),
                                0,
                                xent.getFaction().getBaseUIColor(),
                                xent.getName().getFullName()
                        );
                    }

                    @Override
                    public void addDescriptionBody(TooltipMakerAPI info) {
                        final float width = getBodyPanelWidth();
                        ShmoGuiUtils.addPersonDetails(info, xent, width, 0, true);

                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_descBody_10"),
                                10,
                                xent.getMarket().getTextColorForFactionOrPlanet(),
                                xent.getMarket().getName()
                        );

                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_descBody_11"),
                                10,
                                IcfbFactions.BOUNDLESS.getFaction().getBaseUIColor(),
                                "the Boundless"
                        );

                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_descBody_12"),
                                10,
                                xent.getFaction().getBaseUIColor(),
                                xent.getName().getFullName()
                        );
                    }

                    @Override
                    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui, QuestStepIntelPlugin plugin) {

                    }

                    @Override
                    public void addDescriptionBulletPoints(TooltipMakerAPI info) {

                    }

                    @Override
                    public SectorEntityToken getMapLocation(SectorMapAPI map) {
                        return xent.getMarket().getPrimaryEntity();
                    }

                    @Override
                    public List<IntelInfoPlugin.ArrowData> getArrowData(SectorMapAPI map) {
                        return null;
                    }
                },

                new BaseQuestStepScript() {
                    @Override
                    public void start() {
                        Global.getSector().getMemoryWithoutUpdate().set(IcfbMemFlags.ACCEPTING_XENT_MISSIONS, true);
                    }

                    @Override
                    public void advance(float deltaTime) {
                        if (
                                xent.getRelToPlayer().getRel() >= 0.4f &&
                                        !Global.getSector()
                                                .getMemoryWithoutUpdate()
                                                .getBoolean(IcfbMemFlags.XENT_OFFERING_SPECIAL_MISSION)
                        ) {
                            Global.getSector().getMemoryWithoutUpdate().set(IcfbMemFlags.XENT_OFFERING_SPECIAL_MISSION, true);
                        }
                    }

                    @Override
                    public void end() {
                        Global.getSector().getMemoryWithoutUpdate().unset(IcfbMemFlags.ACCEPTING_XENT_MISSIONS);
                        Global.getSector().getMemoryWithoutUpdate().unset(IcfbMemFlags.XENT_OFFERING_SPECIAL_MISSION);
                    }

                    @Override
                    public boolean isComplete() {
                        return false;
                    }
                }
        );

        quest.addStep(
                new BaseQuestStepIntel() {

                    @Override
                    public void addNotificationBody(TooltipMakerAPI info) {
                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_notifBody_20"),
                                0
                        );
                    }

                    @Override
                    public void addNotificationBulletPoints(TooltipMakerAPI info) {
                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_bulletPoint_20"),
                                0,
                                Misc.getHighlightColor(),
                                String.valueOf(SpecialMission.getObjectiveProgress()),
                                String.valueOf(SpecialMission.OBJECTIVE_GOAL)
                        );
                    }

                    @Override
                    public void addDescriptionBody(TooltipMakerAPI info) {
                        final float width = getBodyPanelWidth();
                        ShmoGuiUtils.addPersonDetails(info, xent, width, 0, true);

                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_descBody_10"),
                                10,
                                xent.getMarket().getTextColorForFactionOrPlanet(),
                                xent.getMarket().getName()
                        );

                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_descBody_11"),
                                10,
                                IcfbFactions.BOUNDLESS.getFaction().getBaseUIColor(),
                                "the Boundless"
                        );

                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_descBody_20"),
                                10
                        );
                    }

                    @Override
                    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui, QuestStepIntelPlugin plugin) {

                    }

                    @Override
                    public void addDescriptionBulletPoints(TooltipMakerAPI info) {
                        addNotificationBulletPoints(info);
                    }

                    @Override
                    public SectorEntityToken getMapLocation(SectorMapAPI map) {
                        return null;
                    }

                    @Override
                    public List<IntelInfoPlugin.ArrowData> getArrowData(SectorMapAPI map) {
                        return null;
                    }
                },

                new BaseQuestStepScript() {

                    private boolean _canCheckSB = false;
                    private boolean _isInHyperspace = false;
                    private boolean _alreadySpawnedMercenaryFleet = false;
                    private CampaignFleetAPI _mercenaryFleet = null;
                    private boolean _despawnedMercenaryFleet = false;

                    @Override
                    public void start() {
                        SpecialMission.setObjectiveProgress(0);
                    }

                    @Override
                    public void advance(float deltaTime) {
                        checkSensorBursts();
                        trySpawnMercenaryFleet();
                        updateMercenaryFleet();
                    }

                    @Override
                    public void end() {
                        despawnMercenaryFleet();
                    }

                    @Override
                    public boolean isComplete() {
                        return SpecialMission.getObjectiveProgress() >= SpecialMission.OBJECTIVE_GOAL;
                    }

                    private void trySpawnMercenaryFleet() {
                        final CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
                        if (playerFleet == null)
                            return;
                        if (!shouldSpawnMercenaryFleet())
                            return;
                        CampaignFleetAPI fleet = createMercenaryFleet();
                        LocationAPI location = playerFleet.getContainingLocation();
                        location.addEntity(fleet);
                        fleet.setLocation(playerFleet.getLocation().getX() + 500, playerFleet.getLocation().getY() + 200);
                        Misc.makeImportant(fleet, ID);
                        _alreadySpawnedMercenaryFleet = true;
                        _mercenaryFleet = fleet;
                    }

                    private CampaignFleetAPI createMercenaryFleet() {
                        final CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

                        MagicFleetBuilder fb = MagicCampaign.createFleetBuilder();
                        fb.setCaptain(IcfbPeople.ARIA_SEROKA_KOTOV.getPerson());
                        fb.setFleetFaction(Factions.INDEPENDENT);
                        fb.setFleetType(FleetTypes.TASK_FORCE);
                        fb.setFleetName("Mercenary Fleet");
                        fb.setMinFP(50 + (int)(playerFleet.getFleetPoints() * 1.15f));
                        fb.setTransponderOn(true);
                        fb.setFlagshipVariant("retribution_Standard");
                        fb.setFlagshipName("Aria's Hammer");
                        fb.setFlagshipAlwaysRecoverable(true);
                        fb.setQualityOverride(1.25f);

                        CampaignFleetAPI fleet = fb.create();
                        fleet.getMemoryWithoutUpdate().set(IcfbMemFlags.IS_JR_MERCENARY_FLEET, true);
                        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_PREVENT_DISENGAGE, true);
                        fleet.getMemoryWithoutUpdate().set(MemFlags.STORY_CRITICAL, true);
                        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NEVER_AVOID_PLAYER_SLOWLY, true);
                        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PURSUE_PLAYER, true);
                        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT, true);
                        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_ALWAYS_PURSUE, true);
                        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_IGNORED_BY_OTHER_FLEETS, true);
                        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_DO_NOT_IGNORE_PLAYER, true);
                        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_SAW_PLAYER_WITH_TRANSPONDER_ON, true);
                        return fleet;
                    }

                    private boolean shouldDespawnMercenaryFleet() {
                        if (_mercenaryFleet == null)
                            return false;
                        if (_mercenaryFleet.getMemoryWithoutUpdate().getBoolean(MemFlags.MEMORY_KEY_RECENTLY_DEFEATED_BY_PLAYER)) {
                            if (_mercenaryFleet.getCommander() != IcfbPeople.ARIA_SEROKA_KOTOV.getPerson()) {
                                Global.getSector().getMemoryWithoutUpdate().set(IcfbMemFlags.KILLED_MERCENARY, true);
                            }
                            return true;
                        }
                        if (Global.getSector().getMemoryWithoutUpdate().getBoolean(IcfbMemFlags.BRIBED_MERCENARY))
                            return true;
                        if (Global.getSector().getMemoryWithoutUpdate().getBoolean(IcfbMemFlags.LET_MERCENARY_SCAN_XENT_DEVICE))
                            return true;
                        return false;
                    }

                    private void despawnMercenaryFleet() {
                        if (_mercenaryFleet == null || _despawnedMercenaryFleet)
                            return;

                        _mercenaryFleet.getMemoryWithoutUpdate().unset(IcfbMemFlags.IS_JR_MERCENARY_FLEET);
                        _mercenaryFleet.getMemoryWithoutUpdate().unset(MemFlags.MEMORY_KEY_MAKE_PREVENT_DISENGAGE);
                        _mercenaryFleet.getMemoryWithoutUpdate().unset(MemFlags.STORY_CRITICAL);
                        _mercenaryFleet.getMemoryWithoutUpdate().unset(MemFlags.MEMORY_KEY_NEVER_AVOID_PLAYER_SLOWLY);
                        _mercenaryFleet.getMemoryWithoutUpdate().unset(MemFlags.MEMORY_KEY_PURSUE_PLAYER);
                        _mercenaryFleet.getMemoryWithoutUpdate().unset(MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT);
                        _mercenaryFleet.getMemoryWithoutUpdate().unset(MemFlags.MEMORY_KEY_MAKE_ALWAYS_PURSUE);
                        _mercenaryFleet.getMemoryWithoutUpdate().unset(MemFlags.FLEET_DO_NOT_IGNORE_PLAYER);
                        Misc.makeUnimportant(_mercenaryFleet, ID);

                        _mercenaryFleet.clearAssignments();
                        _mercenaryFleet.addAssignment(
                                FleetAssignment.GO_TO_LOCATION_AND_DESPAWN,
                                IcfbEntities.WINGS_OF_ENTERIA.getEntity(),
                                999999
                        );

                        _despawnedMercenaryFleet = true;
                    }

                    private void updateMercenaryFleet() {
                        if (_mercenaryFleet == null)
                            return;
                        if (shouldDespawnMercenaryFleet())
                            despawnMercenaryFleet();
                        if (_mercenaryFleet.isExpired() || _mercenaryFleet.getContainingLocation() == null)
                            _mercenaryFleet = null;
                    }

                    private boolean shouldSpawnMercenaryFleet() {
                        if (_alreadySpawnedMercenaryFleet)
                            return false;
                        final CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
                        if (playerFleet == null)
                            return false;
                        final int minObjectiveProgress = 1;
                        final boolean wasInHyperspaceRecently = _isInHyperspace;

                        _isInHyperspace = playerFleet.isInHyperspace();

                        if (minObjectiveProgress > SpecialMission.getObjectiveProgress())
                            return false;
                        if (!wasInHyperspaceRecently)
                            return false;
                        if (_isInHyperspace)
                            return false;

                        boolean systemContainsObjective = false;
                        List<SectorEntityToken> objects = SpecialMission.getPotentialObjectsInSystem(playerFleet.getStarSystem());
                        for (SectorEntityToken object : objects) {
                            if (!SpecialMission.isObjectScanned(object)) {
                                systemContainsObjective = true;
                                break;
                            }
                        }
                        return systemContainsObjective;
                    }

                    private void checkSensorBursts() {
                        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
                        if (playerFleet == null) return;
                        AbilityPlugin ability = playerFleet.getAbility(Abilities.SENSOR_BURST);
                        if (ability == null) return;

                        if (ability.isUsable() || ability.getLevel() <= 0) {
                            _canCheckSB = true;
                        }

                        if (_canCheckSB && !playerFleet.isInHyperspace() && ability.isInProgress() && ability.getLevel() > 0.9f) {
                            final float minDistance = 800;
                            int scannedObjects = 0;
                            List<SectorEntityToken> objects = SpecialMission.getPotentialObjectsInSystem(playerFleet.getStarSystem());
                            for (SectorEntityToken object : objects) {
                                if (Misc.getDistance(object, playerFleet) > minDistance + (object.getRadius() * 1.5f))
                                    continue;
                                if (SpecialMission.isObjectScanned(object)) {
                                    Global.getSector().getCampaignUI().getMessageDisplay().addMessage(
                                            object.getName() + ": Already scanned for Xent's sensor package.",
                                            Misc.getNegativeHighlightColor()
                                    );
                                    continue;
                                }
                                SpecialMission.setObjectScanned(object, true);
                                scannedObjects++;
                            }
                            if (scannedObjects > 0) {
                                SpecialMission.addObjectiveProgress(scannedObjects);
                                getQuestStep().intel.showUpdate();
                            }
                            _canCheckSB = false;
                        }
                    }
                }
        );

        quest.addStep(
                new BaseQuestStepIntel() {

                    @Override
                    public void addNotificationBody(TooltipMakerAPI info) {
                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_notifBody_30"),
                                0
                        );
                    }

                    @Override
                    public void addNotificationBulletPoints(TooltipMakerAPI info) {
                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_bulletPoint_30"),
                                0,
                                xent.getFaction().getBaseUIColor(),
                                xent.getName().getFullName()
                        );
                    }

                    @Override
                    public void addDescriptionBody(TooltipMakerAPI info) {
                        final float width = getBodyPanelWidth();
                        ShmoGuiUtils.addPersonDetails(info, xent, width, 0, true);

                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_descBody_10"),
                                10,
                                xent.getMarket().getTextColorForFactionOrPlanet(),
                                xent.getMarket().getName()
                        );

                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_descBody_11"),
                                10,
                                IcfbFactions.BOUNDLESS.getFaction().getBaseUIColor(),
                                "the Boundless"
                        );

                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_descBody_30"),
                                10
                        );

                        info.addPara(
                                IcfbMisc.getQuestIntelString("jumpstartRequired_descBody_31"),
                                10,
                                xent.getFaction().getBaseUIColor(),
                                xent.getName().getFullName()
                        );
                    }

                    @Override
                    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui, QuestStepIntelPlugin plugin) {

                    }

                    @Override
                    public void addDescriptionBulletPoints(TooltipMakerAPI info) {

                    }

                    @Override
                    public SectorEntityToken getMapLocation(SectorMapAPI map) {
                        return xent.getMarket().getPrimaryEntity();
                    }

                    @Override
                    public List<IntelInfoPlugin.ArrowData> getArrowData(SectorMapAPI map) {
                        return null;
                    }
                },

                new BaseQuestStepScript() {
                    @Override
                    public void start() {
                        Misc.makeImportant(xent, ID);
                        Misc.makeImportant(xent.getMarket().getPrimaryEntity(), ID);
                        Global.getSector().getMemoryWithoutUpdate().set(IcfbMemFlags.XENT_SPECIAL_MISSION_COMPLETE, true);
                    }

                    @Override
                    public void advance(float deltaTime) {

                    }

                    @Override
                    public void end() {
                        Misc.makeUnimportant(xent, ID);
                        Misc.makeUnimportant(xent.getMarket().getPrimaryEntity(), ID);
                        Global.getSector().getMemoryWithoutUpdate().unset(IcfbMemFlags.XENT_SPECIAL_MISSION_COMPLETE);
                        Global.getSector().getMemoryWithoutUpdate().set(IcfbMemFlags.JUMPSTART_REQUIRED_COMPLETE, true);
                        BaseMissionHub.set(xent, null);
                        xent.getMarket().getCommDirectory().getEntryForPerson(xent).setHidden(true);
                    }

                    @Override
                    public boolean isComplete() {
                        return false;
                    }
                }
        );

        quest.addFinalStep();

        return quest;
    }

    public static class SpecialMission {
        public static final String OBJECTIVE_KEY = "$icfbJRNumberOfObjectsScanned";
        public static final String ALREADY_SCANNED_KEY = "$icfbJRAlreadyScanned";
        public static final int OBJECTIVE_GOAL = 5;

        public static int getObjectiveProgress() {
            return Global.getSector().getMemoryWithoutUpdate().getInt(OBJECTIVE_KEY);
        }

        public static void setObjectiveProgress(int value) {
            Global.getSector().getMemoryWithoutUpdate().set(OBJECTIVE_KEY, value);
        }

        public static void addObjectiveProgress(int value) {
            setObjectiveProgress(getObjectiveProgress() + value);
        }

        @SuppressWarnings("unchecked")
        public static List<SectorEntityToken> getPotentialObjectsInSystem(StarSystemAPI system) {
            List<SectorEntityToken> result = new ArrayList<>();
            List<PlanetAPI> planets = system.getEntities(PlanetAPI.class);
            for (PlanetAPI planet : planets) {
                if (planet.getTypeId().equals(StarTypes.BLACK_HOLE) ||
                        planet.getTypeId().equals(StarTypes.NEUTRON_STAR) ||
                        planet.getTypeId().equals(StarTypes.BLUE_SUPERGIANT) ||
                        planet.getTypeId().equals(StarTypes.WHITE_DWARF)
                ) {
                    result.add(planet);
                }
            }
            return result;
        }

        public static boolean isObjectScanned(SectorEntityToken objective) {
            return objective.getMemoryWithoutUpdate().getBoolean(ALREADY_SCANNED_KEY);
        }

        public static void setObjectScanned(SectorEntityToken objective, boolean scanned) {
            objective.getMemoryWithoutUpdate().set(ALREADY_SCANNED_KEY, scanned);
        }
    }

    public static class XentMissionHub extends BaseMissionHub {
        public XentMissionHub(PersonAPI person) {
            super(person);
        }
    }
}
