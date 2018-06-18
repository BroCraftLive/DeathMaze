package com.georlegacy.general.deathmaze.objects;

import com.georlegacy.general.deathmaze.util.SerializableLocation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.io.Serializable;
import java.util.LinkedList;

public class RegionExplorable implements Serializable {

    public RegionExplorable(String name, Location pos1, Location pos2) {
        this.name = name;
        this.pos1 = new SerializableLocation(pos1);
        this.pos2 = new SerializableLocation(pos2);
        this.entrySound = Sound.AMBIENT_CAVE;
        this.entrySplashes = new LinkedList<String>();
        this.entrySplashes.add("Knowing what is unknown");
    }

    @Getter private String name;

    @Getter @Setter private SerializableLocation pos1, pos2;

    @Getter @Setter private Sound entrySound;

    @Getter private LinkedList<String> entrySplashes;

    public void addSplashes(String... splashes) {
        for (String s : splashes) {
            if (entrySplashes.contains(s))
                continue;
            entrySplashes.add(s);
        }
    }

}
