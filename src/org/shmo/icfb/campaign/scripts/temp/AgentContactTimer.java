package org.shmo.icfb.campaign.scripts.temp;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.campaign.IcfbMemFlags;
import org.shmo.icfb.campaign.quests.impl.JumpstartRequiredQuest;
import org.shmo.icfb.campaign.scripts.IcfbQuestManager;

public class AgentContactTimer implements EveryFrameScript {
    private final long _start;
    private final float _days;
    private boolean _done;

    public AgentContactTimer(float days) {
        _start = Global.getSector().getClock().getTimestamp();
        _days = days;
        _done = false;
        IcfbLog.info("Agent timer started");
    }

    @Override
    public boolean isDone() {
        return _done;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        if (isDone())
            return;

        final float daysElapsed = Global.getSector().getClock().getElapsedDaysSince(_start);
        if (daysElapsed >= _days) {
            IcfbQuestManager.getInstance().add(new JumpstartRequiredQuest());
            IcfbLog.info("Agent timer finished");
            _done = true;
        }
    }
}
