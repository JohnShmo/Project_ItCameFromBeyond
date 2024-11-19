package org.shmo.icfb;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class IcfbRenderMisc {
    // Totally didn't just copy-paste most of this from the Nexerelin mining bar renderer :^)
    public static void renderFleetSuspicion(CampaignFleetAPI fleet, String susLevelMemKey, ViewportAPI viewport) {
        if (fleet == null
                || fleet.isExpired()
                || !fleet.isInCurrentLocation()
                || !fleet.getMemoryWithoutUpdate().contains(susLevelMemKey)
                || !fleet.isVisibleToSensorsOf(Global.getSector().getPlayerFleet()))
            return;

        final int barWidth = 128;
        final int barHeight = 16;
        final int iconWidth = 32;
        final String iconCategory = "icfb_icons";
        final String iconId = "sus";
        final Color barColor = new Color(140, 28, 24, 255);
        final Color fillColor = new Color(189, 109, 80, 255);

        final float amount = fleet.getMemoryWithoutUpdate().getFloat(susLevelMemKey);
        if (amount <= 0)
            return;

        // Set OpenGL flags
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Vector2f location = fleet.getLocation();
        float x = viewport.convertWorldXtoScreenX(location.x);
        float y = viewport.convertWorldYtoScreenY(location.y - (fleet.getRadius() + barHeight * 3));
        float screenScale = Global.getSettings().getScreenScaleMult();
        x *= screenScale;
        y *= screenScale;

        int halfWidth = barWidth/2;
        int halfHeight = barHeight/2;
        int screenWidth = (int)Global.getSettings().getScreenWidth();
        int screenHeight = (int)Global.getSettings().getScreenHeight();

        GL11.glViewport(0, 0, screenWidth, screenHeight);
        GL11.glOrtho(0.0, screenWidth, 0.0, screenHeight, -1.0, 1.0);
        GL11.glLineWidth(2);
        GL11.glTranslatef(x, y, 0);

        float screenMult = 1/viewport.getViewMult();
        float diffFrom1 = screenMult - 1;
        screenMult -= diffFrom1/2;  // halve the distance of the multiplier from 1x
        GL11.glScalef(screenMult, screenMult, 1);
        GL11.glTranslatef((-halfWidth + iconWidth/2f) * screenScale, 0, 0);

        // bar fill
        int length = (int)(barWidth * amount);
        glSetColor(fillColor);
        GL11.glBegin(GL11.GL_POLYGON);
        GL11.glVertex2i(0, halfHeight);
        GL11.glVertex2i(length, halfHeight);
        GL11.glVertex2i(length, -halfHeight);
        GL11.glVertex2i(0, -halfHeight);
        GL11.glEnd();

        // bar outline
        glSetColor(barColor);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2i(0, halfHeight);
        GL11.glVertex2i(barWidth, halfHeight);
        GL11.glVertex2i(barWidth, -halfHeight);
        GL11.glVertex2i(0, -halfHeight);
        GL11.glEnd();

        // icon
        GL11.glColor4f(1, 1, 1, 1);
        SpriteAPI sprite = Global.getSettings().getSprite(iconCategory, iconId);
        float sizeMult = 32/sprite.getWidth();
        GL11.glScalef(sizeMult, sizeMult, 1);
        sprite.render(-iconWidth*2f, -iconWidth/2f - 2);

        // Finalize drawing
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static void glSetColor(Color color) {
        GL11.glColor4f(color.getRed()/255f, color.getGreen()/255f,
                color.getBlue()/255f, color.getAlpha()/255f);
    }
}
