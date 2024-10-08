package org.shmo.icfb.campaign.quests.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.intel.contacts.ContactIntel;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.input.Keyboard;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.IcfbFactions;
import org.shmo.icfb.campaign.IcfbMemFlags;
import org.shmo.icfb.campaign.IcfbPeople;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;
import org.shmo.icfb.campaign.quests.intel.BaseQuestStepIntel;
import org.shmo.icfb.campaign.quests.intel.QuestStepIntelPlugin;
import org.shmo.icfb.campaign.quests.scripts.BaseQuestStepScript;
import org.shmo.icfb.utilities.ShmoGuiUtils;

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
                                xent.getRelToPlayer().isAtWorst(RepLevel.FRIENDLY) &&
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
                    }

                    @Override
                    public boolean isComplete() {
                        return false;
                    }
                }
        );

        return quest;
    }
}
