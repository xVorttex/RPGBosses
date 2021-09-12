package eu.vortexgg.rpg.util.sign;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenSignEditor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

@Getter
public class SignManager implements Listener {

    static SignManager instance;
    Map<String, Pair<SignCallback, Location>> editors = Maps.newHashMap();
    List<String> opened = Lists.newArrayList();

    public SignManager(Plugin plugin) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.UPDATE_SIGN) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player p = event.getPlayer();
                if (hasEditor(p)) {
                    close(p, event.getPacket().getStringArrays().read(0));
                }
            }

        });
        Bukkit.getPluginManager().registerEvent(PlayerQuitEvent.class, this, EventPriority.HIGHEST, (listener, event) ->  {
            String name = ((PlayerQuitEvent) event).getPlayer().getName();
            opened.remove(name);
            editors.remove(name);
        }, plugin, true);

        instance = this;
    }

    public static SignManager get() {
        return instance;
    }

    public void open(Player p, SignCallback callback, String... lines) {
        Location location = p.getLocation().add(0.0, 50.0, 0.0);
        p.sendBlockChange(location, Material.OAK_WALL_SIGN, (byte) 0);
        if (lines != null && lines.length == 4)
            p.sendSignChange(location, lines);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenSignEditor(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ())));
        opened.add(p.getName());
        editors.put(p.getName(), Pair.of(callback, location));
    }

    public void close(Player p, String[] lines) {
        Pair<SignCallback, Location> editor = editors.remove(p.getName());
        if (editor != null) {
            editor.getLeft().update(p, lines);
            Location loc = editor.getRight();
            Block block = loc.getBlock();
            p.sendBlockChange(loc, block.getType(), block.getData());
        }
    }

    public boolean hasEditor(Player p) {
        return editors.containsKey(p.getName()) && !opened.remove(p.getName());
    }

    public interface SignCallback {
        void update(Player p, String[] lines);
    }

}
