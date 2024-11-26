package org.shmo.icfb.campaign.entities;

import com.fs.starfarer.api.campaign.LocationAPI;
import org.shmo.icfb.campaign.entities.plugins.ShifterRiftCloudPlugin;

public class ShifterRiftCloud extends CustomEntityWrapper {

    public ShifterRiftCloud(LocationAPI location, float x, float y, float radius, float duration) {
        super(ShifterRiftCloudPlugin.create(location, x, y, radius, duration));
    }

    public ShifterRiftCloud(LocationAPI location, float x, float y, float radius) {
        super(ShifterRiftCloudPlugin.create(location, x, y, radius, -1));
    }

    public ShifterRiftCloudPlugin getPlugin() {
        return (ShifterRiftCloudPlugin)getCustomPlugin();
    }

    public void expire() {
        setExpired(true);
    }

    @Override
    public void setExpired(boolean expired) {
        if (!expired)
            super.setExpired(false);
        final ShifterRiftCloudPlugin plugin = getPlugin();
        if (plugin == null)
            return;
        if (plugin.getState().equals(ShifterRiftCloudPlugin.State.IN))
            plugin.setState(ShifterRiftCloudPlugin.State.TRANSITION);
    }

    @Override
    public void setLocation(float x, float y) {
        super.setLocation(x, y);
        final ShifterRiftCloudPlugin plugin = getPlugin();
        if (plugin == null)
            return;
        plugin.setLocation(x, y);
    }
}
