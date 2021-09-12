package eu.vortexgg.rpg.boss.entity;

import com.google.common.collect.Lists;
import eu.vortexgg.rpg.boss.Boss;
import eu.vortexgg.rpg.boss.BossType;
import eu.vortexgg.rpg.spawner.Spawner;
import eu.vortexgg.rpg.util.BukkitUtil;
import eu.vortexgg.rpg.util.VItemStack;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SummonerBoss extends Boss {

    private static final ItemStack[] SUMMONER_ARMOR = new ItemStack[4];
    static {
        SUMMONER_ARMOR[3] = new VItemStack(Material.LEATHER_HELMET, "", Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        SUMMONER_ARMOR[2] = new VItemStack(Material.LEATHER_CHESTPLATE, "",Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        SUMMONER_ARMOR[1] = new VItemStack(Material.LEATHER_LEGGINGS, "",Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        SUMMONER_ARMOR[0] = new VItemStack(Material.LEATHER_BOOTS, "",Enchantment.PROTECTION_ENVIRONMENTAL, 1);
    }
    private static final ItemStack SUMMONER_SWORD = new VItemStack(Material.STONE_SWORD, "",Enchantment.DAMAGE_ALL, 1);

    final List<SummonerMiniBoss> minions = Lists.newArrayList();
    int spawnCooldown = 5, armorCooldown = 15;
    boolean armored;

    public SummonerBoss(Spawner spawner, BossType type) {
        super(spawner, type);
    }

    @Override
    public void onTick() {
        super.onTick();
        if(--spawnCooldown <= 0) {
            final int amount = ThreadLocalRandom.current().nextInt(3);
            for(int i = 0; i <= amount; i++) {
                SummonerMiniBoss minion = new SummonerMiniBoss(spawner, BossType.SUMMONER_MINI);
                minion.spawn(entity.getLocation());
                minions.add(minion);
            }
            spawnCooldown = 60;
        }
        if(--armorCooldown <= 0) {
            armorCooldown = 30;
            if(armored) {
                entity.getEquipment().clear();
                armored = false;
                return;
            }
            entity.getEquipment().setItemInMainHand(SUMMONER_SWORD);
            entity.getEquipment().setArmorContents(SUMMONER_ARMOR);
            armored = true;
        }
    }

    @Override
    public void despawn() {
        super.despawn();
        minions.forEach(Boss::despawn);
    }

    public class SummonerMiniBoss extends Boss {

        public SummonerMiniBoss(Spawner spawner, BossType type) {
            super(spawner, type);
        }

        @Override
        public void spawn(Location spawn) {
            super.spawn(spawn);
            entity.setCustomName(BukkitUtil.color("&c&lМиники"));
        }

        @Override
        public boolean isOutsideOfRadius() {
            return entity.getLocation().distance(spawner.isAlive() ? spawner.getCurrent().getEntity().getLocation() : spawner.getSpawnLocation()) >= type.getData().getInactiveRadius();
        }

        @Override
        public void despawn() {
            if (entity != null) {
                entity.remove();
            }
        }

        @Override
        public void onAttack(Player entity, EntityDamageByEntityEvent event) {
            event.setDamage(2.5);
        }

    }

}
