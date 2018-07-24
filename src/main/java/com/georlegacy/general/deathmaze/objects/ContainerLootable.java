package com.georlegacy.general.deathmaze.objects;

import com.georlegacy.general.deathmaze.util.ItemStackSerializerUtil;
import com.georlegacy.general.deathmaze.util.SerializableLocation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.InventoryHolder;

import java.io.Serializable;

public class ContainerLootable implements Serializable {

    static final long serialVersionUID = 2438756423756782377L;

    public ContainerLootable(final Material type, final long refillSeconds, final InventoryHolder container, final Location location, final boolean hiddenWhenEmpty) {
        this.type = type;
        this.refillSeconds = refillSeconds;
        this.location = new SerializableLocation(location);
        this.items = ItemStackSerializerUtil.itemStackArrayToBase64(container.getInventory().getContents());
        this.hiddenWhenEmpty = hiddenWhenEmpty;
    }

    @Getter private final Material type;

    @Getter @Setter private long refillSeconds;

    @Getter @Setter private SerializableLocation location;

    @Getter @Setter private String items;

    @Getter @Setter private boolean hiddenWhenEmpty;

}
