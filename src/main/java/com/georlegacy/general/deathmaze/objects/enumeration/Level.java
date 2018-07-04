package com.georlegacy.general.deathmaze.objects.enumeration;

import lombok.Getter;

import java.io.Serializable;

public enum Level implements Serializable {

    ZERO(0, 0),
    ONE(1, 5),
    TWO(2, 10),
    THREE(3, 20),
    FOUR(4, 30),
    FIVE(5, 50),
    SIX(6, 75),
    SEVEN(7, 100),
    EIGHT(8, 150),
    NINE(9, 200),
    TEN(10, 300),
    ELEVEN(11, 500),
    TWELVE(12, 750),
    THIRTEEN(13, 1000),
    FOURTEEN(14, 1500),
    FIFTEEN(15, 2000);

    @Getter private int level;
    @Getter private int xp;

    public int getPreviousTotal() {
        int previousTotal = 0;
        for (Level val : values()) {
            if (val.getLevel() < level) {
                previousTotal += val.getXp();
            }
        }
        return previousTotal;
    }

    Level(int level, int xp) {
        this.level = level;
        this.xp = xp;
    }

    public static Level getNextLevel(Level level) {
        return getByLevel(level.getLevel() + 1);
    }

    public static Level getByLevel(int levelNumber) throws IllegalArgumentException {
        for (Level level :values()) {
            if (level.getLevel() == levelNumber) {
                return level;
            }
        }
        throw new IllegalArgumentException();
    }

}
