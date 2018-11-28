package com.georlegacy.general.deathmaze.objects;

import com.georlegacy.general.deathmaze.objects.annotations.Setable;
import com.georlegacy.general.deathmaze.util.SerializableLocation;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class BossFight implements Serializable {

    private final EntityType bossType;
    private final String regionName;
    private final SerializableLocation spawnLocation;
    private final SerializableLocation defeatTriggerLocation;
    private String unlockedRegionName;

    private String name;
    private String base64Holding;
    private String base64Offhand;
    private String base64Head;
    private String base64Body;
    private String base64Leg;
    private String base64Boot;
    private boolean noGravity;
    private boolean canPickUpLoot;
    private boolean fallFlying;
    private boolean glowing;
    private boolean leftHanded;
    private int maxHealth;
    private int followRange;
    private int knockbackResistance;
    private int movementSpeed;
    private int attackDamage;
    private int armor;
    private final Set<PotionEffect> potionEffects;


    public BossFight(EntityType bossType,
                     RegionExplorable region,
                     Location spawnLocation,
                     Location defeatTriggerLocation,
                     String unlockedRegionName,

                     String name,
                     String base64Holding,
                     String base64Offhand,
                     String base64Head,
                     String base64Body,
                     String base64Leg,
                     String base64Boot,
                     boolean noGravity,
                     boolean canPickUpLoot,
                     boolean fallFlying,
                     boolean glowing,
                     boolean leftHanded,
                     int maxHealth,
                     int followRange,
                     int knockbackResistance,
                     int movementSpeed,
                     int attackDamage,
                     int armor
    ) {
        this.bossType = bossType;
        this.regionName = region.getName();
        this.spawnLocation = new SerializableLocation(spawnLocation);
        this.defeatTriggerLocation = new SerializableLocation(defeatTriggerLocation);

        this.unlockedRegionName = unlockedRegionName;
        this.name = name;
        this.base64Holding = base64Holding;
        this.base64Offhand = base64Offhand;
        this.base64Head = base64Head;
        this.base64Body = base64Body;
        this.base64Leg = base64Leg;
        this.base64Boot = base64Boot;
        this.noGravity = noGravity;
        this.canPickUpLoot = canPickUpLoot;
        this.fallFlying = fallFlying;
        this.glowing = glowing;
        this.leftHanded = leftHanded;
        this.maxHealth = maxHealth;
        this.followRange = followRange;
        this.knockbackResistance = knockbackResistance;
        this.movementSpeed = movementSpeed;
        this.attackDamage = attackDamage;
        this.armor = armor;
        this.potionEffects = new HashSet<>();
    }


    public String setUnlockedRegionName(String unlockedRegionName) {
        this.unlockedRegionName = unlockedRegionName;
        return unlockedRegionName;
    }

    public String setName(String name) {
        this.name = name;
        return name;
    }

    public String setBase64Holding(String base64Holding) {
        this.base64Holding = base64Holding;
        return base64Holding;
    }

    public String setBase64Offhand(String base64Offhand) {
        this.base64Offhand = base64Offhand;
        return base64Offhand;
    }

    public String setBase64Head(String base64Head) {
        this.base64Head = base64Head;
        return base64Head;
    }

    public String setBase64Body(String base64Body) {
        this.base64Body = base64Body;
        return base64Body;
    }

    public String setBase64Leg(String base64Leg) {
        this.base64Leg = base64Leg;
        return base64Leg;
    }

    public String setBase64Boot(String base64Boot) {
        this.base64Boot = base64Boot;
        return base64Boot;
    }

    @Setable(setDisplayName = "noGravity")
    public Boolean setNoGravity(boolean noGravity) {
        this.noGravity = noGravity;
        return noGravity;
    }

    @Setable(setDisplayName = "canPickUpLoot")
    public Boolean setCanPickUpLoot(boolean canPickUpLoot) {
        this.canPickUpLoot = canPickUpLoot;
        return canPickUpLoot;
    }

    @Setable(setDisplayName = "fallFlying")
    public Boolean setFallFlying(boolean fallFlying) {
        this.fallFlying = fallFlying;
        return fallFlying;
    }

    @Setable(setDisplayName = "glowing")
    public Boolean setGlowing(boolean glowing) {
        this.glowing = glowing;
        return glowing;
    }

    @Setable(setDisplayName = "leftHanded")
    public Boolean setLeftHanded(boolean leftHanded) {
        this.leftHanded = leftHanded;
        return leftHanded;
    }

    @Setable(setDisplayName = "maxHealth")
    public Integer setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        return maxHealth;
    }

    @Setable(setDisplayName = "followRange")
    public Integer setFollowRange(int followRange) {
        this.followRange = followRange;
        return followRange;
    }

    @Setable(setDisplayName = "knockbackResistance")
    public Integer setKnockbackResistance(int knockbackResistance) {
        this.knockbackResistance = knockbackResistance;
        return knockbackResistance;
    }

    @Setable(setDisplayName = "movementSpeed")
    public Integer setMovementSpeed(int movementSpeed) {
        this.movementSpeed = movementSpeed;
        return movementSpeed;
    }

    @Setable(setDisplayName = "attackDamage")
    public Integer setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
        return attackDamage;
    }

    @Setable(setDisplayName = "armor")
    public Integer setArmor(int armor) {
        this.armor = armor;
        return armor;
    }

}
