package fr.ostracraft.towns;

import fr.ostracraft.towns.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ConfirmManager {

    private static final HashMap<UUID, Runnable> hashMap = new HashMap<>();

    public static HashMap<UUID, Runnable> getHashMap() {
        return hashMap;
    }

    public static void add(Player player, Runnable runnable) {
        getHashMap().remove(player.getUniqueId());
        getHashMap().put(player.getUniqueId(), runnable);
        player.sendMessage(Messages.TOWN_TO_CONFIRM.format());
        new Thread(() -> {
            try {
                Thread.sleep(60_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            getHashMap().remove(player.getUniqueId());
        }).start();
    }

    public static boolean confirm(String uuidString) {
        UUID uuid = UUID.fromString(uuidString);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline() && getHashMap().containsKey(uuid)) {
            getHashMap().get(uuid).run();
            getHashMap().remove(uuid);
            return true;
        }
        return false;
    }
}
