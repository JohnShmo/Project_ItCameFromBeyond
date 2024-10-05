package org.shmo.icfb.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import org.shmo.icfb.IcfbLog;
import org.shmo.icfb.campaign.gen.EntityFactory;
import org.shmo.icfb.campaign.gen.impl.entities.ChariotOfHopeCorvusModeEntityFactory;
import org.shmo.icfb.campaign.gen.impl.entities.WingsOfEnteriaCorvusModeEntityFactory;

public class IcfbEntities {
    public static EntityData WINGS_OF_ENTERIA = new EntityData("icfb_wings_of_enteria");
    public static EntityData CHARIOT_OF_HOPE = new EntityData("icfb_chariot_of_hope");

    public static void generateForCorvusMode(SectorAPI sector) {
        IcfbLog.info("- Initializing entities...");

        WINGS_OF_ENTERIA.createEntity(
                new WingsOfEnteriaCorvusModeEntityFactory(),
                sector,
                IcfbPlanets.NEW_ENTERIA.LUMINARU_MAJOR.getPlanet(),
                90,
                600,
                20
        );

        CHARIOT_OF_HOPE.createEntity(
                new ChariotOfHopeCorvusModeEntityFactory(),
                sector,
                IcfbPlanets.KATO.MOLLY.getPlanet(),
                -90,
                170,
                15
        );
    }

    public static class EntityData {
        private final String _id;

        public EntityData(String id) {
            _id = id;
        }

        private void createEntity(EntityFactory factory, SectorAPI sector, SectorEntityToken orbitFocus, float angle, float orbitDistance, float orbitDays) {
            SectorEntityToken entity = factory.createEntity(sector, _id, orbitFocus, angle, orbitDistance, orbitDays);
            setContainingLocation(entity.getContainingLocation());
        }

        public String getId() {
            return _id;
        }

        public SectorEntityToken getEntity() {
            LocationAPI location = getContainingLocation();
            if (location == null)
                return null;
            return location.getEntityById(_id);
        }

        public LocationAPI getContainingLocation() {
            return (LocationAPI) Global.getSector().getMemoryWithoutUpdate().get(getContainingLocationKey());
        }

        private String getContainingLocationKey() {
            return "$" + _id + ":containingLocation";
        }

        private void setContainingLocation(LocationAPI location) {
            Global.getSector().getMemoryWithoutUpdate().set(getContainingLocationKey(), location);
        }
    }
}
