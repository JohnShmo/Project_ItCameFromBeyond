package org.shmo.icfb;

import com.fs.starfarer.api.BaseModPlugin;
import org.shmo.icfb.campaign.quests.QuestId;
import org.shmo.icfb.campaign.quests.impl.OddOccurrences;
import org.shmo.icfb.campaign.scripts.QuestManager;
import org.shmo.icfb.campaign.scripts.ShiftDriveManager;
import org.shmo.icfb.utilities.ScriptInitializer;

public class ItCameFromBeyondModPlugin extends BaseModPlugin {

    private void initializeScripts() {
        ItCameFromBeyond.Log.info("" +
                "\n#######################\n" +
                "\nINITIALIZING SCRIPTS...\n" +
                "\n#######################\n"
        );

        ScriptInitializer.initializeScript(new ShiftDriveManager.Factory());
        ScriptInitializer.initializeScript(new QuestManager.Factory());

        ItCameFromBeyond.Log.info("" +
                "\n#######################\n" +
                "\nINITIALIZING SCRIPTS COMPLETE!\n" +
                "\n#######################\n"
        );

        // QUEST TESTS =====================================================

        if (!QuestManager.getInstance().contains(QuestId.ODD_OCCURRENCES)) {
            OddOccurrences.start();
        }
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

    @Override
    public void onNewGameAfterTimePass() {
        super.onNewGameAfterTimePass();
    }
}
