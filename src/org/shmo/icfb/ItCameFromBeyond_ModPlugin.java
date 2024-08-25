package org.shmo.icfb;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import org.shmo.icfb.campaign.scripts.ShiftDriveManager;

public class ItCameFromBeyond_ModPlugin extends BaseModPlugin {
    private void addScripts() {
        Global.getSector().removeScriptsOfClass(ShiftDriveManager.class);
        ShiftDriveManager shiftDriveManager = ShiftDriveManager.getInstance();
        if (shiftDriveManager == null) {
            shiftDriveManager = new ShiftDriveManager();
        }
        Global.getSector().addScript(shiftDriveManager);
    }

    @Override
    public void onApplicationLoad() throws Exception {
        super.onApplicationLoad();
        ItCameFromBeyond.Log.info("Loading plugin...");
        ItCameFromBeyond.Log.info("Plugin loaded successfully!");
    }

    @Override
    public void onGameLoad(boolean newGame) {
        super.onGameLoad(newGame);
        addScripts();
    }

    @Override
    public void onNewGame() {
        super.onNewGame();
    }
}
