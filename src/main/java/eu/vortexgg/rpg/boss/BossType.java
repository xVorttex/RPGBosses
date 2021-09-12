package eu.vortexgg.rpg.boss;

import eu.vortexgg.rpg.boss.entity.RavagerBoss;
import eu.vortexgg.rpg.boss.entity.SummonerBoss;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.EntityType;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum BossType {

    SUMMONER("summoner", SummonerBoss.class,
            new BossData().setType(EntityType.ZOMBIE)),
    SUMMONER_MINI("summoner_mini", SummonerBoss.SummonerMiniBoss.class,
            new BossData().setType(EntityType.ZOMBIE).setBaby(true).setChild(true).setBroadcastable(false)),
    RAVAGER("ravager", RavagerBoss.class,
            new BossData().setType(EntityType.PILLAGER));

    String id;
    Class<? extends Boss> clazz;
    BossData data;

    BossType(String id, Class<? extends Boss> clazz, BossData data) {
        this.id = id;
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
