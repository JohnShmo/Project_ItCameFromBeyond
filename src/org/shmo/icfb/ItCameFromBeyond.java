package org.shmo.icfb;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.shmo.icfb.campaign.abilities.ShiftDrive;
import org.shmo.icfb.campaign.abilities.ShiftDrive_AbilityPlugin;
import org.shmo.icfb.campaign.scripts.ShiftDriveTracker;

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

        public static ShiftDrive_AbilityPlugin getPlayerShiftDrivePlugin() {
            return ShiftDrive_AbilityPlugin.getPlayerInstance();
        }

        public static ShiftDrive getPlayerShiftDrive() {
            ShiftDrive_AbilityPlugin plugin = getPlayerShiftDrivePlugin();
            if (plugin == null)
                return null;
            return plugin.getImpl();
        }

        public static ShiftDriveTracker getShiftDriveTracker() {
            return ShiftDriveTracker.getInstance();
        }
    }

    public static class Utils {
        public static float lerp(float a, float b, float t) {
            return a + t * (b - a);
        }
    }

}
