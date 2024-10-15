package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;

public class IcfbPlanets {
    public static PlanetData NEW_ENTERIA_STAR = new PlanetData("icfb_new_enteria");
    public static PlanetData HEIDI = new PlanetData("icfb_heidi");
    public static PlanetData LORELAI = new PlanetData("icfb_lorelai");
    public static PlanetData LUMINARU = new PlanetData("icfb_luminaru");
    public static PlanetData LUMINARU_MAJOR = new PlanetData("icfb_luminaru_major");
    public static PlanetData LUMINARU_MINOR = new PlanetData("icfb_luminaru_minor");

    public static PlanetData KATO_STAR = new PlanetData("icfb_kato");
    public static PlanetData ALICE = new PlanetData("icfb_alice");
    public static PlanetData MOLLY = new PlanetData("icfb_molly");

    public static PlanetData AZRUUL_STAR_1 = new PlanetData("icfb_azruul_1");
    public static PlanetData AZRUUL_STAR_2 = new PlanetData("icfb_azruul_2");
    public static PlanetData AURUCELLO = new PlanetData("icfb_aurucello");
    public static PlanetData CELADON = new PlanetData("icfb_celadon");
    public static PlanetData PANGEA = new PlanetData("icfb_pangea");
    public static PlanetData LANTERNIA = new PlanetData("icfb_lanternia");

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

        private String getKey() {
            return "$IcfbPlanets:" + _id;
        }

        private String getContainingStarSystemKey() {
            return getKey() + ":containingStarSystem";
        }

        private void setContainingStarSystem(LocationAPI location) {
            Global.getSector().getMemoryWithoutUpdate().set(getContainingStarSystemKey(), location);
        }
    }
}
