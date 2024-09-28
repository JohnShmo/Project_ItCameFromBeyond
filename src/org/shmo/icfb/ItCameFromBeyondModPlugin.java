package org.shmo.icfb;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import exerelin.campaign.SectorManager;
import org.magiclib.util.MagicSettings;
import org.shmo.icfb.campaign.scripts.QuestManager;
import org.shmo.icfb.campaign.scripts.ShiftDriveManager;
import org.shmo.icfb.utilities.ScriptInitializer;
import org.json.JSONObject;

public class ItCameFromBeyondModPlugin extends BaseModPlugin {

    private static ItCameFromBeyondModPlugin INSTANCE;
    public static ItCameFromBeyondModPlugin getInstance() {
        return INSTANCE;
    }

    private ItCameFromBeyondSettings _settings;
    public ItCameFromBeyondSettings getSettings() {
        return _settings;
    }

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
    }

    @Override
    public void onApplicationLoad() throws Exception {
        INSTANCE = this;
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

        boolean isNexEnabled = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (!isNexEnabled || SectorManager.getManager().isCorvusMode()) {
            new ItCameFromBeyondGen().generate(Global.getSector());
        }
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
        _settings = new ItCameFromBeyondSettings();
        MagicSettings.loadModSettings();
        JSONObject json = MagicSettings.modSettings.optJSONObject("ItCameFromBeyond");
        if (json == null)
            return;
        _settings.loadFromJSON(json);
    }
}
