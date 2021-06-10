package fr.ostracraft.towns.types;

import fr.ostracraft.towns.DatabaseManager;
import fr.ostracraft.towns.utils.Config;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class TownBlock {

    private final int x;
    private final int z;
    private int townId;
    private static final HashMap<String, TownBlock> loadedBlocks = new HashMap<>();

    TownBlock(int x, int z, int townId) {
        this.x = x;
        this.z = z;
        this.townId = townId;
    }

    public static TownBlock getTownBlockAt(Location location) {
        assert location.getWorld() != null;
        Chunk chunk = location.getWorld().getChunkAt(location);
        if (isCached(chunk.getX(), chunk.getZ())) {
            return getFromCache(chunk.getX(), chunk.getZ());
        }
        return new TownBlock(chunk.getX(), chunk.getZ(), 0);
    }

    public static boolean isCached(int x, int z) {
        return loadedBlocks.containsKey(x + ":" + z);
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
                    response.get("townId")
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

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
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
            DatabaseManager.send("INSERT INTO `" + Config.DB_PREFIX.get() + "townblocks`(`x`, `z`, `townId`) VALUES(?, ?, ?)", getX(), getZ(), townId);
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
