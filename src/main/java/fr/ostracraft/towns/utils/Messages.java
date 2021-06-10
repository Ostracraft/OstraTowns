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
    ERROR_JOIN_1("%prefix% &cUne erreur est survenue, merci de contacter le staff &e(Code erreur &6TOWNS_JOIN_1&e)"),

    TOWN_CREATED("%prefix% &aVous avez créer la ville &e{0} &a!"),
    TOWN_ALREADY_IN_TOWN("%prefix% &cVous faites déjà parti d'une ville !"),
    TOWN_NOT_IN_TOWN("%prefix% &cVous ne faites pas parti d'une ville !"),
    TOWN_ALREADY_EXISTS("%prefix% &cUne ville avec ne nom existe déjà !"),
    TOWN_MINIMUM_BOURG("%prefix% &cCette commande est disponible à partir de &4BOURG&c. Merci de faire l'amélioration."),

    TOWN_LEAVE_MAYOR("%prefix% &cVous ne pouvez pas quitter votre ville, vous êtes le maire. Vous pouvez la supprimer avec &4/ville supprimer &cou alors définir un nouveau maire &4/ville permission set <joueur> maire&c."),
    TOWN_LEAVE_PLAYER("%prefix% &aVous avez quitté votre ville."),
    TOWN_LEAVE_OTHERS("%prefix% &e{0} &avient de quitter votre ville."),

    TOWN_RANK_INSUFFICIENT("%prefix% &cVous n'avez pas le rang requis au sein de votre ville: &4{0}"),
    TOWN_RANK_PROMOTION("%prefix% &e{0} &aa été promu &e{1} &apar &e{2} &a!"),
    TOWN_RANK_GET_HEADER("%prefix% &aVoici les membres gradés de votre ville:"),
    TOWN_RANK_GET_ITEM("&6- {0}&6: &a{1}"),

    TOWN_KICKED_ALL("%prefix% &aLe membre &e{0} &aa été expulsé de votre ville par &e{1} &a!"),
    TOWN_KICKED_VICTIM("%prefix% &cVous avez été expulsé de votre ville par &4{0} &c!"),

    TOWN_INVITE_ALREADY("%prefix% &cLe joueur &4{0} &cest déjà invité à votre ville !"),
    TOWN_INVITE_SUCCESS("%prefix% &aLe joueur &e{0} &aa été invité à votre ville."),
    TOWN_INVITE_RECEIVE("%prefix% &aVous avez été invité à rejoindre la ville &e{0}&a, faites &e/town join {0} &apour accepter cette invitation."),
    TOWN_INVITE_EXPIRED("%prefix% &cL'invitation à la ville &4{0} &ca expiré."),
    TOWN_INVITE_NOT_INVITED("%prefix% &cVous n'avez pas été invité dans la ville &4{0} &c!"),
    TOWN_INVITE_ACCEPTED("%prefix% &aLe joueur &e{0} &afait désormais parti de votre ville !"),

    TOWN_CLAIM_CAMPEMENT_LIMIT_REACHED("%prefix% &cVous avez atteint votre limite ce claim en tant que Campement: &4{0} claims maximum&c."),
    TOWN_CLAIM_ALREADY_OWNED("%prefix% &cCe claim est déjà possédé par la ville &4{0} &c!"),
    TOWN_CLAIM_CLAIMED("%prefix% &aVous avez claim le chunk &ex: {0}, z: {1}&a."),
    TOWN_NOT_YOUR_CLAIM("%prefix% &cCe chunk ne vous appartient pas, il appartient à la ville &4{0}&c."),
    TOWN_CLAIM_UNCLAIMED("%prefix% &aVous avez unclaim le chunk &ex: {0}, z: {1}&a."),
    TOWN_CLAIM_ENTER_ACTIONBAR("&6Entrée sur le territoire de &e{0}"),
    TOWN_CLAIM_NOT_ENOUGH_MONEY("%prefix% &cVous n'avez pas assez d'argent pour acheter ce claim: &4{0} pixels&c.")
    ;
    private String value;

    Messages(String value) {
        this.value = value;
    }

    public static boolean load() {
        try {
            File file = new File(OstraTowns.get().getDataFolder(), "messages.yml");
            if (!file.exists()) {
                if(!file.getParentFile().exists() && !file.getParentFile().mkdirs())
                    return false;
                if(!file.createNewFile())
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

    public String format(Object... args) {
        String s = get();
        for (int i = 0; i < args.length; i++) {
            s = s.replace("{" + i + "}", String.valueOf(args[i]));
        }
        if(!toString().equalsIgnoreCase("PREFIX"))
            s = s.replace("%prefix%", PREFIX.format());
        return StringUtil.colored(s);
    }

    public void set(String value) {
        this.value = value;
    }

}