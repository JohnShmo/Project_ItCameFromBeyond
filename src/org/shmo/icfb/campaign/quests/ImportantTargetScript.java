package org.shmo.icfb.campaign.quests;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.util.Misc;

public class ImportantTargetScript extends BaseQuestStepScript {

    private SectorEntityToken _target = null;

    public void setTarget(SectorEntityToken target) {
        _target = target;
    }

    public SectorEntityToken getTarget() {
        return _target;
    }

    @Override
    public void start() {
        if (getTarget() != null) {
            Misc.makeImportant(getTarget(), getQuestStep().quest.getName());
        }
    }

    @Override
    public void advance(float deltaTime) {

    }

    @Override
    public void end() {
        if (getTarget() != null) {
            Misc.makeUnimportant(getTarget(), getQuestStep().quest.getName());
        }
    }

    @Override
    public boolean isComplete() {
        return getTarget() == null;
    }
}
