package fr.ostracraft.towns.types;

import fr.ostracraft.towns.DatabaseManager;
import fr.ostracraft.towns.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class TownBlock {

    private static final HashMap<String, TownBlock> loadedBlocks = new HashMap<>();
    private final int x;
    private final int z;
    private final String world;
    private int townId;
    private boolean outpost;
    private int price;
    private String owned;

    public TownBlock(int x, int z, String world, int townId, boolean outpost, int price, String owned) {
        this.x = x;
        this.z = z;
        this.world = world;
        this.townId = townId;
        this.outpost = outpost;
        this.price = price;
        this.owned = owned;
    }

    public static TownBlock getTownBlockAt(Location location) {
        assert location.getWorld() != null;
        Chunk chunk = location.getWorld().getChunkAt(location);
        if (isCached(chunk.getX(), chunk.getZ())) {
            return getFromCache(chunk.getX(), chunk.getZ());
        }
        return new TownBlock(chunk.getX(), chunk.getZ(), location.getWorld().getName(), 0, false, 0, "");
    }

    public static boolean isCached(int x, int z) {
        return loadedBlocks.containsKey(x + ":" + z);
    }

    @Nullable
    public static TownBlock getFromCache(int x, int z) {
        if (isCached(x, z))
            return loadedBlocks.get(x + ":" + z);
        return null;
    }

    public static void fetchAll() {
        List<DatabaseResponse> responses = DatabaseManager.getAll("SELECT * FROM `" + Config.DB_PREFIX.get() + "townblocks`");
        for (DatabaseResponse response : responses) {
            TownBlock townBlock = new TownBlock(
                    response.get("x"),
                    response.get("z"),
                    response.get("world"),
                    response.get("townId"),
                    response.get("outpost"),
                    response.get("price"),
                    response.isSet("owned") && (response.get("owned") != null) ? response.get("owned") : ""
            );
            townBlock.cache();
        }
    }

    private static HashMap<String, TownBlock> getLoadedBlocks() {
        return loadedBlocks;
    }

    public static List<TownBlock> getBlocksOwned(Town town) {
        return new ArrayList<>(getLoadedBlocks().entrySet().stream()
                .filter(entry -> entry.getValue().getTownId() == town.getId())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                .values()
        );
    }

    public void cache() {
        if (!isCached(getX(), getZ()))
            loadedBlocks.put(getX() + ":" + getZ(), this);
    }

    public void removeFromCache() {
        if (isCached(getX(), getZ())) {
            loadedBlocks.remove(getX() + ":" + getZ());
        }
    }

    public void refresh() {
        this.removeFromCache();
        this.cache();
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public String getWorldName() {
        return world;
    }

    public int getTownId() {
        return townId;
    }

    public TownBlock setTownId(int townId) {
        this.townId = townId;

        if (townId == 0) {
            DatabaseManager.send("DELETE FROM `" + Config.DB_PREFIX.get() + "townblocks` WHERE `x`=? AND `z`=?", getX(), getZ());
            this.removeFromCache();
            return this;
        }

        if (isCached(getX(), getZ())) {
            DatabaseManager.send("UPDATE `" + Config.DB_PREFIX.get() + "townblocks` SET `townId`=? WHERE `x`=? AND `z`=?", townId, getX(), getZ());
        } else {
            DatabaseManager.send("INSERT INTO `" + Config.DB_PREFIX.get() + "townblocks`(`x`, `z`, `world`, `townId`, `outpost`) VALUES(?, ?, ?, ?, ?)", getX(), getZ(), getWorldName(), townId, isOutpost());
        }
        this.refresh();

        return this;
    }

    public boolean isOutpost() {
        return outpost;
    }

    public TownBlock setOutpost(boolean outpost) {
        this.outpost = outpost;

        if (getTownId() == 0) {
            throw new IllegalStateException("Trying to set an unclaimed chunk as an outpost");
        }

        if (isCached(getX(), getZ())) {
            DatabaseManager.send("UPDATE `" + Config.DB_PREFIX.get() + "townblocks` SET `outpost`=? WHERE `x`=? AND `z`=?", outpost, getX(), getZ());
        } else {
            DatabaseManager.send("INSERT INTO `" + Config.DB_PREFIX.get() + "townblocks`(`x`, `z`, `world`, `townId`, `outpost`) VALUES(?, ?, ?, ?)", getX(), getZ(), getWorldName(), getTownId(), outpost);
        }
        this.refresh();

        return this;
    }

    public Chunk getChunk() {
        return getWorld().getChunkAt(getX(), getZ());
    }

    public int getPrice() {
        return price;
    }

    public TownBlock setPrice(int price) {
        this.price = price;

        if (isCached(getX(), getZ())) {
            DatabaseManager.send("UPDATE `" + Config.DB_PREFIX.get() + "townblocks` SET `price`=? WHERE `x`=? AND `z`=?", price, getX(), getZ());
        }
        this.refresh();

        return this;
    }

    public String getOwned() {
        return owned;
    }

    public TownBlock setOwned(String owned) {
        this.owned = owned;

        if (isCached(getX(), getZ())) {
            DatabaseManager.send("UPDATE `" + Config.DB_PREFIX.get() + "townblocks` SET `owned`=? WHERE `x`=? AND `z`=?", owned, getX(), getZ());
        }
        this.refresh();

        return this;
    }

    @Override
    public String toString() {
        return "TownBlock{" +
                "x=" + x +
                ", z=" + z +
                ", townId=" + townId +
                '}';
    }
}
