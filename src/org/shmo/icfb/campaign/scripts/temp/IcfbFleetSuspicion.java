package org.shmo.icfb.campaign.scripts.temp;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.listeners.CampaignUIRenderingListener;
import com.fs.starfarer.api.combat.ViewportAPI;
import org.shmo.icfb.IcfbRenderMisc;

public class IcfbFleetSuspicion implements CampaignUIRenderingListener, EveryFrameScript {

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
        if (fleet == null || fleet.isExpired())
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

    private static void updateFleetSuspicion(CampaignFleetAPI fleet, float deltaTime) {
        if (fleet == null || fleet.isExpired())
            return;
        if (Global.getSector().getPlayerFleet().isVisibleToSensorsOf(fleet))
            modifyFleetSuspicion(fleet, deltaTime * 0.1f);
        else
            modifyFleetSuspicion(fleet, deltaTime * -0.025f);
    }

    public static void modifyFleetSuspicion(CampaignFleetAPI fleet, float amount) {
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
        IcfbRenderMisc.renderFleetSuspicion(_fleet, FLEET_SUS_LEVEL_KEY, viewport);
    }

    @Override
    public void renderInUICoordsAboveUIAndTooltips(ViewportAPI viewport) {

    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        updateFleetSuspicion(_fleet, amount);
    }
}
