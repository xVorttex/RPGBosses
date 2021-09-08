package eu.vortexgg.rpg.config;

import eu.vortexgg.rpg.RPG;
import eu.vortexgg.rpg.util.ConfigFile;

import java.util.List;

public interface Lang {

    static final ConfigFile CONFIG = RPG.get().getLang();

    static final List<String> BOSS_KILLED_HEADER = CONFIG.getStringList("BOSS_KILLED_HEADER");
    static final List<String> BOSS_KILLED_FOOTER = CONFIG.getStringList("BOSS_KILLED_FOOTER");
    static final String BOSS_KILLED_OTHER = CONFIG.getString("BOSS_KILLED_OTHER");
    static final String BOSS_TOP_FORMAT = CONFIG.getString("BOSS_TOP_FORMAT");

}
