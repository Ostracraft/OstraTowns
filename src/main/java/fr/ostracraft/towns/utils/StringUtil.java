package fr.ostracraft.towns.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class StringUtil {

    public static String colored(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String locationToString(Location location) {
        assert location.getWorld() != null;
        return location.getX() + "#" +
                location.getY() + "#" +
                location.getZ() + "#" +
                location.getYaw() + "#" +
                location.getPitch() + "#" +
                location.getWorld().getName();
    }

    @Nullable
    public static Location stringToLocation(String string) {
        try {
            String[] array = string.split("#");
            return new Location(
                    Bukkit.getWorld(array[5]),
                    Double.parseDouble(array[0]),
                    Double.parseDouble(array[1]),
                    Double.parseDouble(array[2]),
                    Float.parseFloat(array[3]),
                    Float.parseFloat(array[4]));
        } catch (Exception e) {
            return null;
        }
    }

}
