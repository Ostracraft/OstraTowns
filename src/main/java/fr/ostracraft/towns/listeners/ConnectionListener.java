package fr.ostracraft.towns.listeners;

import fr.ostracraft.towns.OstraTowns;
import fr.ostracraft.towns.types.Resident;
import fr.ostracraft.towns.utils.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try {
            Resident resident = Resident.getResident(player, true);
            if (resident == null)
                resident = Resident.createResident(player);
        } catch (Exception e) {
            OstraTowns.get().getLogger().severe("Failed to load resident " + player.getName() + " (" + player.getUniqueId() + ")");
            player.kickPlayer(Messages.ERROR_JOIN_1.format());
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Resident.getLoadedResidents().remove(event.getPlayer().getUniqueId().toString());
    }

}
