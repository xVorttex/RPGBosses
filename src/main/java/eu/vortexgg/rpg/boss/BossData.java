package eu.vortexgg.rpg.boss;

import com.google.common.collect.Lists;
import eu.vortexgg.rpg.RPG;
import eu.vortexgg.rpg.util.RewardItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BossData {

    public static final String BOSS_METADATA_ID = "custom";
    public static final MetadataValue BOSS_METADATA = new FixedMetadataValue(RPG.get(), true);

    EntityType type;
    List<RewardItem> rewards = Lists.newArrayList();
    int moneyReward;
    ItemStack[] armor = new ItemStack[3];
    ItemStack hand, secondHand;
    int noHitDelay = 20;
    boolean baby, child, broadcastable = true;
    double damage, knockbackResistance, maxHealth, attackSpeed, movementSpeed, followRange, inactiveRadius = 24;

    public void apply(LivingEntity entity) {
        entity.setMaxHealth(maxHealth);
        entity.setHealth(maxHealth);
        if(entity instanceof Ageable && baby)
            ((Ageable)entity).setBaby();
        entity.setMaximumNoDamageTicks(noHitDelay);
        entity.getEquipment().setArmorContents(armor);
        entity.getEquipment().setItemInMainHand(hand);
        entity.getEquipment().setItemInOffHand(secondHand);
        entity.setCanPickupItems(false);
        if (movementSpeed != 0)
            entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed);
        if (attackSpeed != 0)
            entity.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(attackSpeed);
        if (followRange != 0)
            entity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(followRange);
        if (knockbackResistance != 0)
            entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(knockbackResistance);
        entity.setMetadata(BOSS_METADATA_ID, BOSS_METADATA);
    }

    public BossData setArmor(ItemStack[] items) {
        this.armor = items;
        return this;
    }

    public BossData setBaby(boolean baby) {
        this.baby = baby;
        return this;
    }

    public BossData setChild(boolean child) {
        this.child = child;
        return this;
    }

    public BossData setBroadcastable(boolean broadcastable) {
        this.broadcastable = broadcastable;
        return this;
    }

    public BossData setFirstHand(ItemStack item) {
        this.hand = item;
        return this;
    }

    public BossData setSecondHand(ItemStack item) {
        this.secondHand = item;
        return this;
    }

    public BossData setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
        return this;
    }

    public BossData setMovementSpeed(double speed) {
        this.movementSpeed = speed;
        return this;
    }

    public BossData setNoHitDelay(int noHitDelay) {
        this.noHitDelay = noHitDelay;
        return this;
    }

    public BossData setRewards(List<RewardItem> rewards) {
        this.rewards = rewards;
        return this;
    }

    public BossData setMoneyReward(int moneyReward) {
        this.moneyReward = moneyReward;
        return this;
    }

    public BossData setDamage(double damage) {
        this.damage = damage;
        return this;
    }

    public BossData setKnockbackResistance(double knockbackResistance) {
        this.knockbackResistance = knockbackResistance;
        return this;
    }

    public BossData setAttackSpeed(double attackSpeed) {
        this.attackSpeed = attackSpeed;
        return this;
    }

    public BossData setFollowRange(double followRange) {
        this.followRange = followRange;
        return this;
    }

    public BossData setInactiveRadius(double inactiveRadius) {
        this.inactiveRadius = inactiveRadius;
        return this;
    }

    public BossData setType(EntityType type) {
        this.type = type;
        return this;
    }

}
