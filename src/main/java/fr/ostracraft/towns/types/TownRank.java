package fr.ostracraft.towns.types;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum TownRank {
    CAMPEMENT(1, Collections.emptyList()),
    BOURG(3, Collections.singletonList(
            new ItemStack(Material.DIAMOND_BLOCK, 16)
    )),
    VILLAGE(4, Arrays.asList(
            new ItemStack(Material.DIAMOND_BLOCK, 32),
            new ItemStack(Material.NETHERITE_INGOT)
    )),
    CITY(5, Arrays.asList(
            new ItemStack(Material.DIAMOND_BLOCK, 64),
            new ItemStack(Material.NETHERITE_BLOCK)
    )),
    KINGDOM(7, Arrays.asList(
            new ItemStack(Material.DIAMOND_BLOCK, 128),
            new ItemStack(Material.EMERALD_BLOCK, 64),
            new ItemStack(Material.NETHERITE_BLOCK, 5)
    ));

    private final int maxOutposts;
    private final List<ItemStack> price;

    TownRank(int maxOutposts, List<ItemStack> price) {
        this.maxOutposts = maxOutposts;
        this.price = price;
    }

    public int getMaxOutposts() {
        return maxOutposts;
    }

    public List<ItemStack> getPrice() {
        return price;
    }
}
