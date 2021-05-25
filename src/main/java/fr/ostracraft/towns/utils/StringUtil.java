package fr.ostracraft.towns.utils;

import org.bukkit.ChatColor;

public class StringUtil {

    public static String colored(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

}
