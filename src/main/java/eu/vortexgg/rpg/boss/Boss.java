package eu.vortexgg.rpg.boss;

import com.eatthepath.uuid.FastUUID;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.vortexgg.rpg.RPG;
import eu.vortexgg.rpg.boss.event.BossDeathEvent;
import eu.vortexgg.rpg.boss.event.BossSpawnEvent;
import eu.vortexgg.rpg.config.Lang;
import eu.vortexgg.rpg.data.DataManager;
import eu.vortexgg.rpg.spawner.Spawner;
import eu.vortexgg.rpg.spawner.SpawnerManager;
import eu.vortexgg.rpg.util.BukkitUtil;
import eu.vortexgg.rpg.util.JavaUtil;
import eu.vortexgg.rpg.util.RewardItem;
import eu.vortexgg.rpg.util.TaskUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class Boss {

    final BossType type;
    final Spawner spawner;
    final Map<String, Integer> damagers = Maps.newHashMap();
    UUID id;
    LivingEntity entity;
    int totalDamage;

    public Boss(Spawner spawner, BossType type) {
        this.spawner = spawner;
        this.type = type;
    }

    public void spawn(Location spawn) {
        entity = (LivingEntity) spawn.getWorld().spawnEntity(spawn, type.getData().getType());
        entity.setCustomName(type.getDisplayName());
        id = entity.getUniqueId();
        type.getData().apply(entity);
        SpawnerManager.get().getAliveBosses().add(this);
        Bukkit.getPluginManager().callEvent(new BossSpawnEvent(this));
    }

    public void despawn() {
        if (entity != null) {
            entity.remove();
            spawner.die();
        }
    }

    public void onTick() {
        if (isOutsideOfRadius()) {
            entity.teleport(spawner.getSpawnLocation());
        }
    }

    public void onAttack(EntityDamageEvent event) {
        event.setDamage(type.getData().getDamage());
    }

    public void onDeath() {
        if(!type.getData().isChild())
           spawner.die();

        Map<String, Double> percentage = JavaUtil.calculatePercents(damagers, totalDamage);

        int count = 0;
        final boolean bcs = type.getData().isBroadcastable();

        List<String> broadcast = null;
        if (bcs) {
            broadcast = Lists.newArrayList();
            broadcast.addAll(Lang.BOSS_KILLED_HEADER);
        }

        for (Map.Entry<String, Double> entry : percentage.entrySet()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(entry.getKey());
            double earned = 0;
            if (type.getData().getMoneyReward() != 0) {
                earned = entry.getValue() * type.getData().getMoneyReward() / 100.0D;
                RPG.getEcon().depositPlayer(op, earned);
            }

            if (bcs) {
                if (count == 4) {
                    broadcast.add(Lang.BOSS_KILLED_OTHER.replaceAll("%amount%", String.valueOf(damagers.size() - count)));
                } else if (count < 4) {
                    broadcast.add(Lang.BOSS_TOP_FORMAT
                            .replaceAll("%displayname%", BukkitUtil.getName(op))
                            .replaceAll("%earned%", String.valueOf(earned))
                            .replaceAll("%percentage%", String.valueOf(entry.getValue()))
                            .replaceAll("%damage%", String.valueOf(damagers.get(entry.getKey()))));
                }
            }

            count++;
        }

        TaskUtil.async(() -> {
                    List<Pair<String, Integer>> top = damagers.entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).map(entry -> Pair.of(entry.getKey(), entry.getValue())).collect(Collectors.toList());
                    if (top.size() > 3)
                        top.subList(0, 2);
                    DataManager.get().update(
                            "INSERT INTO bosses(ID, DATE, TOP) VALUES" +
                                    "(" +
                                    "'" + FastUUID.toString(id) + "', " +
                                    "'" + System.currentTimeMillis() + "', " +
                                    "'" + JavaUtil.stringify(top, 4, "player", "damage") + "'"
                                    + ")");
                }
        );

        if (bcs) {
            broadcast.addAll(Lang.BOSS_KILLED_FOOTER);
            broadcast.replaceAll(str -> str.replaceAll("%money%", String.valueOf(type.getData().getMoneyReward())).replaceAll("%displayname%", type.getDisplayName()));
            broadcast.forEach(BukkitUtil::bcs);
        }

        Location location = entity.getLocation();
        List<ItemStack> rewards = type.getData().getRewards().stream().filter(RewardItem::isGood).map(RewardItem::getItem).collect(Collectors.toList());
        rewards.forEach(item -> location.getWorld().dropItemNaturally(location, item));

        Bukkit.getPluginManager().callEvent(new BossDeathEvent(this, rewards));

        SpawnerManager.get().getAliveBosses().remove(this);
    }

    public void onDamage(Player player, double damage) {
        totalDamage += damage;

        String id = player.getName();
        if (!damagers.containsKey(id)) {
            damagers.put(id, (int) damage);
        } else {
            damagers.put(id, (int) (damagers.get(id) + damage));
        }
    }

    public boolean isOutsideOfRadius() {
        return entity.getLocation().distance(spawner.getSpawnLocation()) >= type.getData().getInactiveRadius();
    }

    public void heal(double health) {
        BukkitUtil.setHealth(entity, entity.getHealth() + health);
    }

}
