package fr.ostracraft.towns.types;

import com.zaxxer.hikari.pool.ProxyConnection;
import fr.ostracraft.towns.DatabaseManager;
import fr.ostracraft.towns.utils.Config;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

@SuppressWarnings("unused")
public class Resident {

    private final String uuid;
    private String username;
    private int townId;

    private static final HashMap<String, Resident> loadedResidents = new HashMap<>();

    Resident(String uuid, String username, int townId) {
        this.uuid = uuid;
        this.username = username;
        this.townId = townId;
    }

    @Nullable
    public static Resident getResident(String usernameOrUUID, boolean force) {
        if (loadedResidents.containsKey(usernameOrUUID) && !force)
            return loadedResidents.get(usernameOrUUID);
        ProxyConnection connection = DatabaseManager.getConnection();
        try {
            String type;
            if(usernameOrUUID.length() > 16)
                type = "uuid";
            else
                type = "username";
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + Config.DB_PREFIX.get() + "residents` WHERE `" + type + "`='" + usernameOrUUID +"'");
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next())
                return null;

            if(loadedResidents.containsKey(resultSet.getString("uuid")))
                return loadedResidents.get(resultSet.getString("uuid"));

            Resident resident = new Resident(
                    resultSet.getString("uuid"),
                    resultSet.getString("username"),
                    resultSet.getInt("townId")
            );
            loadedResidents.put(resident.getUuid(), resident);
            return resident;
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }


    public static Resident getResident(Player player) {
        return getResident(player.getUniqueId().toString(), false);
    }

    public static Resident getResident(Player player, boolean force) {
        return getResident(player.getUniqueId().toString(), force);
    }

    public static Resident createResident(Player player) {
        Resident resident = new Resident(player.getUniqueId().toString(), player.getName(), 0);
        ProxyConnection connection = DatabaseManager.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + Config.DB_PREFIX.get() + "residents`(`uuid`, `username`, `townId`) VALUES(?, ?, ?)");
            statement.setString(1, resident.getUuid());
            statement.setString(2, resident.getUsername());
            statement.setInt(3, resident.getTownId());
            statement.execute();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        loadedResidents.put(player.getUniqueId().toString(), resident);
        return resident;
    }

    public static HashMap<String, Resident> getLoadedResidents() {
        return loadedResidents;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public Resident setUsername(String username) {
        this.username = username;
        return this;
    }

    public int getTownId() {
        return townId;
    }

    public Resident setTownId(int townId) {
        this.townId = townId;
        return this;
    }

    @Override
    public String toString() {
        return "Resident{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", townId=" + townId +
                '}';
    }
}
