package org.shmo.icfb.utilities;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import org.shmo.icfb.ItCameFromBeyond;

public class ScriptInitializer {
    public static <T extends ScriptFactory> void initializeScript(T factory) {
        EveryFrameScript script = factory.createOrGetInstance();
        Global.getSector().removeScriptsOfClass(script.getClass());
        Global.getSector().addScript(script);
        ItCameFromBeyond.Log.info("Initialized script: " + script.getClass().getName());
    }
}
