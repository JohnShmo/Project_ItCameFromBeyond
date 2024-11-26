package org.shmo.icfb.campaign.entities;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.InteractionDialogImageVisual;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.AbilityPlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.StatBonus;
import com.fs.starfarer.api.impl.campaign.procgen.Constellation;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomEntityWrapper implements SectorEntityToken {

    private final SectorEntityToken _entity;
    public CustomEntityWrapper(SectorEntityToken underlyingEntity) {
        _entity = underlyingEntity;
    }

    @Override
    public boolean isPlayerFleet() {
        return _entity.isPlayerFleet();
    }

    @Override
    public MarketAPI getMarket() {
        return _entity.getMarket();
    }

    @Override
    public void setMarket(MarketAPI market) {
        _entity.setMarket(market);
    }

    @Override
    public CargoAPI getCargo() {
        return _entity.getCargo();
    }

    @Override
    public Vector2f getLocation() {
        return _entity.getLocation();
    }

    @Override
    public Vector2f getLocationInHyperspace() {
        return _entity.getLocationInHyperspace();
    }

    @Override
    public OrbitAPI getOrbit() {
        return _entity.getOrbit();
    }

    @Override
    public void setOrbit(OrbitAPI orbit) {
        _entity.setOrbit(orbit);
    }

    @Override
    public String getId() {
        return _entity.getId();
    }

    @Override
    public String getName() {
        return _entity.getName();
    }

    @Override
    public String getFullName() {
        return _entity.getFullName();
    }

    @Override
    public void setFaction(String factionId) {
        _entity.setFaction(factionId);
    }

    @Override
    public LocationAPI getContainingLocation() {
        return _entity.getContainingLocation();
    }

    @Override
    public float getRadius() {
        return _entity.getRadius();
    }

    @Override
    public FactionAPI getFaction() {
        return _entity.getFaction();
    }

    @Override
    public String getCustomDescriptionId() {
        return _entity.getCustomDescriptionId();
    }

    @Override
    public void setCustomDescriptionId(String customDescriptionId) {
        _entity.setCustomDescriptionId(customDescriptionId);
    }

    @Override
    public void setCustomInteractionDialogImageVisual(InteractionDialogImageVisual visual) {
        _entity.setCustomInteractionDialogImageVisual(visual);
    }

    @Override
    public InteractionDialogImageVisual getCustomInteractionDialogImageVisual() {
        return _entity.getCustomInteractionDialogImageVisual();
    }

    @Override
    public void setFreeTransfer(boolean freeTransfer) {
        _entity.setFreeTransfer(freeTransfer);
    }

    @Override
    public boolean isFreeTransfer() {
        return _entity.isFreeTransfer();
    }

    @Override
    public boolean hasTag(String tag) {
        return _entity.hasTag(tag);
    }

    @Override
    public void addTag(String tag) {
        _entity.addTag(tag);
    }

    @Override
    public void removeTag(String tag) {
        _entity.removeTag(tag);
    }

    @Override
    public Collection<String> getTags() {
        return _entity.getTags();
    }

    @Override
    public void clearTags() {
        _entity.clearTags();
    }

    @Override
    public void setFixedLocation(float x, float y) {
        _entity.setFixedLocation(x, y);
    }

    @Override
    public void setCircularOrbit(SectorEntityToken focus, float angle, float orbitRadius, float orbitDays) {
        _entity.setCircularOrbit(focus, angle, orbitRadius, orbitDays);
    }

    @Override
    public void setOrbitFocus(SectorEntityToken focus) {
        _entity.setOrbitFocus(focus);
    }

    @Override
    public void setCircularOrbitPointingDown(SectorEntityToken focus, float angle, float orbitRadius, float orbitDays) {
        _entity.setCircularOrbitPointingDown(focus, angle, orbitRadius, orbitDays);
    }

    @Override
    public void setCircularOrbitWithSpin(SectorEntityToken focus, float angle, float orbitRadius, float orbitDays, float minSpin, float maxSpin) {
        _entity.setCircularOrbitWithSpin(focus, angle, orbitRadius, orbitDays, minSpin, maxSpin);
    }

    @Override
    public MemoryAPI getMemory() {
        return _entity.getMemory();
    }

    @Override
    public MemoryAPI getMemoryWithoutUpdate() {
        return _entity.getMemoryWithoutUpdate();
    }

    @Override
    public float getFacing() {
        return _entity.getFacing();
    }

    @Override
    public void setFacing(float facing) {
        _entity.setFacing(facing);
    }

    @Override
    public boolean isInHyperspace() {
        return _entity.isInHyperspace();
    }

    @Override
    public void addScript(EveryFrameScript script) {
        _entity.addScript(script);
    }

    @Override
    public void removeScript(EveryFrameScript script) {
        _entity.removeScript(script);
    }

    @Override
    public void removeScriptsOfClass(Class c) {
        _entity.removeScriptsOfClass(c);
    }

    @Override
    public boolean isInOrNearSystem(StarSystemAPI system) {
        return _entity.isInOrNearSystem(system);
    }

    @Override
    public boolean isInCurrentLocation() {
        return _entity.isInCurrentLocation();
    }

    @Override
    public Vector2f getVelocity() {
        return _entity.getVelocity();
    }

    @Override
    public void setInteractionImage(String category, String key) {
        _entity.setInteractionImage(category, key);
    }

    @Override
    public void setName(String name) {
        _entity.setName(name);
    }

    @Override
    public boolean isAlive() {
        return _entity.isAlive();
    }

    @Override
    public PersonAPI getActivePerson() {
        return _entity.getActivePerson();
    }

    @Override
    public void setActivePerson(PersonAPI activePerson) {
        _entity.setActivePerson(activePerson);
    }

    @Override
    public boolean isVisibleToSensorsOf(SectorEntityToken other) {
        return _entity.isVisibleToSensorsOf(other);
    }

    @Override
    public boolean isVisibleToPlayerFleet() {
        return _entity.isVisibleToPlayerFleet();
    }

    @Override
    public VisibilityLevel getVisibilityLevelToPlayerFleet() {
        return _entity.getVisibilityLevelToPlayerFleet();
    }

    @Override
    public VisibilityLevel getVisibilityLevelTo(SectorEntityToken other) {
        return _entity.getVisibilityLevelTo(other);
    }

    @Override
    public void addAbility(String id) {
        _entity.addAbility(id);
    }

    @Override
    public void removeAbility(String id) {
        _entity.removeAbility(id);
    }

    @Override
    public AbilityPlugin getAbility(String id) {
        return _entity.getAbility(id);
    }

    @Override
    public boolean hasAbility(String id) {
        return _entity.hasAbility(id);
    }

    @Override
    public Map<String, AbilityPlugin> getAbilities() {
        return _entity.getAbilities();
    }

    @Override
    public boolean isTransponderOn() {
        return _entity.isTransponderOn();
    }

    @Override
    public void setTransponderOn(boolean transponderOn) {
        _entity.setTransponderOn(transponderOn);
    }

    @Override
    public void addFloatingText(String text, Color color, float duration) {
        _entity.addFloatingText(text, color, duration);
    }

    @Override
    public SectorEntityToken getLightSource() {
        return _entity.getLightSource();
    }

    @Override
    public Color getLightColor() {
        return _entity.getLightColor();
    }

    @Override
    public void setMemory(MemoryAPI memory) {
        _entity.setMemory(memory);
    }

    @Override
    public Map<String, Object> getCustomData() {
        return _entity.getCustomData();
    }

    @Override
    public Color getIndicatorColor() {
        return _entity.getIndicatorColor();
    }

    @Override
    public CustomCampaignEntityPlugin getCustomPlugin() {
        return _entity.getCustomPlugin();
    }

    @Override
    public float getCircularOrbitRadius() {
        return _entity.getCircularOrbitRadius();
    }

    @Override
    public float getCircularOrbitPeriod() {
        return _entity.getCircularOrbitPeriod();
    }

    @Override
    public SectorEntityToken getOrbitFocus() {
        return _entity.getOrbitFocus();
    }

    @Override
    public void setId(String id) {
        _entity.setId(id);
    }

    @Override
    public String getAutogenJumpPointNameInHyper() {
        return _entity.getAutogenJumpPointNameInHyper();
    }

    @Override
    public void setAutogenJumpPointNameInHyper(String autogenJumpPointNameInHyper) {
        _entity.setAutogenJumpPointNameInHyper(autogenJumpPointNameInHyper);
    }

    @Override
    public boolean isSkipForJumpPointAutoGen() {
        return _entity.isSkipForJumpPointAutoGen();
    }

    @Override
    public void setSkipForJumpPointAutoGen(boolean skipForJumpPointAutoGen) {
        _entity.setSkipForJumpPointAutoGen(skipForJumpPointAutoGen);
    }

    @Override
    public float getCircularOrbitAngle() {
        return _entity.getCircularOrbitAngle();
    }

    @Override
    public String getCustomEntityType() {
        return _entity.getCustomEntityType();
    }

    @Override
    public float getSensorStrength() {
        return _entity.getSensorStrength();
    }

    @Override
    public void setSensorStrength(Float sensorStrength) {
        _entity.setSensorStrength(sensorStrength);
    }

    @Override
    public float getSensorProfile() {
        return _entity.getSensorProfile();
    }

    @Override
    public void setSensorProfile(Float sensorProfile) {
        _entity.setSensorProfile(sensorProfile);
    }

    @Override
    public StatBonus getDetectedRangeMod() {
        return _entity.getDetectedRangeMod();
    }

    @Override
    public StatBonus getSensorRangeMod() {
        return _entity.getSensorRangeMod();
    }

    @Override
    public float getBaseSensorRangeToDetect(float sensorProfile) {
        return _entity.getBaseSensorRangeToDetect(sensorProfile);
    }

    @Override
    public boolean hasSensorStrength() {
        return _entity.hasSensorStrength();
    }

    @Override
    public boolean hasSensorProfile() {
        return _entity.hasSensorProfile();
    }

    @Override
    public float getMaxSensorRangeToDetect(SectorEntityToken other) {
        return _entity.getMaxSensorRangeToDetect(other);
    }

    @Override
    public boolean isDiscoverable() {
        return _entity.isDiscoverable();
    }

    @Override
    public void setDiscoverable(Boolean discoverable) {
        _entity.setDiscoverable(discoverable);
    }

    @Override
    public CustomEntitySpecAPI getCustomEntitySpec() {
        return _entity.getCustomEntitySpec();
    }

    @Override
    public List<SalvageEntityGenDataSpec.DropData> getDropValue() {
        return _entity.getDropValue();
    }

    @Override
    public List<SalvageEntityGenDataSpec.DropData> getDropRandom() {
        return _entity.getDropRandom();
    }

    @Override
    public void addDropValue(String group, int value) {
        _entity.addDropValue(group, value);
    }

    @Override
    public void addDropRandom(String group, int chances) {
        _entity.addDropRandom(group, chances);
    }

    @Override
    public void addDropRandom(String group, int chances, int value) {
        _entity.addDropRandom(group, chances, value);
    }

    @Override
    public boolean isExpired() {
        return _entity.isExpired();
    }

    @Override
    public void setExpired(boolean expired) {
        _entity.setExpired(expired);
    }

    @Override
    public float getSensorFaderBrightness() {
        return _entity.getSensorFaderBrightness();
    }

    @Override
    public float getSensorContactFaderBrightness() {
        return _entity.getSensorContactFaderBrightness();
    }

    @Override
    public void forceSensorFaderBrightness(float b) {
        _entity.forceSensorFaderBrightness(b);
    }

    @Override
    public Float getDiscoveryXP() {
        return _entity.getDiscoveryXP();
    }

    @Override
    public void setDiscoveryXP(Float discoveryXP) {
        _entity.setDiscoveryXP(discoveryXP);
    }

    @Override
    public boolean hasDiscoveryXP() {
        return _entity.hasDiscoveryXP();
    }

    @Override
    public void addDropValue(SalvageEntityGenDataSpec.DropData data) {
        _entity.addDropValue(data);
    }

    @Override
    public void addDropRandom(SalvageEntityGenDataSpec.DropData data) {
        _entity.addDropRandom(data);
    }

    @Override
    public void setAlwaysUseSensorFaderBrightness(Boolean alwaysUseSensorFaderBrightness) {
        _entity.setAlwaysUseSensorFaderBrightness(alwaysUseSensorFaderBrightness);
    }

    @Override
    public Boolean getAlwaysUseSensorFaderBrightness() {
        return _entity.getAlwaysUseSensorFaderBrightness();
    }

    @Override
    public void advance(float amount) {
        _entity.advance(amount);
    }

    @Override
    public boolean hasScriptOfClass(Class c) {
        return _entity.hasScriptOfClass(c);
    }

    @Override
    public void setContainingLocation(LocationAPI location) {
        _entity.setContainingLocation(location);
    }

    @Override
    public void clearAbilities() {
        _entity.clearAbilities();
    }

    @Override
    public Constellation getConstellation() {
        return _entity.getConstellation();
    }

    @Override
    public boolean isStar() {
        return _entity.isStar();
    }

    @Override
    public Float getSalvageXP() {
        return _entity.getSalvageXP();
    }

    @Override
    public void setSalvageXP(Float salvageXP) {
        _entity.setSalvageXP(salvageXP);
    }

    @Override
    public boolean hasSalvageXP() {
        return _entity.hasSalvageXP();
    }

    @Override
    public void setDetectionRangeDetailsOverrideMult(Float detectionRangeDetailsOverrideMult) {
        _entity.setDetectionRangeDetailsOverrideMult(detectionRangeDetailsOverrideMult);
    }

    @Override
    public Float getDetectionRangeDetailsOverrideMult() {
        return _entity.getDetectionRangeDetailsOverrideMult();
    }

    @Override
    public VisibilityLevel getVisibilityLevelOfPlayerFleet() {
        return _entity.getVisibilityLevelOfPlayerFleet();
    }

    @Override
    public void setCircularOrbitAngle(float angle) {
        _entity.setCircularOrbitAngle(angle);
    }

    @Override
    public void addFloatingText(String text, Color color, float duration, boolean showWhenOnlySensorContact) {
        _entity.addFloatingText(text, color, duration, showWhenOnlySensorContact);
    }

    @Override
    public boolean isSystemCenter() {
        return _entity.isSystemCenter();
    }

    @Override
    public StarSystemAPI getStarSystem() {
        return _entity.getStarSystem();
    }

    @Override
    public void clearFloatingText() {
        _entity.clearFloatingText();
    }

    @Override
    public void setLocation(float x, float y) {
        _entity.setLocation(x, y);
    }

    @Override
    public void autoUpdateHyperLocationBasedOnInSystemEntityAtRadius(SectorEntityToken entity, float radius) {
        _entity.autoUpdateHyperLocationBasedOnInSystemEntityAtRadius(entity, radius);
    }

    @Override
    public void forceSensorContactFaderBrightness(float b) {
        _entity.forceSensorContactFaderBrightness(b);
    }

    @Override
    public void forceSensorFaderOut() {
        _entity.forceSensorFaderOut();
    }

    @Override
    public void setLightSource(SectorEntityToken star, Color color) {
        _entity.setLightSource(star, color);
    }

    @Override
    public List<EveryFrameScript> getScripts() {
        return _entity.getScripts();
    }

    @Override
    public float getExtendedDetectedAtRange() {
        return 0;
    }

    @Override
    public void setExtendedDetectedAtRange(Float extendedDetectedAtRange) {
        _entity.setExtendedDetectedAtRange(extendedDetectedAtRange);
    }

    @Override
    public void fadeOutIndicator() {
        _entity.fadeOutIndicator();
    }

    @Override
    public void fadeInIndicator() {
        _entity.fadeInIndicator();
    }

    @Override
    public void forceOutIndicator() {
        _entity.fadeOutIndicator();
    }
}
