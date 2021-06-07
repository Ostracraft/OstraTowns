package fr.ostracraft.towns.commands;

import fr.bakaaless.api.command.CommandRunner;
import fr.bakaaless.api.command.annotations.RunCommand;
import fr.bakaaless.api.command.annotations.RunSubCommand;
import fr.ostracraft.towns.types.Resident;
import fr.ostracraft.towns.types.Town;
import fr.ostracraft.towns.types.ResidentRank;
import fr.ostracraft.towns.utils.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

// /town (perm|permission) -> gui
// /town (perm|permission) set <joueur> <maire/assistant/membre/nouveau> -> définir un grade
// /town (perm|permission) get <maire/assistant/membre/nouveau> -> voir les grades

@RunSubCommand(command = "town", subCommand = {"perm", "permission"}, executor = RunCommand.ExecutorType.PLAYERS)
public class TownPermSubCommand implements CommandRunner {

    @Override
    public boolean run(CommandSender sender, List<String> args) {
        Player player = ((Player) sender);
        Resident resident = Resident.getResident(player);
        if(resident.getTownId() < 1) {
            player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
            return true;
        }
        if (args.size() < 1) {
            sender.sendMessage(Messages.INVALID_ARGUMENTS.format("&4/ville permission <get/set>"));
            return true;
        }
        if (args.get(1).equalsIgnoreCase("set")) {
            if (args.size() < 3) {
                sender.sendMessage(Messages.INVALID_ARGUMENTS.format("&4/ville permission set <joueur> <rang>"));
                return true;
            }
            Resident target = Resident.getResident(args.get(2));
            if (resident.equals(target)) {
                sender.sendMessage(Messages.INVALID_ARGUMENTS.format("Vous ne pouvez pas définir votre propre rang"));
                return true;
            }
            if (target == null || target.getTownId() != resident.getTownId()) {
                sender.sendMessage(Messages.INVALID_ARGUMENTS.format("&cCe joueur n'est pas dans votre ville"));
                return true;
            }
            if (!resident.isAbove(target)) {
                sender.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format("Vous n'avez pas un rang suppérieur à &4" + target.getUsername() + "&c"));
                return true;
            }
            ResidentRank residentRank;
            try {
                residentRank = ResidentRank.valueOf(args.get(3).toUpperCase());
            } catch (Exception exception) {
                residentRank = null;
            }
            if (residentRank == null) {
                sender.sendMessage(Messages.INVALID_ARGUMENTS.format("Rang invalide: &4" + args.get(3) + "&c, rangs: maire/assistant/membre/nouveau"));
                return true;
            }
            Town town = Town.getTownById(resident.getTownId());
            if (town == null) {
                player.sendMessage(Messages.ERROR_UNKNOWN.format());
                return true;
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

        } else if (args.get(1).equalsIgnoreCase("get")) {
            Town town = Town.getTownById(resident.getTownId());
            String assistants = String.join(", ", town.getAssistants().stream().map(s -> Resident.getResident(s).getUsername()).collect(Collectors.toList()));
            String members = String.join(", ", town.getMembers().stream().map(s -> Resident.getResident(s).getUsername()).collect(Collectors.toList()));

            player.sendMessage(Messages.TOWN_RANK_GET_HEADER.format());
            player.sendMessage(Messages.TOWN_RANK_GET_ITEM.format("MAIRE", resident.isMayor() ? resident.getUsername() : Resident.getResident(town.getMayor()).getUsername()));
            player.sendMessage(Messages.TOWN_RANK_GET_ITEM.format("ASSISTANTS", assistants.trim().length() > 0 ? assistants : "AUCUNS"));
            player.sendMessage(Messages.TOWN_RANK_GET_ITEM.format("MEMBRES", members.trim().length() > 0 ? members : "AUCUNS"));
        } else {
            sender.sendMessage(Messages.INVALID_ARGUMENTS.format("&4/ville permission <get/set>"));
        }
        return true;
    }

    @Override
    public List<String> tabCompleter(CommandSender sender, List<String> args) {
//        if(args.size() == 0) {
//            return Arrays.asList("get", "set");
//        } else if (args.size() == 1) {
//            if(args.get(1).equalsIgnoreCase("get"))
//                return null;
//            return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
//        } else if (args.size() == 2) {
//            if(args.get(1).equalsIgnoreCase("get"))
//                return null;
//            return Arrays.stream(TownRank.values()).map(TownRank::toString).collect(Collectors.toList());
//        }
        return null;
    }

}
