package org.shmo.icfb.factories;

import com.fs.starfarer.api.EveryFrameScript;

public interface ScriptFactory {
    EveryFrameScript createOrGetInstance();
}
