package org.shmo.icfb.campaign.quests.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.shmo.icfb.campaign.ids.ItCameFromBeyondPeople;
import org.shmo.icfb.campaign.quests.Quest;
import org.shmo.icfb.campaign.quests.factories.QuestFactory;
import org.shmo.icfb.campaign.quests.intel.BaseQuestStepIntel;
import org.shmo.icfb.campaign.quests.scripts.BaseQuestStepScript;

import java.util.List;

public class JumpstartRequiredQuest implements QuestFactory {
    public static final String ID = "jumpstart_required";
    public static final String NAME = "Jumpstart Required";

    @Override
    public Quest create() {
        final Quest quest = new Quest(ID);
        quest.setName(NAME);
        quest.setIcon(Global.getSettings().getSpriteName("icfb_portraits", "shifter_01_static"));

        final PersonAPI xent = ItCameFromBeyondPeople.XentAlabaster.getInstance();

        quest.addStep(
                new BaseQuestStepIntel() {
                    @Override
                    public void addNotificationBody(TooltipMakerAPI info) {
                        info.addPara(
                                "Transmission from: %s.",
                                0,
                                Misc.getHighlightColor(),
                                xent.getName().getFullName()
                        );
                    }

                    @Override
                    public void addNotificationBulletPoints(TooltipMakerAPI info) {
                    }

                    @Override
                    public void addDescriptionBody(TooltipMakerAPI info) {
                        info.addPara(
                                "You received a transmission from an agent located at %s." +
                                        " His name is %s, and he seems rather eager to speak with you about something.",
                                10,
                                Misc.getHighlightColor(),
                                xent.getMarket().getName(),
                                xent.getName().getFullName()
                        );
                    }

                    @Override
                    public void addDescriptionBulletPoints(TooltipMakerAPI info) {
                        info.addPara("Talk to %s.", 10, Misc.getHighlightColor(), xent.getName().getFullName());
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
