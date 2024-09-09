package org.shmo.icfb.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ShiftJumpDamageIntel extends BaseIntelPlugin {
    private final String _message;

    public ShiftJumpDamageIntel(String message) {
        super();
        _message = message;
    }

    @Override
    protected String getName() {
        return null;
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
        info.addPara(_message, 10f, Misc.getNegativeHighlightColor());
    }

    @Override
    public boolean isEnded() {
        return true;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public String getIcon() {
        return null;
    }
}
