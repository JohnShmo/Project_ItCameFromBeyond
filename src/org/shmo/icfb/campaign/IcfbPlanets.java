package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;

public class IcfbPlanets {
    public static class NEW_ENTERIA {
        public static PlanetData STAR = new PlanetData("icfb_new_enteria");
        public static PlanetData HEIDI = new PlanetData("icfb_heidi");
        public static PlanetData LORELAI = new PlanetData("icfb_lorelai");
        public static PlanetData LUMINARU = new PlanetData("icfb_luminaru");
        public static PlanetData LUMINARU_MAJOR = new PlanetData("icfb_luminaru_major");
        public static PlanetData LUMINARU_MINOR = new PlanetData("icfb_luminaru_minor");
    }

    public static class KATO {
        public static PlanetData STAR = new PlanetData("icfb_kato");
        public static PlanetData ALICE = new PlanetData("icfb_alice");
        public static PlanetData MOLLY = new PlanetData("icfb_molly");
    }

    public static class PlanetData {
        private final String _id;

        public PlanetData(String id) {
            _id = id;
        }

        public void registerPlanet(StarSystemAPI starSystem) {
            setContainingStarSystem(starSystem);
        }

        public String getId() {
            return _id;
        }

        public PlanetAPI getPlanet() {
            StarSystemAPI system = getContainingStarSystem();
            if (system == null)
                return null;
            return (PlanetAPI)system.getEntityById(_id);
        }

        public StarSystemAPI getContainingStarSystem() {
            return (StarSystemAPI) Global.getSector().getMemoryWithoutUpdate().get(getContainingStarSystemKey());
        }

        private String getContainingStarSystemKey() {
            return "$" + _id + ":containingStarSystem";
        }

        private void setContainingStarSystem(LocationAPI location) {
            Global.getSector().getMemoryWithoutUpdate().set(getContainingStarSystemKey(), location);
        }
    }
}
