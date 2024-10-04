package org.shmo.icfb.utilities;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

import java.awt.*;

public class ShmoGuiUtils {
    public static void addPersonDetails(TooltipMakerAPI info, PersonAPI person, float width, float pad, boolean showName) {
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

    public static ButtonAPI addGenericButton(TooltipMakerAPI info, float width, Color tc, Color bg, String text, Object data) {
        float opad = 10f;
        return info.addButton(text, data, tc, bg,
                (int)(width), 20f, opad * 2f);
    }

    public static ButtonAPI addGenericButton(TooltipMakerAPI info, float width, String text, Object data) {
        return addGenericButton(
                info,
                width,
                com.fs.starfarer.api.Global.getSector().getPlayerFaction(),
                text,
                data
        );
    }

    public static ButtonAPI addGenericButton(TooltipMakerAPI info, float width, FactionAPI faction, String text, Object data) {
        return addGenericButton(
                info,
                width,
                faction.getBaseUIColor(),
                faction.getDarkUIColor(),
                text,
                data
        );
    }
}
