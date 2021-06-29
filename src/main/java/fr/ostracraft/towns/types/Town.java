package fr.ostracraft.towns.types;

import fr.ostracraft.towns.DatabaseManager;
import fr.ostracraft.towns.utils.Config;
import fr.ostracraft.towns.utils.Messages;
import fr.ostracraft.towns.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Town {

    private static final HashMap<Integer, Town> loadedTowns = new HashMap<>();
    private final int id;
    private final long creation;
    private final TownSettings settings;
    private String name;
    private String mayor;
    private List<String> assistants;
    private List<String> members;
    private Location spawn;
    private TownRank rank;

    Town(int id, String name, String mayor, List<String> assistants, List<String> members, Location spawn, TownRank rank, TownSettings settings, long creation) {
        this.id = id;
        this.name = name;
        this.mayor = mayor;
        this.assistants = assistants;
        this.members = members;
        this.spawn = spawn;
        this.rank = rank;
        this.settings = settings;
        this.creation = creation;
    }

    @Nullable
    public static Town getTownById(int id) {
        if (loadedTowns.containsKey(id))
            return loadedTowns.get(id);
        DatabaseResponse response = DatabaseManager.get("SELECT * FROM `" + Config.DB_PREFIX.get() + "towns` WHERE `id`=?", id);
        if (response == null)
            return null;
        List<String> assistants = new ArrayList<>();
        Collections.addAll(assistants, response.<String>get("assistants").split("#"));
        List<String> members = new ArrayList<>();
        Collections.addAll(members, response.<String>get("members").split("#"));
        TownRank rank;
        try {
            rank = TownRank.valueOf(response.<String>get("rank").toUpperCase());
        } catch (Exception exception) {
            rank = TownRank.CAMPEMENT;
        }
        Town town = new Town(
                id,
                response.get("name"),
                response.get("mayor"),
                assistants,
                members,
                StringUtil.stringToLocation(response.get("spawn")),
                rank,
                TownSettings.fromString(response.get("settings")),
                response.get("creation")
        );
        loadedTowns.put(town.getId(), town);
        return town;
    }

    @Nullable
    public static Town getTownNamed(String name) {
        DatabaseResponse response = DatabaseManager.get("SELECT * FROM `" + Config.DB_PREFIX.get() + "towns` WHERE `name`=?", name);
        if (response == null)
            return null;
        if (loadedTowns.containsKey(response.<Integer>get("id")))
            return loadedTowns.get(response.<Integer>get("id"));
        List<String> assistants = new ArrayList<>();
        Collections.addAll(assistants, response.<String>get("assistants").split("#"));
        List<String> members = new ArrayList<>();
        Collections.addAll(members, response.<String>get("members").split("#"));
        TownRank rank = TownRank.valueOf(response.<String>get("rank").toUpperCase());
        Town town = new Town(
                response.get("id"),
                name,
                response.get("mayor"),
                assistants,
                members,
                StringUtil.stringToLocation(response.get("spawn")),
                rank,
                TownSettings.fromString(response.get("settings")),
                response.get("creation")
        );
        loadedTowns.put(town.getId(), town);
        return town;
    }

    public static Town createTown(String name, Player mayor) {
        if (getTownNamed(name) != null)
            throw new IllegalStateException("A town with named " + name + " already exists !");
        DatabaseManager.send("INSERT INTO `" + Config.DB_PREFIX.get() + "towns`(`name`, `mayor`, `creation`) VALUES (?, ?, ?)", name, mayor.getUniqueId().toString(), System.currentTimeMillis());
        return getTownNamed(name);
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
        DatabaseManager.send("UPDATE `" + Config.DB_PREFIX.get() + "towns` SET `name`=? WHERE `id`=?", name, getId());
        return this;
    }

    public String getMayor() {
        return mayor;
    }

    public Town setMayor(String mayor) {
        this.mayor = mayor;
        DatabaseManager.send("UPDATE `" + Config.DB_PREFIX.get() + "towns` SET `mayor`=? WHERE `id`=?", mayor, getId());
        return this;
    }

    public List<String> getAssistants() {
        return assistants;
    }

    public Town setAssistants(List<String> assistants) {
        this.assistants = assistants;
        DatabaseManager.send("UPDATE `" + Config.DB_PREFIX.get() + "towns` SET `assistants`=? WHERE `id`=?", String.join("#", assistants), getId());
        return this;
    }

    public Town addAssistant(String assistant) {
        if (!this.assistants.contains(assistant)) {
            this.assistants.add(assistant);
            setAssistants(this.assistants);
        }
        return this;
    }

    public Town removeAssistant(String assistant) {
        if (this.assistants.contains(assistant)) {
            this.assistants.remove(assistant);
            setAssistants(this.assistants);
        }
        return this;
    }

    public List<String> getMembers() {
        return members;
    }

    public Town setMembers(List<String> members) {
        this.members = members;
        DatabaseManager.send("UPDATE `" + Config.DB_PREFIX.get() + "towns` SET `members`=? WHERE `id`=?", String.join("#", members), getId());
        return this;
    }

    public Town addMember(String member) {
        if (!this.members.contains(member)) {
            this.members.add(member);
            setMembers(this.members);
        }
        return this;
    }

    public Town removeMember(String member) {
        if (this.members.contains(member)) {
            this.members.remove(member);
            setMembers(this.members);
        }
        return this;
    }

    @Nullable
    public Location getSpawn() {
        return spawn;
    }

    public Town setSpawn(Location spawn) {
        this.spawn = spawn;
        DatabaseManager.send("UPDATE `" + Config.DB_PREFIX.get() + "towns` SET `spawn`=? WHERE `id`=?", StringUtil.locationToString(spawn), getId());
        return this;
    }

    public TownRank getRank() {
        return rank;
    }

    public Town setRank(TownRank rank) {
        this.rank = rank;
        DatabaseManager.send("UPDATE `" + Config.DB_PREFIX.get() + "towns` SET `rank`=? WHERE `id`=?", rank.toString().toUpperCase(), getId());
        return this;
    }

    public TownSettings getSettings() {
        return settings;
    }

    public long getCreation() {
        return creation;
    }

    public String getFormattedCreation() {
        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(getCreation()).atZone(ZoneId.of("Europe/Paris"));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return zonedDateTime.format(dateTimeFormatter);
    }

    public List<Resident> getResidents() {
        List<Resident> residents = new ArrayList<>();
        Resident.fetchAllResidents();
        for (Resident resident : Resident.getLoadedResidents().values()) {
            if(resident.getTownId() == getId())
                residents.add(resident);
        }
        return residents;
    }

    public void messageAll(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Resident resident = Resident.getResident(player);
            if (resident.getTownId() == getId()) {
                player.sendMessage(message);
            }
        }
    }

    public void delete() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Resident resident = Resident.getResident(player);
            if (resident.getTownId() == getId()) {
                player.sendMessage(Messages.TOWN_DELETED.format(getName()));
                resident.setTownId(0);
            }
        }
        DatabaseManager.send("DELETE FROM `ot_towns` WHERE `id`=?", getId());
        loadedTowns.remove(getId());
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
