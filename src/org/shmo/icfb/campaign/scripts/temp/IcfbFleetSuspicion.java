package org.shmo.icfb.campaign.scripts.temp;

import com.fs.starfarer.api.EveryFrameScriptWithCleanup;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.listeners.CampaignUIRenderingListener;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.loading.CampaignPingSpec;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class IcfbFleetSuspicion implements CampaignUIRenderingListener, EveryFrameScriptWithCleanup {

    public static final String FLEET_SUS_LEVEL_KEY = "$icfbSusLevel";
    public static final String FLEET_SUS_REF_KEY = "$icfbSusRef";

    private final CampaignFleetAPI _fleet;

    public static void addToFleet(CampaignFleetAPI fleet) {
        if (fleet == null || fleet.isExpired())
            return;
        if (fleet.getMemoryWithoutUpdate().contains(FLEET_SUS_REF_KEY))
            return;
        IcfbFleetSuspicion sus = new IcfbFleetSuspicion(fleet);
        fleet.getMemoryWithoutUpdate().set(FLEET_SUS_LEVEL_KEY, 0.0f);
        fleet.getMemoryWithoutUpdate().set(FLEET_SUS_REF_KEY, sus);
        fleet.addScript(sus);
        Global.getSector().getListenerManager().addListener(sus);
    }

    public static void removeFromFleet(CampaignFleetAPI fleet) {
        if (fleet == null)
            return;
        if (!fleet.getMemoryWithoutUpdate().contains(FLEET_SUS_REF_KEY))
            return;
        IcfbFleetSuspicion sus = (IcfbFleetSuspicion)fleet.getMemoryWithoutUpdate().get(FLEET_SUS_REF_KEY);
        fleet.getMemoryWithoutUpdate().unset(FLEET_SUS_REF_KEY);
        fleet.getMemoryWithoutUpdate().unset(FLEET_SUS_LEVEL_KEY);
        fleet.removeScript(sus);
        Global.getSector().getListenerManager().removeListener(sus);
    }

    private IcfbFleetSuspicion(CampaignFleetAPI fleet) {
        _fleet = fleet;
    }

    private static void spawnPing(CampaignFleetAPI fleet, float radius, float t) {
        final float duration = 1.3333f + ((1f - t) * 1.6666f);
        if (fleet.getMemoryWithoutUpdate().getBoolean("$icfbSusRing"))
            return;
        fleet.getMemoryWithoutUpdate().set("$icfbSusRing", true, (duration / 10f) / 2f);

        CampaignPingSpec custom = new CampaignPingSpec();
        custom.setColor(new Color(255, 70, 20, 100));
        custom.setWidth(4 + t * 26f);
        custom.setMinRange(radius);
        custom.setRange(radius);
        custom.setDuration(2);
        custom.setAlphaMult(0.1f + t * 0.4f);
        custom.setNum(1);
        custom.setInvert(true);

        Global.getSector().addPing(fleet, custom);
    }

    private static void updateFleetSuspicion(CampaignFleetAPI fleet, float deltaTime) {
        if (fleet == null || fleet.isExpired()) {
            return;
        }
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet == null)
            return;
        if (playerFleet.isVisibleToSensorsOf(fleet)) {
            final float radius = Misc.getDistance(fleet, playerFleet);
            final float sensorRange = fleet.getBaseSensorRangeToDetect(playerFleet.getSensorProfile());
            final float ratio = radius / Math.max(sensorRange, 1f);
            final float t = 1f - Math.min(1f, ratio * ratio);
            final float amount = 0.025f + 0.125f * t;
            modifyFleetSuspicion(fleet, deltaTime * amount);
            if (fleet.isVisibleToSensorsOf(playerFleet))
                spawnPing(fleet, radius, t);
        }
        else {
            modifyFleetSuspicion(fleet, deltaTime * -0.025f);
        }
    }

    private static void modifyFleetSuspicion(CampaignFleetAPI fleet, float amount) {
        float sus = fleet.getMemoryWithoutUpdate().getFloat(FLEET_SUS_LEVEL_KEY);
        sus += amount;
        if (sus > 1)
            sus = 1;
        if (sus < 0)
            sus = 0;
        fleet.getMemoryWithoutUpdate().set(FLEET_SUS_LEVEL_KEY, sus);
    }

    public static float getFleetSuspicion(CampaignFleetAPI fleet) {
        return fleet.getMemoryWithoutUpdate().getFloat(FLEET_SUS_LEVEL_KEY);
    }

    public static boolean fleetHasSuspicion(CampaignFleetAPI fleet) {
        return fleet.getMemoryWithoutUpdate().get(FLEET_SUS_REF_KEY) != null;
    }

    // Totally didn't just copy-paste most of this from the Nexerelin mining bar renderer :^)
    public static void renderFleetSuspicion(CampaignFleetAPI fleet, String susLevelMemKey, ViewportAPI viewport) {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet == null)
            return;
        if (fleet == null
                || fleet.isExpired()
                || !fleet.isInCurrentLocation()
                || !fleet.getMemoryWithoutUpdate().contains(susLevelMemKey)
                || !fleet.isVisibleToSensorsOf(playerFleet))
            return;
        final float amount = fleet.getMemoryWithoutUpdate().getFloat(susLevelMemKey);
        if (amount <= 0)
            return;

        final int barWidth = 128;
        final int barHeight = 16;
        final int iconWidth = 32;
        final String iconCategory = "icfb_icons";
        String iconId = "sus";
        final Color barColor = new Color(140, 28, 24, 255);
        Color fillColor = new Color(189, 109, 80, 255);

        if (amount >= 1) {
            fillColor = new Color(220, 200, 80, 255);
            iconId = "sus2";
        }

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

    @Override
    public void renderInUICoordsBelowUI(ViewportAPI viewport) {

    }

    @Override
    public void renderInUICoordsAboveUIBelowTooltips(ViewportAPI viewport) {
        if (Global.getSector().getPlayerFleet() == null)
            return;
        CampaignUIAPI ui = Global.getSector().getCampaignUI();
        if (ui.isShowingDialog() || ui.isShowingMenu())
            return;
        renderFleetSuspicion(_fleet, FLEET_SUS_LEVEL_KEY, viewport);
    }

    @Override
    public void renderInUICoordsAboveUIAndTooltips(ViewportAPI viewport) {

    }

    @Override
    public boolean isDone() {
        return _fleet == null || _fleet.isExpired();
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        updateFleetSuspicion(_fleet, amount);
    }

    @Override
    public void cleanup() {
        Global.getSector().getListenerManager().removeListener(this);
    }
}
