package org.shmo.icfb.campaign.quests.missions;

import com.fs.starfarer.api.characters.PersonAPI;
import org.shmo.icfb.campaign.quests.impl.missions.AcquireResource;
import org.shmo.icfb.campaign.quests.impl.missions.ExtractScientist;
import org.shmo.icfb.campaign.quests.impl.missions.StealPhaseTech;
import org.shmo.icfb.campaign.quests.impl.missions.SubspaceFissure;

public class IcfbMissions {
    // Go to proc-gen system; Interact with fissure; Avoid or defeat Boundless fleets; Get close to comm relay
    public static final String SUBSPACE_FISSURE     = "icfbFis";

    // Go to proc-gen system; Avoid or defeat Tri-Tachyon fleets; Interact with planet with enough marines; Avoid, defeat, or bribe pather fleet on route back; Return
    public static final String STEAL_PHASE_TECH     = "icfbPhs";

    // Simple: bring enough of a commodity to mission giver for reward
    public static final String ACQUIRE_RESOURCE     = "icfbRes";

    // Go to core world system; Pay scientist or use marines to capture them forcibly; Avoid or defeat associated faction fleet; Return
    public static final String EXTRACT_SCIENTIST    = "icfbSci";

    // Go to proc-gen system; Recover three probes; Avoid or defeat Boundless fleet, or bluff with SP; Get close to comm relay
    public static final String ANALYZE_PROBES       = "icfbPrb";

    // Go to proc-gen system; Defeat Boundless, pirate, or independent (pirate) fleet
    public static final String BOUNTY_EASY          = "icfbBtyE";

    // Go to proc-gen system; Defeat Boundless, pather, Tri-Tachyon, or independent (pirate) fleet
    public static final String BOUNTY_MEDIUM        = "icfbBtyM";

    // Go to proc-gen system; Defeat Boundless, remnant, or independent (pirate) fleet
    public static final String BOUNTY_HARD          = "icfbBtyH";

    public static IcfbMission createMission(String id, PersonAPI person) {
        switch (id) {
            case SUBSPACE_FISSURE: return new SubspaceFissure(person);
            case STEAL_PHASE_TECH: return new StealPhaseTech(person);
            case ACQUIRE_RESOURCE: return new AcquireResource(person);
            case EXTRACT_SCIENTIST: return new ExtractScientist(person);
            // TODO: Other missions
            default: return null;
        }
    }
}
