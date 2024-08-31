package org.shmo.icfb.campaign.quests.intel;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class DefeatFleetQuestStepIntel extends LocationQuestStepIntel {
    private String _targetName = null;
    private TargetNameType _targetNameType = TargetNameType.PERSON;

    public enum TargetNameType {
        PERSON,
        FACTION
    }

    public void setTargetName(String targetName) {
        _targetName = targetName;
    }

    public String getTargetName() {
        return _targetName;
    }

    public void setTargetNameType(TargetNameType targetNameType) {
        _targetNameType = targetNameType;
    }

    public TargetNameType getTargetNameType() {
        return _targetNameType;
    }

    private String createBulletPointString() {
        final StringBuilder builder = new StringBuilder();
        final TargetNameType type = getTargetNameType();
        builder.append("Engage and defeat ");

        if (getTargetName() == null) {
            builder.append("the enemy fleet.");
            return builder.toString();
        }

        switch (type) {
            case PERSON: builder.append("%s's fleet."); break;
            case FACTION: builder.append("the %s fleet"); break;
        }

        return builder.toString();
    }

    @Override
    public void addNotificationBulletPoints(TooltipMakerAPI info) {
        final String name = getTargetName();
        if (name != null)
            info.addPara(createBulletPointString(), 0, Misc.getHighlightColor(), name);
        else
            info.addPara(createBulletPointString(), 0);
        super.addNotificationBulletPoints(info);
    }

    @Override
    public void addDescriptionBulletPoints(TooltipMakerAPI info) {
        addNotificationBulletPoints(info);
    }
}
