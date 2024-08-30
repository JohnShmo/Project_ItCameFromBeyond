package org.shmo.icfb.utilities;

import com.fs.starfarer.api.EveryFrameScript;

public interface ScriptFactory {
    EveryFrameScript createOrGetInstance();
}
