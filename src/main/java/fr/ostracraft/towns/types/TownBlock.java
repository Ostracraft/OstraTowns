package fr.ostracraft.towns.types;

import fr.ostracraft.towns.DatabaseManager;
import fr.ostracraft.towns.utils.Config;
import fr.ostracraft.towns.utils.Pair;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.HashMap;

@SuppressWarnings("unused")
public class TownBlock {

    private final int x;
    private final int z;
    private int townId;
    private static final HashMap<Pair<Integer, Integer>, TownBlock> loadedBlocks = new HashMap<>();

    TownBlock(int x, int z, int townId) {
        this.x = x;
        this.z = z;
        this.townId = townId;
    }

    public static TownBlock getTownBlockAt(Location location) {
        assert location.getWorld() != null;
        Chunk chunk = location.getWorld().getChunkAt(location);
        Pair<Integer, Integer> pair = new Pair<>(chunk.getX(), chunk.getZ());
        if (loadedBlocks.containsKey(pair))
            return loadedBlocks.get(pair);
        DatabaseResponse response = DatabaseManager.get("SELECT * FROM `" + Config.DB_PREFIX.get() + "townblocks` WHERE `x`=? AND `z`=?", chunk.getX(), chunk.getZ());
        TownBlock townBlock;
        if (response != null && response.isSet("townId")) {
            townBlock = new TownBlock(chunk.getX(), chunk.getZ(), response.get("townId"));
        } else {
            DatabaseManager.send("INSERT INTO `" + Config.DB_PREFIX.get() + "townblocks`(`x`, `z`,`townId`) VALUES(?, ?, ?)", chunk.getX(), chunk.getZ(), 0);
            townBlock = new TownBlock(chunk.getX(), chunk.getZ(), 0);
        }
        loadedBlocks.put(new Pair<>(chunk.getX(), chunk.getZ()), townBlock);
        return townBlock;
    }

    public static HashMap<Pair<Integer, Integer>, TownBlock> getLoadedBlocks() {
        return loadedBlocks;
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
        DatabaseManager.send("UPDATE `" + Config.DB_PREFIX.get() + "townblocks` SET `townId`=? WHERE `x`=? AND `z`=?", townId, x, z);
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
