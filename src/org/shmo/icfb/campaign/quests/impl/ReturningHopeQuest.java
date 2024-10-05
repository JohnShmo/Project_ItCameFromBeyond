package org.shmo.icfb.campaign.quests.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.IcfbMisc;
import org.shmo.icfb.campaign.IcfbEntities;
import org.shmo.icfb.campaign.IcfbFactions;
import org.shmo.icfb.campaign.IcfbMarkets;
import org.shmo.icfb.campaign.IcfbMemFlags;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;
import org.shmo.icfb.campaign.quests.intel.BaseQuestStepIntel;
import org.shmo.icfb.campaign.quests.scripts.BaseQuestStepScript;
import org.shmo.icfb.campaign.scripts.temp.AgentContactTimer;

import java.util.List;

public class ReturningHopeQuest implements QuestFactory {

    public static final String ID = "returning_hope";
    public static final String NAME = "Returning Hope";

    @Override
    public Quest create() {
        Quest quest = new Quest(ID);
        quest.setName(NAME);
        quest.setIcon(Global.getSettings().getSpriteName("campaignMissions", "analyze_entity"));
        quest.addStep(new BaseQuestStepIntel() {
                          @Override
                          public void addNotificationBody(TooltipMakerAPI info) {

                          }

                          @Override
                          public void addNotificationBulletPoints(TooltipMakerAPI info) {
                              info.addPara(
                                      IcfbMisc.getQuestIntelString("returningHope_bulletPoint_00"),
                                      0,
                                      IcfbFactions.BOUNDLESS.getFaction().getBaseUIColor(),
                                      "Boundless"
                              );
                          }

                          @Override
                          public void addDescriptionBody(TooltipMakerAPI info) {
                                info.addPara(
                                        IcfbMisc.getQuestIntelString("returningHope_descBody_00"),
                                        0
                                );
                          }

                          @Override
                          public void addDescriptionBulletPoints(TooltipMakerAPI info) {

                          }

                          @Override
                          public SectorEntityToken getMapLocation(SectorMapAPI map) {
                              return IcfbEntities.WINGS_OF_ENTERIA.getEntity();
                          }

                          @Override
                          public List<IntelInfoPlugin.ArrowData> getArrowData(SectorMapAPI map) {
                              return null;
                          }
                      },
                new BaseQuestStepScript() {
                    private SectorEntityToken _objectiveEntity = null;
                    private PersonAPI _personEntity = null;

                    @Override
                    public void start() {
                        _objectiveEntity =
                                IcfbEntities.WINGS_OF_ENTERIA.getEntity();
                        Misc.makeImportant(_objectiveEntity, ID);
                        _personEntity =
                                IcfbMarkets.WINGS_OF_ENTERIA.getMarket().getAdmin();
                        Misc.makeImportant(_personEntity, ID);
                        _personEntity.getMemoryWithoutUpdate().set(IcfbMemFlags.IS_BOUNDLESS_OFFICIAL_FOR_CHARIOT_QUEST, true);
                    }

                    @Override
                    public void advance(float deltaTime) {

                    }

                    @Override
                    public void end() {
                        Misc.makeUnimportant(_objectiveEntity, ID);
                        Misc.makeUnimportant(_personEntity, ID);
                        _personEntity.getMemoryWithoutUpdate().unset(IcfbMemFlags.IS_BOUNDLESS_OFFICIAL_FOR_CHARIOT_QUEST);
                        Global.getSector().getMemoryWithoutUpdate().set(IcfbMemFlags.IS_AWAITING_AGENT_CONTACT, true);
                        Global.getSector().addScript(new AgentContactTimer(10));
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
