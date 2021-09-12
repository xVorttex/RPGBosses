package eu.vortexgg.rpg.boss.entity;

import eu.vortexgg.rpg.boss.Boss;
import eu.vortexgg.rpg.boss.BossType;
import eu.vortexgg.rpg.spawner.Spawner;
import eu.vortexgg.rpg.util.VItemStack;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class RavagerBoss extends Boss {

    private static final ItemStack RAVAGER_AXE = new VItemStack(Material.IRON_AXE, "", Enchantment.DAMAGE_ALL, 1);
    private static final ItemStack RAVAGER_CROSSBOW = new VItemStack(Material.CROSSBOW, "", Enchantment.IMPALING, 5, Enchantment.MULTISHOT, 1);
    private static final PotionEffect RAVAGER_STRENGTH = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0);

    int abilityCooldown;
    boolean axed;

    public RavagerBoss(Spawner spawner, BossType type) {
        super(spawner, type);
    }

    @Override
    public void onTick() {
        super.onTick();
        if(entity.getHealth() <= (entity.getMaxHealth() / 2)) {
            if(axed) {
                if(--abilityCooldown <= 0) {
                    abilityCooldown = 60;
                    entity.addPotionEffect(RAVAGER_STRENGTH);
                    Player target = (Player)((Mob)entity).getTarget();
                    if(target != null && !target.isDead() && target.getGameMode() != GameMode.CREATIVE) {
                        Location targetLocation = target.getLocation(), location = entity.getLocation();

                        double distance = targetLocation.distance(location);

                        Vector vector = entity.getVelocity();
                        vector.setX((1 + 0.1 * distance) * (targetLocation.getX() - location.getX()) / distance);
                        vector.setY((1 + 0.03 * distance) * (targetLocation.getY() - location.getY()) / distance - 0.5 * -0.08 * distance);
                        vector.setZ((1 + 0.1 * distance) * (targetLocation.getZ() - location.getZ()) / distance);
                        entity.setVelocity(vector);

                        ((Mob) entity).setTarget(target);
                    }
                }
            } else {
                axed = true;
                entity.getEquipment().setItemInMainHand(null); // Refresh main hand item, cuz it will bug the axe
                entity.getEquipment().setItemInMainHand(RAVAGER_AXE);
            }
        }
    }

    @Override
    public void spawn(Location spawn) {
        super.spawn(spawn);
        entity.getEquipment().setItemInMainHand(RAVAGER_CROSSBOW);
    }

}
