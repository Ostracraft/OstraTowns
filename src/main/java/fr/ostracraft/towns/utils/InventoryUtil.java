package fr.ostracraft.towns.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {

    public static boolean hasEnough(Inventory inventory, ItemStack itemStack) {
        int amount = 0;
        int required = itemStack.getAmount();
        Material material = itemStack.getType();

        for (ItemStack content : inventory.getContents()) {
            if (content == null) continue;
            if (content.getType().equals(material)) amount += content.getAmount();
        }
        return amount >= required;
    }

    public static void take(Inventory inventory, ItemStack itemStack) {
        int amount = itemStack.getAmount();
        int removed = 0;
        Material material = itemStack.getType();
        int removeRemaining = amount;
        for (ItemStack content : inventory.getContents()) {
            if(content == null) continue;
            if (!content.getType().equals(material)) continue;
            removeRemaining = amount - removed;
            if (content.getAmount() < removeRemaining) {
                removed += content.getAmount();
                inventory.remove(content);
            } else {
                removed += removeRemaining;
                content.setAmount(content.getAmount() - removeRemaining);
            }
        }
    }

}
