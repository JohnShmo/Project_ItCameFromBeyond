package org.shmo.icfb;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.shmo.icfb.campaign.abilities.ShiftJump;
import org.shmo.icfb.campaign.abilities.ShiftJump_AbilityPlugin;
import org.shmo.icfb.campaign.scripts.ShiftDriveManager;

public class ItCameFromBeyond {
    public static class Log {
        private static final Logger LOGGER = LogManager.getLogger("[It Came From Beyond]");

        public static void debug(Object message) {
            LOGGER.debug(message);
        }

        public static void info(Object message) {
            LOGGER.info(message);
        }

        public static void warn(Object message) {
            LOGGER.warn(message);
        }

        public static void error(Object message) {
            LOGGER.error(message);
        }

        public static void fatal(Object message) {
            LOGGER.fatal(message);
        }
    }

    public static class Global {
        public static ItCameFromBeyond_ModPlugin getPlugin() {
            return ItCameFromBeyond_ModPlugin.getInstance();
        }

        public static ShiftJump_AbilityPlugin getPlayerShiftJumpPlugin() {
            return ShiftJump_AbilityPlugin.getPlayerInstance();
        }

        public static ShiftJump getPlayerShiftJump() {
            ShiftJump_AbilityPlugin plugin = getPlayerShiftJumpPlugin();
            if (plugin == null)
                return null;
            return plugin.getImpl();
        }

        public static ShiftDriveManager getShiftDriveManager() {
            return ShiftDriveManager.getInstance();
        }
    }

    public static class Utils {
        public static float lerp(float a, float b, float t) {
            return a + t * (b - a);
        }
    }

}
