package org.shmo.icfb.campaign.quests.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.input.Keyboard;
import org.shmo.icfb.ItCameFromBeyond;
import org.shmo.icfb.campaign.IcfbPeople;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;
import org.shmo.icfb.campaign.quests.intel.BaseQuestStepIntel;
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

        final PersonAPI xent = IcfbPeople.XentAlabaster.getPerson();

        quest.addStep(
                new BaseQuestStepIntel() {
                    private transient boolean _showTransmission = false;

                    @Override
                    public void addNotificationBody(TooltipMakerAPI info) {
                        info.addPara(
                                ItCameFromBeyond.Misc.getQuestIntelString("jumpstartRequired_notifBody_00"),
                                0,
                                Misc.getTextColor(),
                                xent.getName().getFullName()
                        );
                    }

                    @Override
                    public void addNotificationBulletPoints(TooltipMakerAPI info) {
                        info.addPara(
                                ItCameFromBeyond.Misc.getQuestIntelString("jumpstartRequired_bulletPoint_00"),
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
                                ItCameFromBeyond.Misc.getQuestIntelString("jumpstartRequired_descBody_00"),
                                10,
                                xent.getMarket().getTextColorForFactionOrPlanet(),
                                xent.getMarket().getName()
                        );

                        ButtonAPI button = ShmoGuiUtils.addGenericButton(
                                info,
                                width,
                                (!_showTransmission) ?
                                    ItCameFromBeyond.Misc.getQuestIntelString("jumpstartRequired_button_00") :
                                    ItCameFromBeyond.Misc.getQuestIntelString("jumpstartRequired_button_01"),
                                SHOW_OR_HIDE_MESSAGE_BUTTON
                        );
                        button.setShortcut(Keyboard.KEY_T, true);

                        if (!_showTransmission)
                            return;

                        LabelAPI message = info.addPara(
                                ItCameFromBeyond.Misc.getQuestIntelString("jumpstartRequired_transmissionMessage"),
                                10,
                                Misc.getTextColor(),
                                Global.getSector().getPlayerPerson().getName().getFullName(),
                                xent.getName().getFullName()
                        );
                        message.italicize();
                    }

                    @Override
                    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
                        if (buttonId.equals(SHOW_OR_HIDE_MESSAGE_BUTTON)) {
                            _showTransmission = !_showTransmission;
                            ui.recreateIntelUI();
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
