package fr.ostracraft.towns.listeners;

import fr.ostracraft.towns.types.Resident;
import fr.ostracraft.towns.types.ResidentRank;
import fr.ostracraft.towns.types.Town;
import fr.ostracraft.towns.types.TownBlock;
import fr.ostracraft.towns.utils.Messages;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Place/Break: Nouveau MIN
 * Place chest/Break chest/Ouvrir coffre: Membre MIN
 */
@SuppressWarnings("unused")
public class TownListener implements Listener {

    private static final Material[] blockedTypes = {
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.DROPPER,
            Material.DISPENSER,
            Material.BARREL,
            Material.FURNACE,
            Material.SMOKER,
            Material.SHULKER_BOX,
            Material.WHITE_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX,
            Material.LIME_SHULKER_BOX,
            Material.PINK_SHULKER_BOX,
            Material.GRAY_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX,
            Material.PURPLE_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX,
            Material.RED_SHULKER_BOX,
            Material.BLACK_SHULKER_BOX,
            Material.TNT,
    };

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // Check if old chunk is different than the new
        if(event.getTo() == null)
            return;
        if(!event.getFrom().getChunk().equals(event.getTo().getChunk())) {
            TownBlock townBlockOld = TownBlock.getTownBlockAt(event.getFrom());
            TownBlock townBlockNew = TownBlock.getTownBlockAt(event.getTo());
            if(townBlockOld.getTownId() != townBlockNew.getTownId()) {
                if(townBlockNew.getTownId() < 1) {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.TOWN_CLAIM_ENTER_ACTIONBAR.format("Territoire libre")));
                } else {
                    Town town = Town.getTownById(townBlockNew.getTownId());
                    assert town != null;
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.TOWN_CLAIM_ENTER_ACTIONBAR.format(town.getName())));
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Resident resident = Resident.getResident(player);
        Block block = event.getBlock();
        TownBlock townBlock = TownBlock.getTownBlockAt(block.getLocation());
        if(townBlock.getTownId() < 1)
            return;
        Town town = Town.getTownById(townBlock.getTownId());
        if(town == null) {
            player.sendMessage(Messages.ERROR_UNKNOWN.format());
            return;
        }
        if (townBlock.getTownId() != resident.getTownId()) {
            player.sendMessage(Messages.TOWN_NOT_YOUR_CLAIM.format(town.getName()));
            event.setCancelled(true);
            return;
        }
        if(resident.isMayor() || resident.isAssistant() || resident.isMember())
            return;
        for (Material material : blockedTypes) {
            if (block.getType().equals(material)) {
                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.MEMBRE));
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Resident resident = Resident.getResident(player);
        Block block = event.getBlock();
        TownBlock townBlock = TownBlock.getTownBlockAt(block.getLocation());
        if(townBlock.getTownId() < 1)
            return;
        Town town = Town.getTownById(townBlock.getTownId());
        if(town == null) {
            player.sendMessage(Messages.ERROR_UNKNOWN.format());
            return;
        }
        if (townBlock.getTownId() != resident.getTownId()) {
            player.sendMessage(Messages.TOWN_NOT_YOUR_CLAIM.format(town.getName()));
            event.setCancelled(true);
            return;
        }
        if(resident.isMayor() || resident.isAssistant() || resident.isMember())
            return;
        for (Material material : blockedTypes) {
            if (block.getType().equals(material)) {
                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.MEMBRE));
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Resident resident = Resident.getResident(player);
        Block block = event.getClickedBlock();
        if(block == null)
            return;
        TownBlock townBlock = TownBlock.getTownBlockAt(block.getLocation());
        if(townBlock.getTownId() < 1)
            return;
        Town town = Town.getTownById(townBlock.getTownId());
        if(town == null) {
            player.sendMessage(Messages.ERROR_UNKNOWN.format());
            return;
        }
        if (townBlock.getTownId() != resident.getTownId()) {
            player.sendMessage(Messages.TOWN_NOT_YOUR_CLAIM.format(town.getName()));
            event.setCancelled(true);
            return;
        }
        if(resident.isMayor() || resident.isAssistant() || resident.isMember())
            return;
        for (Material material : blockedTypes) {
            if (block.getType().equals(material)) {
                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.MEMBRE));
                event.setCancelled(true);
                return;
            }
        }
    }

}
