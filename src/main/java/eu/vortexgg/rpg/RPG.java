package eu.vortexgg.rpg;

import eu.vortexgg.rpg.boss.Boss;
import eu.vortexgg.rpg.command.SpawnerCommands;
import eu.vortexgg.rpg.data.DataManager;
import eu.vortexgg.rpg.spawner.Spawner;
import eu.vortexgg.rpg.spawner.SpawnerManager;
import eu.vortexgg.rpg.util.ConfigFile;
import eu.vortexgg.rpg.util.menu.MenuManager;
import eu.vortexgg.rpg.util.sign.SignManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RPG extends JavaPlugin {

    private static RPG instance;
    @Getter private static Economy econ;

    ConfigFile config, lang;
    DataManager data;

    public static RPG get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        config = new ConfigFile("config.yml", this);
        lang = new ConfigFile("lang.yml", this);

        setupHooks();

        registerManagers();
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        SpawnerManager.get().getAliveBosses().forEach(Boss::despawn);
        SpawnerManager.get().getData().save();
        data.disconnect();
    }

    private void setupHooks() {
        econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        Arrays.asList(Spawner.class).forEach(ConfigurationSerialization::registerClass);
    }

    private void registerManagers() {
        data = new DataManager();
        data.connect();
        new SpawnerManager(this);
        new SignManager(this);
        new MenuManager(this);
    }

    private void registerCommands() {
        new SpawnerCommands(this);
    }

    public void registerListeners() {
    }

}
