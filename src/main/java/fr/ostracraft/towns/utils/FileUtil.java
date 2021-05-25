package fr.ostracraft.towns.utils;

import org.bukkit.configuration.file.YamlConfiguration;

public class FileUtil {

    public static boolean addDefault(YamlConfiguration config, String path, Object value) {
        if(config == null) return true;
        if(config.get(path) == null) {
            config.set(path, value);
            return true;
        }
        return false;
    }

}
