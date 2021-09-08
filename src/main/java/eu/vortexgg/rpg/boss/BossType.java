package eu.vortexgg.rpg.boss;

import eu.vortexgg.rpg.boss.entity.SummonerBoss;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum BossType {

    SUMMONER("summoner", "&6&lПризыватель", SummonerBoss.class,
            new BossData().setType(EntityType.ZOMBIE).setMaxHealth(20)),
    SUMMONER_MINI("summoner_mini", "&c&lМини призыватели", SummonerBoss.SummonerMiniBoss.class,
            new BossData().setType(EntityType.ZOMBIE).setBaby(true).setChild(true).setMaxHealth(20).setBroadcastable(false));

    String id, displayName;
    Class<? extends Boss> clazz;
    BossData data;

    BossType(String id, String displayName, Class<? extends Boss> clazz, BossData data) {
        this.id = id;
        this.displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        this.clazz = clazz;
        this.data = data;
    }

    public static BossType getTypeById(String id) {
        for(BossType type : values())
            if(type.getId().equals(id))
                return type;
        return null;
    }

}
