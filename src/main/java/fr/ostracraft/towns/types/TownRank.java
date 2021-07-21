package fr.ostracraft.towns.types;

import fr.ostracraft.towns.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum TownRank {
    CAMPEMENT(0, "#B65B00CAMPEMENT", 1, 1, Collections.emptyList()),
    BOURG(1, "#C3C3C3BOURG", 12, 1, Collections.singletonList(
            new ItemStack(Material.DIAMOND_BLOCK, 10)
    )),
    VILLAGE(2, "#FFE020VILLAGE", 22, 2, Arrays.asList(
            new ItemStack(Material.DIAMOND_BLOCK, 32),
            new ItemStack(Material.NETHERITE_INGOT)
    )),
    CITY(3, "#37DADBVILLE", 150, 4, Arrays.asList(
            new ItemStack(Material.DIAMOND_BLOCK, 64),
            new ItemStack(Material.NETHERITE_BLOCK, 5)
    )),
    KINGDOM(4, "#37DB72ROYAUME", 0, 7, Arrays.asList(
            new ItemStack(Material.DIAMOND_BLOCK, 128),
            new ItemStack(Material.NETHERITE_BLOCK, 20)
    ));

    private final int priority;
    private final String prefix;
    private final int maxClaims;
    private final int maxOutposts;
    private final List<ItemStack> price;

    TownRank(int priority, String prefix, int maxClaims, int maxOutposts, List<ItemStack> price) {
        this.priority = priority;
        this.prefix = prefix;
        this.maxClaims = maxClaims;
        this.maxOutposts = maxOutposts;
        this.price = price;
    }

    public int getPriority() {
        return priority;
    }

    public String getPrefix() {
        return StringUtil.colored(prefix);
    }

    public int getMaxClaims() {
        return maxClaims;
    }

    public int getMaxOutposts() {
        return maxOutposts;
    }

    public List<ItemStack> getPrice() {
        return price;
    }
}
