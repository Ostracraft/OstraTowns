package fr.ostracraft.towns;

import fr.bakaaless.api.inventory.InventoryAPI;
import fr.bakaaless.api.inventory.ItemAPI;
import fr.ostracraft.towns.types.Resident;
import fr.ostracraft.towns.types.Town;
import fr.ostracraft.towns.utils.Messages;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIManager {

    public static void openSettings(Player player, Resident resident) {
        if((!resident.isMayor() && !resident.isAssistant()) || resident.getTownId() < 1)
            return;
        Town town = Town.getTownById(resident.getTownId());
        assert town != null;

        InventoryAPI inventory = InventoryAPI.create(OstraTowns.get())
            .setRefresh(true)
            .setSize(27);

        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        assert borderMeta != null;
        borderMeta.setDisplayName(" ");
        border.setItemMeta(borderMeta);
        inventory.setBorder(border);

        inventory.addItem(11, o -> {
            ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.TOWN_SETTING_GUI_PVP.format(town.getSettings().isPvp() ? "&2ACTIVÉ" : "&cDÉSACTIVÉ"));
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, inventoryClickEvent -> {
            town.getSettings().setPvp(!town.getSettings().isPvp());
            town.updateSettings();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
        });

        inventory.addItem(13, o -> {
            ItemStack itemStack = new ItemStack(Material.FLINT_AND_STEEL);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.TOWN_SETTING_GUI_FIRE.format(town.getSettings().isFire() ? "&2ACTIVÉ" : "&cDÉSACTIVÉ"));
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, inventoryClickEvent -> {
            town.getSettings().setFire(!town.getSettings().isFire());
            town.updateSettings();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
        });

        inventory.addItem(15, o -> {
            ItemStack itemStack = new ItemStack(Material.RED_BED);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.TOWN_SETTING_GUI_SPAWN.format(town.getSettings().isPublicSpawn() ? "&2ACCESSIBLE" : "&cINACCESSIBLE"));
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, inventoryClickEvent -> {
            town.getSettings().setPublicSpawn(!town.getSettings().isPublicSpawn());
            town.updateSettings();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
        });

        inventory.build(player);
    }

}
