package fr.ostracraft.towns.types;

import com.zaxxer.hikari.pool.ProxyConnection;
import fr.ostracraft.towns.DatabaseManager;
import fr.ostracraft.towns.utils.Config;
import fr.ostracraft.towns.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class Town {

    private final int id;
    private String name;
    private String mayor;
    private List<String> assistants;
    private List<String> members;
    private Location spawn;
    private final long creation;

    private static HashMap<Integer, Town> loadedTowns = new HashMap<>();

    Town(int id, String name, String mayor, List<String> assistants, List<String> members, Location spawn, long creation) {
        this.id = id;
        this.name = name;
        this.mayor = mayor;
        this.assistants = assistants;
        this.members = members;
        this.spawn = spawn;
        this.creation = creation;
    }

    @Nullable
    public static Town getTownById(int id) {
        if(loadedTowns.containsKey(id))
            return loadedTowns.get(id);
        ProxyConnection connection = DatabaseManager.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + Config.DB_PREFIX.get() + "towns` WHERE `id`=" + id);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next())
                return null;
            Town town = new Town(
                    id,
                    resultSet.getString("name"),
                    resultSet.getString("mayor"),
                    Arrays.asList(resultSet.getString("assistants").split("#")),
                    Arrays.asList(resultSet.getString("members").split("#")),
                    Bukkit.getWorlds().get(0).getSpawnLocation(),
                    resultSet.getLong("creation")
            );
            loadedTowns.put(town.getId(), town);
            return town;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Town getTownNamed(String name) {
        ProxyConnection connection = DatabaseManager.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + Config.DB_PREFIX.get() + "towns` WHERE `name`=" + name)) {
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                return null;

            if (loadedTowns.containsKey(resultSet.getInt("id")))
                return loadedTowns.get(resultSet.getInt("id"));

            Town town = new Town(
                    resultSet.getInt("id"),
                    name,
                    resultSet.getString("mayor"),
                    Arrays.asList(resultSet.getString("assistants").split("#")),
                    Arrays.asList(resultSet.getString("members").split("#")),
                    StringUtil.stringToLocation(resultSet.getString("spawn")),
                    resultSet.getLong("creation")
            );
            loadedTowns.put(town.getId(), town);
            return town;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public static HashMap<Integer, Town> getLoadedTowns() {
        return loadedTowns;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Town setName(String name) {
        this.name = name;

        ProxyConnection connection = DatabaseManager.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("UPDATE `" + Config.DB_PREFIX.get() + "_towns` SET `name`='" + name + "' WHERE `id`='" + getId() + "'")) {
            statement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return this;
    }

    public String getMayor() {
        return mayor;
    }

    public Town setMayor(String mayor) {
        this.mayor = mayor;

        ProxyConnection connection = DatabaseManager.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("UPDATE `" + Config.DB_PREFIX.get() + "_towns` SET `mayor`='" + mayor + "' WHERE `id`='" + getId() + "'")) {
            statement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return this;
    }

    public List<String> getAssistants() {
        return assistants;
    }

    public Town setAssistants(List<String> assistants) {
        this.assistants = assistants;

        ProxyConnection connection = DatabaseManager.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("UPDATE `" + Config.DB_PREFIX.get() + "_towns` SET `assistants`='" + String.join("#", assistants) + "' WHERE `id`='" + getId() + "'")) {
            statement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return this;
    }

    public List<String> getMembers() {
        return members;
    }

    public Town setMembers(List<String> members) {
        this.members = members;

        ProxyConnection connection = DatabaseManager.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("UPDATE `" + Config.DB_PREFIX.get() + "_towns` SET `members`='" + String.join("#", members) + "' WHERE `id`='" + getId() + "'")) {
            statement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return this;
    }

    @Nullable
    public Location getSpawn() {
        return spawn;
    }

    public Town setSpawn(Location spawn) {
        this.spawn = spawn;

        ProxyConnection connection = DatabaseManager.getConnection();
        try (PreparedStatement statement = connection.prepareStatement("UPDATE `" + Config.DB_PREFIX.get() + "_towns` SET `spawn`='" + StringUtil.locationToString(spawn) + "' WHERE `id`='" + getId() + "'")) {
            statement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return this;
    }

    public long getCreation() {
        return creation;
    }

    @Override
    public String toString() {
        return "Town{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mayor='" + mayor + '\'' +
                ", assistants=" + assistants +
                ", members=" + members +
                ", spawn=" + spawn +
                ", creation=" + creation +
                '}';
    }
}
