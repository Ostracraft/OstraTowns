package fr.ostracraft.towns.utils;

import fr.ostracraft.towns.OstraTowns;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public enum Config {

    DB_HOST("localhost"),
    DB_PORT(3306),
    DB_USER("root"),
    DB_PASSWORD("password"),
    DB_DATABASE("db_ostracraft"),
    DB_SSL(false),
    DB_PREFIX("ot_"),

    TOWN_NAME_MAX_LENGHT(25),
    ;

    private Object value;

    Config(Object value) {
        this.value = value;
    }

    @SuppressWarnings("all")
    public static boolean load() {
        try {
            File file = new File(OstraTowns.get().getDataFolder(), "config.yml");
            if (!file.exists()) {
                if(!file.getParentFile().exists() && !file.getParentFile().mkdirs())
                    return false;
                if(!file.createNewFile())
                    return false;
            }
            YamlConfiguration config = new YamlConfiguration();
            config.load(file);
            for (Config value : Config.values()) {
                if (!FileUtil.addDefault(config, value.toString(), value.get())) {
                    value.set(config.get(value.toString()));
                }
            }
            config.save(file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) this.value;
    }

    public void set(Object value) {
        this.value = value;
    }

}
