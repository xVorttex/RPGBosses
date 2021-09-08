package eu.vortexgg.rpg.spawner;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.object.line.CraftTextLine;
import com.google.common.collect.Maps;
import eu.vortexgg.rpg.RPG;
import eu.vortexgg.rpg.boss.Boss;
import eu.vortexgg.rpg.boss.BossType;
import eu.vortexgg.rpg.spawner.menu.SpawnerMenu;
import eu.vortexgg.rpg.util.BukkitUtil;
import eu.vortexgg.rpg.util.TimeUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Spawner implements ConfigurationSerializable {

    String id;
    Boss current;
    BossType type;
    Location spawnLocation;
    long interval, respawnAt;
    SpawnerMenu spawnerMenu;
    Hologram hologram;
    boolean showHologram = true;

    public Spawner(String id, Location spawnLocation, BossType type, Number interval) {
        this.id = id;
        this.type = type;
        this.spawnLocation = spawnLocation;
        this.interval = interval.longValue();
        this.spawnerMenu = new SpawnerMenu(this);
        SpawnerManager.get().getSpawners().put(id, this);
    }

    public Spawner(Map<String, Object> map) {
        this((String) map.get("id"), BukkitUtil.fromString((String) map.get("location")), BossType.valueOf((String) map.get("type")), ((Number) map.get("interval")).longValue());
        this.respawnAt = ((Number) map.get("respawnAt")).longValue();
        this.showHologram = (Boolean) map.get("showHologram");
    }

    public void die() {
        respawnAt = System.currentTimeMillis() + interval;
        current = null;
    }

    public void update() {
        if(isAlive()) {
            current.onTick();
        } else if(getRemainingUntilRespawn() <= 0) {
            spawn();
        } else if(showHologram) {
            String time = BukkitUtil.color(type.getDisplayName() + " &fпоявится через &e" + TimeUtil.formatSeconds(getRemainingUntilRespawn() / 1000));
            if(hologram == null) {
                hologram = HologramsAPI.createHologram(RPG.get(), spawnLocation.clone().add(0, 1.5, 0));
                hologram.appendTextLine("Привет, {player}!"); // TODO: Fix {player} somehow...
                hologram.appendTextLine(time);
                return;
            }
            ((CraftTextLine)hologram.getLine(1)).setText(time);
        } else clearHologram();
    }

    public void spawn() {
        current = SpawnerManager.get().spawnBoss(this);
    }

    public void despawn() {
        if(current != null)
            current.despawn();
        clearHologram();
    }

    public boolean isAlive() {
        return current != null && !current.getEntity().isDead();
    }

    public long getRemainingUntilRespawn() {
        return respawnAt - System.currentTimeMillis();
    }

    public void clearHologram() {
        if(hologram != null) {
            hologram.delete();
            hologram = null;
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", id);
        map.put("location", BukkitUtil.toString(spawnLocation));
        map.put("type", type.name());
        map.put("interval", interval);
        map.put("respawnAt", respawnAt);
        map.put("showHologram", showHologram);
        return map;
    }

}
