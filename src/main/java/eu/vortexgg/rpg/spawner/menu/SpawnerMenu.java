package eu.vortexgg.rpg.spawner.menu;

import com.google.common.collect.Lists;
import eu.vortexgg.rpg.boss.BossType;
import eu.vortexgg.rpg.spawner.Spawner;
import eu.vortexgg.rpg.util.BukkitUtil;
import eu.vortexgg.rpg.util.TaskUtil;
import eu.vortexgg.rpg.util.VItemStack;
import eu.vortexgg.rpg.util.menu.Menu;
import eu.vortexgg.rpg.util.menu.item.MenuItem;
import eu.vortexgg.rpg.util.menu.type.PaginatedMenu;
import eu.vortexgg.rpg.util.sign.SignManager;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpawnerMenu extends Menu {

    Spawner spawner;
    Menu bossMenu;

    public SpawnerMenu(Spawner spawner) {
        super(null, "Спавнер Эдитор", 5);
        this.spawner = spawner;
        this.bossMenu = new SpawnerBossSelectorMenu(spawner, this);

        for(int i = 0; i < 9; i++) {
            setItem(i, DEFAULT_BACKGROUND_ITEM);
        }
        for(int i = 36; i < 45; i++) {
            setItem(i, DEFAULT_BACKGROUND_ITEM);
        }

        setItem(22, createDisplayNameEditorItem(this, spawner));
        setItem(21, createBossSelectorItem(this, spawner));
        setItem(23, createLocationEditorItem(this, spawner));
    }

    @Override
    public void update() {
        setItem(13, createSpawnerInfoItem(spawner));
        super.update();
    }

    public static MenuItem createBossSelectorItem(SpawnerMenu parent, Spawner spawner) {
        MenuItem item = new MenuItem(new VItemStack(Material.ZOMBIE_HEAD, "&fИзменить тип босса"));
        List<String> lore = Lists.newArrayList();
        lore.add("&7Нажмите для изменения типа босса.");
        item.setLore(lore);
        item.addListener((type, m, slot, p) -> parent.bossMenu.open(p));
        return item;
    }

    public static MenuItem createSpawnerInfoItem(Spawner spawner) {
        MenuItem item = new MenuItem(new VItemStack(Material.END_CRYSTAL, "&fСпавнер " + spawner.getDisplayName()));
        List<String> lore = spawner.getDescription();
        lore.add("");

        lore.add("");
        lore.add("&7Используя кнопки ниже,");
        lore.add(" &7вы можете изменить спавнер.");

        item.setLore(lore);
        return item;
    }

    public static MenuItem createDisplayNameEditorItem(SpawnerMenu parent, Spawner spawner) {
        MenuItem item = new MenuItem(new VItemStack(Material.OAK_SIGN, "&fИзменить имя спавнера"));
        List<String> lore = Lists.newArrayList();
        lore.add("&7Нажмите для изменения имени");
        lore.add(" &7спавнера.");
        item.setLore(lore);

        item.addListener((type, menu, slot, p) -> SignManager.get().open(p, (player, lines) -> {
            spawner.setDisplayName(BukkitUtil.color(lines[0]));
            parent.update();
            parent.open(p);
        }, "", "Напишите новое имя", "---", "---"));

        return item;
    }

    public static MenuItem createLocationEditorItem(SpawnerMenu parent, Spawner spawner) {
        MenuItem item = new MenuItem(new VItemStack(Material.PAPER, "&fИзменить локацию спавнера"));
        List<String> lore = Lists.newArrayList();
        lore.add("&7Нажмите для установки локации,");
        lore.add(" &7спавнера на текущую вашу.");
        item.setLore(lore);
        item.addListener((type, menu, slot, p) -> {
            spawner.setSpawnLocation(p.getLocation());
            p.sendMessage(BukkitUtil.color("Вы успешно &aизменили &fлокацию спавнера"));
            p.closeInventory();
        });
        return item;
    }

    public static class SpawnerBossSelectorMenu extends PaginatedMenu<BossType> {

        private final Spawner spawner;
        private final SpawnerMenu parent;

        public SpawnerBossSelectorMenu(Spawner spawner, SpawnerMenu parent) {
            super(null, "Спавнер Эдитор > Тип босса", 6);
            this.spawner = spawner;
            this.parent = parent;
            startSlot = 0;
            endSlot = 44;
            backSlot = 45;
            nextSlot = 53;
            setMenuListener((p, inv, menu) -> {
                parent.update();
                TaskUtil.sync(() -> parent.open(p));
            });
        }

        @Override
        public void refresh() {
            values = Lists.newArrayList(BossType.values());
            super.refresh();
        }

        @Override
        public void fill(int slot, int index) {
            BossType type = values.get(index);
            MenuItem item = new MenuItem(new VItemStack(Material.ZOMBIE_HEAD, "&f&l" + type.getId()));
            List<String> lore = Lists.newArrayList();
            boolean uses = spawner.getType() == type;
            if (uses) {
                lore.add("&aИспользуется в данный момент");
                lore.add("");
                lore.add("&7Нажмите для отмены использования");
            } else {
                lore.add("&7Нажмите для установления");
            }

            item.addListener((t, menu, sl, p) -> {
                spawner.setType(type);
                parent.update();
                parent.open(p);
                p.sendMessage(BukkitUtil.color("Вы установили &7" + type.getId() + " &fдля &a" + spawner.getId() + "&f."));
                p.closeInventory();
            });

            item.setLore(lore);
            super.setItem(slot, item);
            super.fill(slot, index);
        }
    }
}
