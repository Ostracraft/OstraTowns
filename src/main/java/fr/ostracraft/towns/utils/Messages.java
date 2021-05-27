package fr.ostracraft.towns.utils;

import fr.ostracraft.towns.OstraTowns;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public enum Messages {

    PREFIX("&a&lOstra&f&lCraft &6»"),

    NO_PERM("%prefix% &cVous n'avez pas la permission requise !"),
    INVALID_ARGUMENTS("%prefix% &cErreur de syntaxe: {0}"),
    ;

    private String value;

    Messages(String value) {
        this.value = value;
    }

    public static boolean load() {
        try {
            File file = new File(OstraTowns.get().getDataFolder(), "messages.yml");
            if (!file.exists()) {
                if(!file.getParentFile().mkdirs() || !file.createNewFile())
                    return false;
            }
            YamlConfiguration config = new YamlConfiguration();
            config.load(file);
            for (Messages value : Messages.values()) {
                if (!FileUtil.addDefault(config, value.toString(), value.get())) {
                    value.set(config.getString(value.toString()));
                }
            }
            config.save(file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String get() {
        return this.value;
    }

    public String format(String... args) {
        String s = get();
        for (int i = 0; i < args.length; i++) {
            s = s.replace("{" + i + "}", args[i]);
        }
        if(!toString().equalsIgnoreCase("PREFIX"))
            s = s.replace("%prefix%", PREFIX.format());
        return StringUtil.colored(s);
    }

    public void set(String value) {
        this.value = value;
    }

}