package eu.vortexgg.rpg.data;

import eu.vortexgg.rpg.RPG;
import lombok.Getter;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

@Getter
public class DataManager {

    private static DataManager instance;
    private Connection connection;

    public DataManager() {
        instance = this;
    }

    public static DataManager get() {
        return instance;
    }

    /*
     * Основной метод для подключения к датабазам:
     * 1. Подключается к SQLite датабазе
     * 2. Создает таблицу убийств боссов
     * */
    public void connect() {
        try {
            File file = new File(RPG.get().getDataFolder(), "bosses.db");
            if (!file.exists()) {
                file.createNewFile();
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + file);

            update("CREATE TABLE IF NOT EXISTS bosses(ID VARCHAR(255), DATE VARCHAR(255), TOP VARCHAR(255), PRIMARY KEY (ID))");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(String sql) {
        try {
            connection.prepareStatement(sql).executeUpdate();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
