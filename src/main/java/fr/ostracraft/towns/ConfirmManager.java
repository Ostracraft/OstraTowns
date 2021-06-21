package fr.ostracraft.towns;

import fr.ostracraft.towns.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class ConfirmManager {

    private static HashMap<String, Consumer<Player>> hashMap = new HashMap<>();

    public static HashMap<String, Consumer<Player>> getHashMap() {
        return hashMap;
    }

    public static void add(Player player, Consumer<Player> consumer) {
        getHashMap().remove(player.getUniqueId().toString());
        getHashMap().put(player.getUniqueId().toString(), consumer);
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
        if(player != null && player.isOnline() && getHashMap().containsKey(uuid)) {
            getHashMap().get(uuid).accept(player);
            getHashMap().remove(uuid);
            return true;
        }
        return false;
    }
}
