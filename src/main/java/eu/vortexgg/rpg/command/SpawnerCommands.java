package eu.vortexgg.rpg.command;

import eu.vortexgg.rpg.RPG;
import eu.vortexgg.rpg.boss.BossType;
import eu.vortexgg.rpg.spawner.Spawner;
import eu.vortexgg.rpg.spawner.SpawnerManager;
import eu.vortexgg.rpg.util.BukkitUtil;
import eu.vortexgg.rpg.util.TimeUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnerCommands implements CommandExecutor {

    private final SpawnerManager spawnerManager;

    public SpawnerCommands(RPG plugin) {
        this.spawnerManager = SpawnerManager.get();
        plugin.getCommand("spawner").setExecutor(this);
        plugin.getCommand("spawners").setExecutor(new SpawnersCommand());
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String arg, String[] args) {
        if (s.hasPermission("admin")) {
            if (args.length == 0) {
                s.sendMessage(BukkitUtil.formatHelp("Квесты"));
                s.sendMessage("/spawner create <айди> <type> <interval>");
                return true;
            } else {
                switch (args[0].toLowerCase()) {
                    case "create": {
                        if (args.length == 4) {
                            String id = args[1].toLowerCase();

                            if (spawnerManager.getSpawners().containsKey(id)) {
                                s.sendMessage("§cТакой спавнер уже существует!");
                                return false;
                            }
                            String typeId = args[2].toLowerCase();

                            BossType type = BossType.getTypeById(typeId);
                            if (type == null) {
                                s.sendMessage("§cТакого типа не существует!");
                                return false;
                            }

                            long interval = TimeUtil.getTimeByArg(args[3]);
                            if(interval < 0) {
                                s.sendMessage("§cИнтервал не может быть нулевым!");
                                return false;
                            }

                            new Spawner(id, ((Player)s).getLocation(), type, interval);

                            s.sendMessage("§fВы §aуспешно §fсоздали спавнер! ID: " + id + " Тип: " + type.getId() + " Интервал: " + TimeUtil.formatSeconds(interval / 1000));
                            s.sendMessage("Используйте §7/spawner " + id + " §fдля изменения.");
                        }
                        return false;
                    }
                    default: {
                        Spawner spawner = spawnerManager.getSpawners().get(args[0].toLowerCase());

                        if (spawner == null) {
                            s.sendMessage("§cТакой спавнер не существует!");
                            return false;
                        }

                        spawner.getSpawnerMenu().open((Player) s);
                    }

                }
            }
        }
        return false;
    }

    private class SpawnersCommand implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender s, Command cmd, String arg, String[] args) {
            if (s.hasPermission("admin")) {
                spawnerManager.getMenu().open((Player)s);
            }
            return false;
        }

    }

}
