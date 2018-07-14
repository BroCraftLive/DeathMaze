package com.georlegacy.general.deathmaze.objects.enumeration;

import jdk.nashorn.internal.objects.annotations.Getter;

public enum LeaderboardType {

    KILLS("Kills"),
    DEATHS("Deaths"),
    LEVEL("Level"),
    XP("XP"),
    DISTANCE("Distance"),
    REGIONS("Regions"),
    LOOTABLES("Lootables");

    LeaderboardType(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    private String friendlyName;

    public String getFriendlyName() {
        return friendlyName;
    }

}
