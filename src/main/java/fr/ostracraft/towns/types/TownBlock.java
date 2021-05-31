package fr.ostracraft.towns.types;

import com.zaxxer.hikari.pool.ProxyConnection;
import fr.ostracraft.towns.DatabaseManager;
import fr.ostracraft.towns.utils.Config;
import fr.ostracraft.towns.utils.Pair;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        if(loadedBlocks.containsKey(pair))
            return loadedBlocks.get(pair);
        ProxyConnection connection = DatabaseManager.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + Config.DB_PREFIX.get() + "townblocks` WHERE `x`=" + chunk.getX() + " AND `z`=" + chunk.getZ())) {
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                return new TownBlock(chunk.getX(), chunk.getZ(), 0);
            return new TownBlock(chunk.getX(), chunk.getZ(), resultSet.getInt("townId"));
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return new TownBlock(chunk.getX(), chunk.getZ(), 0);
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

        ProxyConnection connection = DatabaseManager.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("UPDATE `" + Config.DB_PREFIX.get() + "_townblocks` SET `townId`='" + townId + "' WHERE `x`='" + getX() + "' AND `z`='" + getZ() + "'")) {
            statement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

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
