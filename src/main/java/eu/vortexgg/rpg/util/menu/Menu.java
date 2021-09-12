package eu.vortexgg.rpg.util.menu;

import com.google.common.collect.Maps;
import eu.vortexgg.rpg.util.TaskUtil;
import eu.vortexgg.rpg.util.VItemStack;
import eu.vortexgg.rpg.util.menu.item.MenuItem;
import eu.vortexgg.rpg.util.menu.item.type.UpdatableMenuItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutSetSlot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Map.Entry;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class Menu {

    public static final MenuItem DEFAULT_BACKGROUND_ITEM = new MenuItem(new VItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, ""));
    private static final ItemStack AIR = new ItemStack(Material.AIR);

    final Player owner;
    final Map<Integer, MenuItem> items = Maps.newConcurrentMap();
    final Map<Integer, UpdatableMenuItem> updatables = Maps.newConcurrentMap();
    final String title;
    final MenuItem background;
    Inventory inventory;
    int height;
    BukkitTask task;
    InventoryType type;
    MenuListener menuListener;

    public Menu(Player owner, String title, MenuItem background, InventoryType type) {
        this.owner = owner;
        this.type = type;
        this.background = background;
        this.title = title;
        this.inventory = Bukkit.createInventory(owner, type, title);
    }

    public Menu(Player owner, String title, int height, MenuItem background) {
        this.owner = owner;
        this.background = background;
        this.height = height;
        this.title = title;
        this.inventory = Bukkit.createInventory(owner, 9 * height, title);
    }

    public Menu(Player owner, String title, int height) {
        this(owner, title, height, null);
    }

    public void update() {
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, items.containsKey(i) ? items.get(i).getItemStack() : background != null ? background.getItemStack() : AIR);
        }

        if (!updatables.isEmpty()) {
            if (task != null)
                task.cancel();
            task = TaskUtil.timer(() -> {
                for (Entry<Integer, UpdatableMenuItem> entry : updatables.entrySet()) {
                    UpdatableMenuItem value = entry.getValue();
                    for (HumanEntity en : inventory.getViewers())
                        value.getRunnable().update((Player) en, value, this, entry.getKey());
                }
            }, 0, 20);
        }
    }

    public void open() {
        update();
        owner.openInventory(inventory);
        MenuManager.track(owner.getName(), this);
    }

    public void open(Player p) {
        update();
        p.openInventory(inventory);
        MenuManager.track(p.getName(), this);
    }

    public void setItem(int column, int row, MenuItem item) {
        setItem((row - 1) * 9 + column - 1, item);
    }

    public void sendFakeItem(Player p, int slot, ItemStack item) {
        EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutSetSlot(entityPlayer.activeContainer.windowId, slot, CraftItemStack.asNMSCopy(item)));
    }

    public void setHeight(int height) {
        inventory = Bukkit.createInventory(owner, 9 * height, title);
    }

    public void setItem(int index, MenuItem item) {
        updatables.remove(index);
        if (item == null) {
            items.remove(index);
            if (inventory != null) {
                inventory.setItem(index, new ItemStack(Material.AIR));
            }
            return;
        }
        if (item instanceof UpdatableMenuItem)
            updatables.put(index, (UpdatableMenuItem) item);
        items.put(index, item);
    }

    public int getSize() {
        return height * 9;
    }

}
