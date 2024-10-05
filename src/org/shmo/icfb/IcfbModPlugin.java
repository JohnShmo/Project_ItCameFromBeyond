package org.shmo.icfb;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import exerelin.campaign.SectorManager;
import org.magiclib.util.MagicSettings;
import org.shmo.icfb.campaign.scripts.QuestManager;
import org.shmo.icfb.campaign.scripts.ShiftDriveManager;
import org.shmo.icfb.utilities.ScriptInitializer;
import org.json.JSONObject;

public class IcfbModPlugin extends BaseModPlugin {

    private static IcfbModPlugin INSTANCE;
    public static IcfbModPlugin getInstance() {
        return INSTANCE;
    }

    private IcfbSettings _settings;
    public IcfbSettings getSettings() {
        return _settings;
    }

    private void initializeScripts() {
        IcfbLog.info("" +
                "\n#######################\n" +
                "\nINITIALIZING SCRIPTS...\n" +
                "\n#######################\n"
        );

        ScriptInitializer.initializeScript(new ShiftDriveManager.Factory());
        ScriptInitializer.initializeScript(new QuestManager.Factory());

        IcfbLog.info("" +
                "\n#######################\n" +
                "\nINITIALIZING SCRIPTS COMPLETE!\n" +
                "\n#######################\n"
        );
    }

    @Override
    public void onApplicationLoad() throws Exception {
        INSTANCE = this;
        super.onApplicationLoad();
        IcfbLog.info("Loading plugin...");
        loadSettings();
        IcfbLog.info("Plugin loaded successfully!");
    }

    @Override
    public void onGameLoad(boolean newGame) {
        super.onGameLoad(newGame);
        initializeScripts();
        executeSectorGenIfNeeded();
    }

    @Override
    public void onNewGame() {
        super.onNewGame();
        executeSectorGenIfNeeded();
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
        _settings = new IcfbSettings();
        MagicSettings.loadModSettings();
        JSONObject json = MagicSettings.modSettings.optJSONObject("ItCameFromBeyond");
        if (json == null)
            return;
        _settings.loadFromJSON(json);
    }

    private void executeSectorGenIfNeeded() {
        boolean isNexEnabled = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (!isNexEnabled || SectorManager.getManager().isCorvusMode()) {
            if (!IcfbGen.hasAlreadyGeneratedForCorvus(Global.getSector()))
                IcfbGen.generateForCorvusMode(Global.getSector());
        }
    }
}
