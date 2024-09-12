package org.shmo.icfb;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.shmo.icfb.campaign.abilities.ShiftJump;
import org.shmo.icfb.campaign.abilities.ShiftJumpAbilityPlugin;
import org.shmo.icfb.campaign.intel.events.ShiftDriveEvent;
import org.shmo.icfb.campaign.scripts.QuestManager;
import org.shmo.icfb.campaign.scripts.ShiftDriveManager;
import org.shmo.icfb.utilities.ShmoMath;

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
        public static ShiftJumpAbilityPlugin getPlayerShiftJumpPlugin() {
            return ShiftJumpAbilityPlugin.getPlayerInstance();
        }

        public static ShiftJump getPlayerShiftJump() {
            ShiftJumpAbilityPlugin plugin = getPlayerShiftJumpPlugin();
            if (plugin == null)
                return null;
            return plugin.getImpl();
        }

        public static ItCameFromBeyondSettings getSettings() {
            return ItCameFromBeyondModPlugin.getInstance().getSettings();
        }
    }

    public static class Misc {
        public static float computeShiftJumpCRPenalty(
                ItCameFromBeyondSettings.ShiftJumpSettings.CRPenaltyCurve curve,
                float t
        ) {
            if (curve == null)
                return t;
            switch (curve) {
                case FAST: return ShmoMath.easeInQuad(t);
                case MEDIUM: return ShmoMath.easeInQuart(t);
                case SLOW: return ShmoMath.easeInExpo(t);
                default: return t;
            }
        }
    }
}
