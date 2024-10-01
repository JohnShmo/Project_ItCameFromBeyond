package org.shmo.icfb.campaign.quests.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.campaign.generation.entities.WingsOfEnteria;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondEntities;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondMarkets;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondMemKeys;
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
                              info.addPara("Return the device.", 0);
                          }

                          @Override
                          public void addNotificationBulletPoints(TooltipMakerAPI info) {

                          }

                          @Override
                          public void addDescriptionBody(TooltipMakerAPI info) {
                                info.addPara("During your travels, you encountered a ship with a strange" +
                                        " device onboard. An anonymously-registered bounty encourages you to return" +
                                        " it to its rightful owner, whoever that is.",
                                        10
                                );
                          }

                          @Override
                          public void addDescriptionBulletPoints(TooltipMakerAPI info) {
                              info.addPara(
                                      "Ask a %s official about the strange device you found aboard"
                                              + " the %s.",
                                      10,
                                      Misc.getHighlightColor(),
                                      "Boundless",
                                      "Chariot of Hope"
                              );
                          }

                          @Override
                          public SectorEntityToken getMapLocation(SectorMapAPI map) {
                              return WingsOfEnteria.getContainingSystem().getEntityById(ItCameFromBeyondEntities.WINGS_OF_ENTERIA);
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
                                WingsOfEnteria.getContainingSystem().getEntityById(ItCameFromBeyondEntities.WINGS_OF_ENTERIA);
                        Misc.makeImportant(_objectiveEntity, ID);
                        _personEntity =
                                Global.getSector().getEconomy().getMarket(ItCameFromBeyondMarkets.WINGS_OF_ENTERIA).getAdmin();
                        Misc.makeImportant(_personEntity, ID);
                        _personEntity.getMemoryWithoutUpdate().set(ItCameFromBeyondMemKeys.IS_BOUNDLESS_OFFICIAL_FOR_CHARIOT_QUEST, true);
                    }

                    @Override
                    public void advance(float deltaTime) {

                    }

                    @Override
                    public void end() {
                        Misc.makeUnimportant(_objectiveEntity, ID);
                        Misc.makeUnimportant(_personEntity, ID);
                        _personEntity.getMemoryWithoutUpdate().unset(ItCameFromBeyondMemKeys.IS_BOUNDLESS_OFFICIAL_FOR_CHARIOT_QUEST);
                        Global.getSector().getMemoryWithoutUpdate().set(ItCameFromBeyondMemKeys.IS_AWAITING_AGENT_CONTACT, true);
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
