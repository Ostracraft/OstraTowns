package fr.ostracraft.towns.utils;

import fr.ostracraft.towns.OstraTowns;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public enum Messages {

    PREFIX("&a&lOstra&f&lCraft &6»"),

    NO_PERM("%prefix% &cVous n'avez pas la permission requise !"),
    EXECUTABLE_BY_CONSOLE("%prefix% &cCette commande ne peut être utilisée que par la console !"),
    EXECUTABLE_BY_PLAYER("%prefix% &cCette commande ne peut être utilisée que par des joueurs !"),
    INVALID_ARGUMENTS("%prefix% &cErreur de syntaxe: {0}."),

    ERROR_UNKNOWN("%prefix% &cUne erreur inconnue est survenue !"),
    ERROR_JOIN_1("%prefix% &cUne erreur est survenue, merci de contacter le staff &e(Code erreur &6TOWNS_JOIN_11E°"),

    TOWN_CREATED("%prefix% &aVous avez créer la ville &e{0} &a!"),
    TOWN_ALREADY_IN_TOWN("%prefix% &cVous faites déjà parti d'une ville !"),
    TOWN_ALREADY_EXISTS("%prefix% &cUne ville avec ne nom existe déjà !"),
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