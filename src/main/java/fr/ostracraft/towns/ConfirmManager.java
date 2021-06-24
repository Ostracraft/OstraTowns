package fr.ostracraft.towns;

import fr.ostracraft.towns.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ConfirmManager {

    private static final HashMap<String, Runnable> hashMap = new HashMap<>();

    public static HashMap<String, Runnable> getHashMap() {
        return hashMap;
    }

    public static void add(Player player, Runnable runnable) {
        getHashMap().remove(player.getUniqueId().toString());
        getHashMap().put(player.getUniqueId().toString(), runnable);
        player.sendMessage(Messages.TOWN_TO_CONFIRM.format());
        new Thread(() -> {
            try {
                Thread.sleep(60_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            getHashMap().remove(player.getUniqueId().toString());
        }).start();
    }

    public static boolean confirm(String uuid) {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if (player != null && player.isOnline() && getHashMap().containsKey(uuid)) {
            getHashMap().get(uuid).run();
            getHashMap().remove(uuid);
            return true;
        }
        return false;
    }
}
