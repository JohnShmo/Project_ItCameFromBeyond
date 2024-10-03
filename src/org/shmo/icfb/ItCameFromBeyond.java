package org.shmo.icfb;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.shmo.icfb.campaign.abilities.ShiftJump;
import org.shmo.icfb.campaign.abilities.ShiftJumpAbilityPlugin;
import org.shmo.icfb.utilities.ShmoMath;

import java.awt.*;

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

        public static String getQuestIntelString(String id) {
            return com.fs.starfarer.api.Global.getSettings().getString(
                    "icfb_questIntel",
                    id
            );
        }

        public static void tooltipAddPersonDetails(TooltipMakerAPI info, PersonAPI person, float width, float pad, boolean showName) {
            final float imageHeight = 128;
            final float barWidth = 128 + 10;
            info.addImage(person.getPortraitSprite(), width, imageHeight,pad);
            if (showName) {
                info.addPara(person.getName().getFullName(), person.getFaction().getBaseUIColor(), 4).setAlignment(Alignment.MID);
            }
            info.addRelationshipBar(person, barWidth,0);
            UIComponentAPI prev = info.getPrev();
            prev.getPosition().setXAlignOffset((width / 2f) - (barWidth / 2f));
            info.addSpacer(0f);
            info.getPrev().getPosition().setXAlignOffset(-((width / 2f) - (barWidth / 2f)));
        }

        public static ButtonAPI tooltipAddGenericButton(TooltipMakerAPI info, float width, Color tc, Color bg, String text, Object data) {
            float opad = 10f;
            return info.addButton(text, data, tc, bg,
                    (int)(width), 20f, opad * 2f);
        }

        public static ButtonAPI tooltipAddGenericButton(TooltipMakerAPI info, float width, String text, Object data) {
            return tooltipAddGenericButton(
                    info,
                    width,
                    com.fs.starfarer.api.Global.getSector().getPlayerFaction(),
                    text,
                    data
            );
        }

        public static ButtonAPI tooltipAddGenericButton(TooltipMakerAPI info, float width, FactionAPI faction, String text, Object data) {
            return tooltipAddGenericButton(
                    info,
                    width,
                    faction.getBaseUIColor(),
                    faction.getDarkUIColor(),
                    text,
                    data
            );
        }
    }
}
