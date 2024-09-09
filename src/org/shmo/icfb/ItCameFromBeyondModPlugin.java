package org.shmo.icfb;

import com.fs.starfarer.api.BaseModPlugin;
import org.magiclib.util.MagicSettings;
import org.shmo.icfb.campaign.quests.impl.TestQuest;
import org.shmo.icfb.campaign.quests.impl.factories.TestQuestFactory;
import org.shmo.icfb.campaign.scripts.QuestManager;
import org.shmo.icfb.campaign.scripts.ShiftDriveManager;
import org.shmo.icfb.utilities.ScriptInitializer;
import org.json.JSONObject;

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

        if (!QuestManager.getInstance().contains(TestQuest.ID)) {
            QuestManager.getInstance().add(new TestQuestFactory());
        }
    }

    @Override
    public void onApplicationLoad() throws Exception {
        super.onApplicationLoad();
        ItCameFromBeyond.Log.info("Loading plugin...");
        loadSettings();
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
    public void onDevModeF8Reload() {
        super.onDevModeF8Reload();
        loadSettings();
    }

    @Override
    public void onNewGameAfterTimePass() {
        super.onNewGameAfterTimePass();
    }

    private void loadSettings() {
        MagicSettings.loadModSettings();
        JSONObject json = MagicSettings.modSettings.optJSONObject("ItCameFromBeyond");
        if (json == null)
            return;
        ItCameFromBeyond.Global.getSettings().loadFromJSON(json);
    }
}
