package eu.vortexgg.rpg.listener;

import eu.vortexgg.rpg.boss.Boss;
import eu.vortexgg.rpg.boss.BossData;
import eu.vortexgg.rpg.spawner.SpawnerManager;
import eu.vortexgg.rpg.util.BukkitUtil;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class BossListener implements Listener {

    @EventHandler
    public void onUnload(ChunkUnloadEvent e) {
        Chunk chunk = e.getChunk();
        for (Entity entity : chunk.getEntities()) {
            if (verify(entity) != null) {
                e.getWorld().loadChunk(chunk);
                e.setSaveChunk(false);
                break;
            }
        }
    }

    @EventHandler
    public void onCombust(EntityCombustEvent e) {
        if (verify(e.getEntity()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (!(e.getTarget() instanceof Player) && verify(e.getEntity()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        LivingEntity damager = BukkitUtil.getFinalAttacker(e, true);
        if(damager == null)
            return;

        Entity entity = e.getEntity();

        Boss boss = verify(entity);
        if (boss != null) {
            EntityDamageEvent.DamageCause cause = e.getCause();
            if(cause == EntityDamageEvent.DamageCause.FALL || cause == EntityDamageEvent.DamageCause.FALLING_BLOCK || cause == EntityDamageEvent.DamageCause.SUFFOCATION || cause == EntityDamageEvent.DamageCause.DROWNING) {
                e.setCancelled(true);
                return;
            }
            if(damager instanceof Player)
                boss.onDamage((Player)damager, e.getFinalDamage());
            return;
        }

        boss = verify(damager);
        if(boss != null) {
            if(!(entity instanceof Player)) {
                e.setCancelled(true);
                return;
            }
            boss.onAttack(e);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        Boss boss = verify(e.getEntity());
        if (boss != null) {
            e.setDroppedExp(0);
            e.getDrops().clear();
            boss.onDeath();
        }
    }

    private Boss verify(Entity entity) {
        Boss boss = null;
        if (entity.hasMetadata(BossData.BOSS_METADATA_ID)) {
            boss = SpawnerManager.get().getBossByEntity(entity);
            if (boss == null) {
                entity.remove();
            }
        }
        return boss;
    }

}
