package org.shmo.icfb.campaign.listeners;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;

public interface ShiftDriveListener {
    void notifyShiftJumpUsed(CampaignFleetAPI fleet, float distanceLY);
}
