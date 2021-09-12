package eu.vortexgg.rpg.spawner;

import eu.vortexgg.rpg.RPG;
import eu.vortexgg.rpg.util.FlatFile;
import eu.vortexgg.rpg.util.JavaUtil;

import java.util.ArrayList;

public class SpawnerFile extends FlatFile {

    public SpawnerFile(RPG plugin) {
        super("spawners", plugin);
    }

    @Override
    public void load() {
        SpawnerManager.get().getSpawners().clear();
        if (config.contains("spawners")) {
            JavaUtil.createList(config.get("spawners"), Spawner.class).forEach(Spawner::register);
        }
        RPG.get().getLogger().info("Loaded " + SpawnerManager.get().getSpawners().size() + " spawners.");
    }

    @Override
    public void save() {
        config.set("spawners", new ArrayList<>(SpawnerManager.get().getSpawners().values()));
        super.save();
    }

}
