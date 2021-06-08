package fr.ostracraft.towns.commands;

import fr.ostracraft.towns.types.Resident;
import fr.ostracraft.towns.types.ResidentRank;
import fr.ostracraft.towns.types.Town;
import fr.ostracraft.towns.types.TownRank;
import fr.ostracraft.towns.utils.Config;
import fr.ostracraft.towns.utils.Messages;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TownCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.EXECUTABLE_BY_PLAYER.format());
            return true;
        }
        Player player = ((Player) sender);
        Resident resident = Resident.getResident(player);
        if (args.length == 0) {
            player.sendMessage("TOWN INFO");
            return true;
        }
        String subCommand = args[0];
        List<String> subArgs = Arrays.stream(args).filter(s -> ArrayUtils.indexOf(args, s) > 0).collect(Collectors.toList());
        switch (subCommand) {
            // Creation
            case "create":
            case "créer":
            case "creer":
            case "creation": {
                if (subArgs.size() < 1) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("Merci de spécifier le nom souhaité"));
                    break;
                }
                if (resident.getTownId() > 0) {
                    player.sendMessage(Messages.TOWN_ALREADY_IN_TOWN.format());
                    break;
                }
                String name = subArgs.get(0);
                if (name.length() > Config.TOWN_NAME_MAX_LENGHT.<Integer>get()) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("Le nom spécifié est trop long (Max: " + Config.TOWN_NAME_MAX_LENGHT.get() + ")"));
                    break;
                }
                if (Town.getTownNamed(name) != null) {
                    player.sendMessage(Messages.TOWN_ALREADY_EXISTS.format());
                    break;
                }
                Town town = Town.createTown(name, ((Player) sender));
                resident.setTownId(town.getId());
                player.sendMessage(Messages.TOWN_CREATED.format(name));
                break;
            }

            // Leaving
            case "leave":
            case "quitter":
            case "quit": {
                if (resident.getTownId() < 1) {
                    player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                    break;
                }
                Town town = Town.getTownById(resident.getTownId());
                if (town.getMayor().equalsIgnoreCase(player.getUniqueId().toString())) {
                    player.sendMessage(Messages.TOWN_LEAVE_MAYOR.format());
                    break;
                }
                town.removeAssistant(player.getUniqueId().toString());
                town.removeMember(player.getUniqueId().toString());
                resident.setTownId(0);
                player.sendMessage(Messages.TOWN_LEAVE_PLAYER.format());
                town.messageAll(Messages.TOWN_LEAVE_OTHERS.format(player.getName()));
                break;
            }

            // Kick
            case "kick":
            case "exclude": {
                if (resident.getTownId() < 1) {
                    player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                    break;
                }
                if(!resident.isAssistant() && !resident.isMayor()) {
                    player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT.toString()));
                    break;
                }
                if(subArgs.size() < 1) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("Vous devez spécifier le membre à exclure"));
                    break;
                }
                Resident target = Resident.getResident(subArgs.get(0));
                if(target == null || target.getTownId() != resident.getTownId()) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("Ce membre ne fait pas parti de votre ville"));
                    break;
                }
                if(!resident.isAbove(target)) {
                    player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format("Vous n'avez pas un rang supérieur à &4" + target.getUsername() + "&c"));
                    break;
                }
                Town town = Town.getTownById(resident.getTownId());
                town.removeAssistant(target.getUuid());
                town.removeMember(target.getUuid());
                target.setTownId(0);
                town.messageAll(Messages.TOWN_KICKED_ALL.format(target.getUsername(), resident.getUsername()));
                Player victim = Bukkit.getPlayer(UUID.fromString(target.getUuid()));
                if(victim.isOnline())
                    victim.sendMessage(Messages.TOWN_KICKED_VICTIM.format(resident.getUsername()));
                break;
            }

            // Permission management
            case "perm":
            case "permission": {
                if (resident.getTownId() < 1) {
                    player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                    break;
                }

                Town town = Town.getTownById(resident.getTownId());
                if (town == null) {
                    player.sendMessage(Messages.ERROR_UNKNOWN.format());
                    break;
                }
                if (town.getRank().equals(TownRank.CAMPEMENT)) {
                    player.sendMessage(Messages.TOWN_MINIMUM_BOURG.format());
                    break;
                }

                if (subArgs.size() < 1) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("&4/ville permission <get/set>"));
                    break;
                }
                if (subArgs.get(0).equalsIgnoreCase("set")) {
                    if (subArgs.size() < 3) {
                        player.sendMessage(Messages.INVALID_ARGUMENTS.format("&4/ville permission set <joueur> <rang>"));
                        break;
                    }
                    Resident target = Resident.getResident(subArgs.get(1));
                    if (resident.equals(target)) {
                        player.sendMessage(Messages.INVALID_ARGUMENTS.format("Vous ne pouvez pas définir votre propre rang"));
                        break;
                    }
                    if (target == null || target.getTownId() != resident.getTownId()) {
                        player.sendMessage(Messages.INVALID_ARGUMENTS.format("&cCe joueur n'est pas dans votre ville"));
                        break;
                    }
                    if (!resident.isAbove(target)) {
                        player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format("Vous n'avez pas un rang supérieur à &4" + target.getUsername() + "&c"));
                        break;
                    }
                    ResidentRank residentRank;
                    try {
                        residentRank = ResidentRank.valueOf(subArgs.get(2).toUpperCase());
                    } catch (Exception exception) {
                        residentRank = null;
                    }
                    if (residentRank == null) {
                        sender.sendMessage(Messages.INVALID_ARGUMENTS.format("Rang invalide: &4" + subArgs.get(2) + "&c, rangs: maire/assistant/membre/nouveau"));
                        break;
                    }
                    switch (residentRank) {
                        case NOUVEAU: {
                            if (resident.isMayor() || resident.isAssistant()) {
                                town.removeAssistant(target.getUuid());
                                town.removeMember(target.getUuid());

                                town.messageAll(Messages.TOWN_RANK_PROMOTION.format(target.getUsername(), residentRank.toString(), player.getName()));
                            } else {
                                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT.toString()));
                            }
                            break;
                        }
                        case MEMBRE: {
                            if (resident.isMayor() || resident.isAssistant()) {
                                town.removeAssistant(target.getUuid());
                                town.addMember(target.getUuid());

                                town.messageAll(Messages.TOWN_RANK_PROMOTION.format(target.getUsername(), residentRank.toString(), player.getName()));
                            } else {
                                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT.toString()));
                            }
                            break;
                        }
                        case ASSISTANT: {
                            if (resident.isMayor()) {
                                town.addAssistant(target.getUuid());
                                town.removeMember(target.getUuid());

                                town.messageAll(Messages.TOWN_RANK_PROMOTION.format(target.getUsername(), residentRank.toString(), player.getName()));
                            } else {
                                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.MAIRE.toString()));
                            }
                            break;
                        }
                        case MAIRE: {
                            if (resident.isMayor()) {
                                town.removeAssistant(target.getUuid());
                                town.removeMember(target.getUuid());
                                town.setMayor(target.getUuid());
                                town.addAssistant(player.getUniqueId().toString());

                                town.messageAll(Messages.TOWN_RANK_PROMOTION.format(target.getUsername(), residentRank.toString(), player.getName()));
                            } else {
                                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.MAIRE.toString()));
                            }
                            break;
                        }
                        default: {
                            player.sendMessage(Messages.ERROR_UNKNOWN.format());
                            break;
                        }
                    }

                } else if (subArgs.get(0).equalsIgnoreCase("get")) {
                    List<String> assistants = new ArrayList<>();
                    for (String assistantUUID : town.getAssistants()) {
                        if (assistantUUID.trim().length() < 1)
                            continue;
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(assistantUUID));
                        assistants.add(offlinePlayer.getName());
                    }

                    List<String> members = new ArrayList<>();
                    for (String memberUUID : town.getMembers()) {
                        if (memberUUID.trim().length() < 1)
                            continue;
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(memberUUID));
                        members.add(offlinePlayer.getName());
                    }

                    player.sendMessage(Messages.TOWN_RANK_GET_HEADER.format());
                    player.sendMessage(Messages.TOWN_RANK_GET_ITEM.format("MAIRE", resident.isMayor() ? resident.getUsername() : Resident.getResident(town.getMayor()).getUsername()));
                    player.sendMessage(Messages.TOWN_RANK_GET_ITEM.format("ASSISTANTS", assistants.size() > 0 ? String.join(", ", assistants) : "AUCUNS"));
                    player.sendMessage(Messages.TOWN_RANK_GET_ITEM.format("MEMBRES", members.size() > 0 ? String.join(", ", members) : "AUCUNS"));
                } else {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("&4/ville permission <get/set>"));
                }
                break;
            }

            default: {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Sous-commande inconnue"));
                break;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Arrays.asList("create", "leave", "permission");
    }
}
