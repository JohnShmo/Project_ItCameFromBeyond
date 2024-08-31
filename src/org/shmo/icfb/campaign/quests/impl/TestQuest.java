package org.shmo.icfb.campaign.quests.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;

public class TestQuest {
    public static final String ID = "test";
    public static final String NAME = "Test Quest";

    public static SectorEntityToken getPirateSpawnLocation() {
        return Global.getSector().getStarSystem("Penelope's Star").getPlanets().get(2);
    }
}
