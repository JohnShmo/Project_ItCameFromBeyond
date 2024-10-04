package org.shmo.icfb.utilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

public class ShmoGenUtils {
    public static PersonAPI createPerson(
            String id,
            MarketAPI market,
            int commIndex,
            boolean isAdmin,
            String firstName,
            String lastName,
            FullName.Gender gender,
            String factionId,
            String rankId,
            String postId,
            String portraitSpriteCategory,
            String portraitSpriteId
    ) {
        PersonAPI person = Global.getFactory().createPerson();
        person.setId(id);
        person.setName(new FullName(firstName, lastName, gender));
        person.setFaction(factionId);
        person.setPortraitSprite(Global.getSettings().getSpriteName(portraitSpriteCategory, portraitSpriteId));

        person.setRankId(rankId);
        person.setPostId(postId);

        if (isAdmin)
            market.setAdmin(person);
        market.addPerson(person);
        market.getCommDirectory().addPerson(person, commIndex);

        return person;
    }

    public static void generateHyperspace(StarSystemAPI system) {
        system.autogenerateHyperspaceJumpPoints(true, true);

        // Clear away hyperspace clouds
        final HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        final NebulaEditor editor = new NebulaEditor(plugin);
        final float minRadius = plugin.getTileSize() * 2;
        final float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(
                system.getLocation().x, system.getLocation().y,
                0, radius + minRadius, 0, 360
        );
        editor.clearArc(
                system.getLocation().x, system.getLocation().y,
                0, radius + minRadius, 0, 360, 0.25f
        );
    }
}
