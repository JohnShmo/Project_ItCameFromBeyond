package org.shmo.icfb.campaign.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class ShiftJump_DamageIntel extends BaseIntelPlugin {
    private static final String ICON_CATEGORY = "intel";
    private static final String ICON_ID = "damage_report";

    private final String _message;

    public ShiftJump_DamageIntel(String message) {
        super();
        _message = message;
    }

    @Override
    protected String getName() {
        return "Shift Jump Incident";
    }

    @Override
    public String getSortString() {
        return _message;
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        info.addPara(_message, 10f);
    }

    @Override
    public void createLargeDescription(CustomPanelAPI panel, float width, float height) {
        TooltipMakerAPI desc = panel.createUIElement(width, height, true);
        createSmallDescription(desc, width, height);
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        super.createIntelInfo(info, mode);
        info.addPara(_message, 10f);
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName(ICON_CATEGORY, ICON_ID);
    }
}
