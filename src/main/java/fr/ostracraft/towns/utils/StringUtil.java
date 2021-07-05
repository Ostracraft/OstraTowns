package fr.ostracraft.towns.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static String colored(String input) {
        if (Bukkit.getVersion().contains("1.16")) {
            Pattern pattern = Pattern.compile("#[a-fA-F-0-9]{6}");
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                String color = input.substring(matcher.start(), matcher.end());
                input = input.replace(color, ChatColor.of(color).toString());
                matcher = pattern.matcher(input);
            }
        }
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
