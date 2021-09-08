package eu.vortexgg.rpg.spawner.menu;

import com.google.common.collect.Lists;
import eu.vortexgg.rpg.spawner.Spawner;
import eu.vortexgg.rpg.spawner.SpawnerManager;
import eu.vortexgg.rpg.util.BukkitUtil;
import eu.vortexgg.rpg.util.TimeUtil;
import eu.vortexgg.rpg.util.VItemStack;
import eu.vortexgg.rpg.util.menu.item.MenuItem;
import eu.vortexgg.rpg.util.menu.type.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class SpawnersMenu extends PaginatedMenu<Spawner> {

    public SpawnersMenu() {
        super(null, "Спавнеры", 6);
        startSlot = 0;
        endSlot = 44;
        backSlot = 45;
        nextSlot = 53;
    }

    @Override
    public void refresh() {
        values = Lists.newArrayList(SpawnerManager.get().getSpawners().values());
        super.refresh();
    }

    @Override
    public void fill(int slot, int index) {
        Spawner spawner = values.get(index);
        MenuItem item = new MenuItem(new VItemStack(Material.SKELETON_SKULL, "&3&l" + spawner.getId()));
        List<String> lore = Lists.newArrayList();
        lore.add("&7Босс: &f" + spawner.getType().getDisplayName());
        if(!spawner.isAlive()) {
            lore.add("&7Статус: &c&l" + TimeUtil.formatSeconds(spawner.getRemainingUntilRespawn()));
        } else {
            lore.add("&7Статус: &a&lЖивой");
        }
        lore.add("");

        if(spawner.isAlive()) lore.add("&f&lЛевый клик &7телепортироваться");
        else lore.add("&f&lПравый клик &7возродить");

        lore.add("&f&lШифт + Левый клик &7удалить");
        lore.add("&f&lШифт + Правый клик &7изменить");

        item.addListener((t, menu, sl, p) -> {
            boolean alive = spawner.isAlive();
            if(alive && t == ClickType.LEFT) {
                p.teleport(spawner.getCurrent().getEntity());
            } else if(!alive && t == ClickType.RIGHT) {
                spawner.spawn();
                p.sendMessage(BukkitUtil.color("Вы успешно &aвозродили &fбосса " + spawner.getType().getDisplayName()));
            } else if(t == ClickType.SHIFT_RIGHT) {
                spawner.getSpawnerMenu().open(p);
            } else if(t == ClickType.SHIFT_LEFT) {
                SpawnerManager.get().getSpawners().remove(spawner.getId());
                p.sendMessage(BukkitUtil.color("Вы успешно &cудалили спавнер " + spawner.getId()));
                p.closeInventory();
            }
        });

        item.setLore(lore);
        super.setItem(slot, item);
        super.fill(slot, index);
    }
}
