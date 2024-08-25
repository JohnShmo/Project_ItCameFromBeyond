package org.shmo.icfb;

import com.fs.starfarer.api.BaseModPlugin;
import org.shmo.icfb.campaign.scripts.ShiftDriveManager;
import org.shmo.icfb.utilities.ScriptInitializer;

public class ItCameFromBeyond_ModPlugin extends BaseModPlugin {

    private void initializeScripts() {
        ItCameFromBeyond.Log.info("" +
                "\n#######################\n" +
                "\nINITIALIZING SCRIPTS...\n" +
                "\n#######################\n"
        );

        ScriptInitializer.initializeScript(new ShiftDriveManager.Factory());

        ItCameFromBeyond.Log.info("" +
                "\n#######################\n" +
                "\nINITIALIZING SCRIPTS COMPLETE!\n" +
                "\n#######################\n"
        );
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
        initializeScripts();
    }

    @Override
    public void onNewGame() {
        super.onNewGame();
    }
}
