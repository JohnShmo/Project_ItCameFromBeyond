package org.shmo.icfb;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ItCameFromBeyond {
    public static class Log {
        private static final Logger LOGGER = LogManager.getLogger("[It Came From Beyond]");

        public static void debug(Object message) {
            LOGGER.debug(message);
        }

        public static void info(Object message) {
            LOGGER.info(message);
        }

        public static void error(Object message) {
            LOGGER.error(message);
        }

        public static void fatal(Object message) {
            LOGGER.fatal(message);
        }
    }

    public static ItCameFromBeyond_ModPlugin getPlugin() {
        return ItCameFromBeyond_ModPlugin.getInstance();
    }

}
