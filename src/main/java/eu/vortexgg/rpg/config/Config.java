package eu.vortexgg.rpg.config;

import eu.vortexgg.rpg.RPG;
import eu.vortexgg.rpg.util.ConfigFile;

public interface Config {

    static final ConfigFile CONFIG = RPG.get().getConfig();

}
