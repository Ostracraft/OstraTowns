package fr.ostracraft.towns.types;

import fr.ostracraft.towns.DatabaseManager;
import fr.ostracraft.towns.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"unused", "UnusedReturnValue", "BooleanMethodIsAlwaysInverted"})
public class Resident {

    private static final HashMap<String, Resident> loadedResidents = new HashMap<>();
    private final String uuid;
    private String username;
    private int townId;

    Resident(String uuid, String username, int townId) {
        this.uuid = uuid;
        this.username = username;
        this.townId = townId;
    }

    @Nullable
    public static Resident getResident(String usernameOrUUID, boolean force) {
        if (loadedResidents.containsKey(usernameOrUUID) && !force)
            return loadedResidents.get(usernameOrUUID);
        String type;
        if (usernameOrUUID.length() > 16)
            type = "uuid";
        else
            type = "username";
        DatabaseResponse response = DatabaseManager.get("SELECT * FROM `" + Config.DB_PREFIX.get() + "residents` WHERE `" + type + "`=?", usernameOrUUID);
        if (response == null)
            return null;
        if (loadedResidents.containsKey(response.<String>get("uuid")))
            return loadedResidents.get(response.<String>get("uuid"));
        Resident resident = new Resident(
                response.get("uuid"),
                response.get("username"),
                response.<Integer>get("townId")
        );
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(resident.getUuid()));
        if (!resident.getUsername().equalsIgnoreCase(offlinePlayer.getName()))
            resident.setUsername(offlinePlayer.getName());
        if (Town.getTownById(resident.getTownId()) == null)
            resident.setTownId(0);
        loadedResidents.put(resident.getUuid(), resident);
        return resident;
    }


    public static Resident getResident(String usernameOrUUID) {
        return getResident(usernameOrUUID, false);
    }

    public static Resident getResident(Player player) {
        return getResident(player.getUniqueId().toString(), false);
    }

    public static Resident getResident(Player player, boolean force) {
        return getResident(player.getUniqueId().toString(), force);
    }

    public static Resident createResident(Player player) {
        Resident resident = new Resident(player.getUniqueId().toString(), player.getName(), 0);
        DatabaseManager.send("INSERT INTO `" + Config.DB_PREFIX.get() + "residents`(`uuid`, `username`, `townId`) VALUES(?, ?, ?)", resident.getUuid(), resident.getUsername(), resident.getTownId());
        loadedResidents.put(player.getUniqueId().toString(), resident);
        return resident;
    }

    public static HashMap<String, Resident> getLoadedResidents() {
        return loadedResidents;
    }

    public static void fetchAllResidents() {
        List<DatabaseResponse> responses = DatabaseManager.getAll("SELECT * FROM `" + Config.DB_PREFIX.get() + "residents`");
        for (DatabaseResponse response : responses) {
            if(loadedResidents.containsKey(response.<String>get("uuid")))
                continue;
            Resident resident = new Resident(
                    response.get("uuid"),
                    response.get("username"),
                    response.<Integer>get("townId")
            );
            loadedResidents.put(response.get("uuid"), resident);
        }
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public Resident setUsername(String username) {
        this.username = username;
        DatabaseManager.send("UPDATE `" + Config.DB_PREFIX.get() + "residents` SET `username`=? WHERE `uuid`=?", username, getUuid());
        return this;
    }

    public int getTownId() {
        return townId;
    }

    public Resident setTownId(int townId) {
        this.townId = townId;
        DatabaseManager.send("UPDATE `" + Config.DB_PREFIX.get() + "residents` SET `townId`=? WHERE `uuid`=?", townId, getUuid());
        return this;
    }

    public boolean isMayor() {
        if (getTownId() < 1) {
            return false;
        }
        Town town = Town.getTownById(getTownId());
        assert town != null;
        return town.getMayor().equalsIgnoreCase(getUuid());
    }

    public boolean isAssistant() {
        if (getTownId() < 1) {
            return false;
        }
        Town town = Town.getTownById(getTownId());
        assert town != null;
        return town.getAssistants().contains(getUuid());
    }

    public boolean isMember() {
        if (getTownId() < 1) {
            return false;
        }
        Town town = Town.getTownById(getTownId());
        assert town != null;
        return town.getMembers().contains(getUuid());
    }

    public boolean isAbove(Resident target) {
        if (this.isMayor())
            return true;
        if (this.isAssistant())
            return target.isMayor() == target.isAssistant();
        if (this.isMember())
            return !target.isMayor() && !target.isAssistant() && !target.isMember();
        return false;
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
