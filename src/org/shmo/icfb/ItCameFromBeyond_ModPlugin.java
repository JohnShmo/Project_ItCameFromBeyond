package org.shmo.icfb;

import com.fs.starfarer.api.BaseModPlugin;

public class ItCameFromBeyond_ModPlugin extends BaseModPlugin {

    private static ItCameFromBeyond_ModPlugin INSTANCE = null;

    public static ItCameFromBeyond_ModPlugin getInstance() {
        return INSTANCE;
    }

    @Override
    public void onApplicationLoad() throws Exception {
        super.onApplicationLoad();
        INSTANCE = this;
        ItCameFromBeyond.Log.info("Loading plugin...");

        ItCameFromBeyond.Log.info("Plugin loaded successfully!");
    }
}
