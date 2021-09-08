package eu.vortexgg.rpg.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
@Getter
@Setter
public class RewardItem {

    private ItemStack item;
    private double chance;

    public boolean isGood() {
        return ThreadLocalRandom.current().nextDouble(100) <= chance;
    }

}
