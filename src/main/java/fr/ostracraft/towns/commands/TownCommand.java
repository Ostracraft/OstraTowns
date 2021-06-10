package fr.ostracraft.towns.commands;

import fr.ostracraft.towns.InviteManager;
import fr.ostracraft.towns.OstraTowns;
import fr.ostracraft.towns.types.*;
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
                if (name.length() > Config.TOWN_NAME_MAX_LENGTH.<Integer>get()) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("Le nom spécifié est trop long (Max: " + Config.TOWN_NAME_MAX_LENGTH.get() + ")"));
                    break;
                }
                if (Town.getTownNamed(name) != null) {
                    player.sendMessage(Messages.TOWN_ALREADY_EXISTS.format());
                    break;
                }
                double playerBalance = OstraTowns.getEconomy().getBalance(player);
                if(playerBalance < Config.TOWN_CREATION_PRICE.<Integer>get()) {
                    player.sendMessage(Messages.TOWN_CLAIM_NOT_ENOUGH_MONEY.format(Config.TOWN_CREATION_PRICE.<Integer>get()));
                    break;
                }
                Town town = Town.createTown(name, ((Player) sender));
                resident.setTownId(town.getId());
                player.sendMessage(Messages.TOWN_CREATED.format(name));
                OstraTowns.getEconomy().withdrawPlayer(player, Config.TOWN_CREATION_PRICE.<Integer>get().doubleValue());
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
                assert town != null;
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
                    player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT));
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
                assert town != null;
                town.removeAssistant(target.getUuid());
                town.removeMember(target.getUuid());
                target.setTownId(0);
                town.messageAll(Messages.TOWN_KICKED_ALL.format(target.getUsername(), resident.getUsername()));
                Player victim = Bukkit.getPlayer(UUID.fromString(target.getUuid()));
                if (victim != null && victim.isOnline())
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
                                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT));
                            }
                            break;
                        }
                        case MEMBRE: {
                            if (resident.isMayor() || resident.isAssistant()) {
                                town.removeAssistant(target.getUuid());
                                town.addMember(target.getUuid());

                                town.messageAll(Messages.TOWN_RANK_PROMOTION.format(target.getUsername(), residentRank.toString(), player.getName()));
                            } else {
                                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT));
                            }
                            break;
                        }
                        case ASSISTANT: {
                            if (resident.isMayor()) {
                                town.addAssistant(target.getUuid());
                                town.removeMember(target.getUuid());

                                town.messageAll(Messages.TOWN_RANK_PROMOTION.format(target.getUsername(), residentRank.toString(), player.getName()));
                            } else {
                                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.MAIRE));
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
                                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.MAIRE));
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

            // Invitations
            case "invite":
            case "inviter": {
                if (resident.getTownId() < 1) {
                    player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                    break;
                }
                if(!resident.isAssistant() && !resident.isMayor()) {
                    player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT));
                    break;
                }
                if (subArgs.size() < 1) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("Vous devez spécifier le membre à inviter"));
                    break;
                }
                Resident target = Resident.getResident(subArgs.get(0));
                if (target == null) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("Le membre &4" + subArgs.get(0) + " &cn'a pas été trouvé"));
                    break;
                }
                Player targetPlayer = Bukkit.getPlayer(UUID.fromString(target.getUuid()));
                if (targetPlayer == null || !targetPlayer.isOnline()) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("Le membre &4" + target.getUsername() + " &c n'est pas connecté"));
                    break;
                }
                if (target.getTownId() == resident.getTownId()) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("Le membre &4" + subArgs.get(0) + " &cfait déjà parti de votre ville"));
                    break;
                }
                Town town = Town.getTownById(resident.getTownId());
                assert town != null;
                if (InviteManager.isInvited(target, town)) {
                    player.sendMessage(Messages.TOWN_INVITE_ALREADY.format(target.getUsername()));
                    break;
                }
                InviteManager.addInvite(target, town);
                player.sendMessage(Messages.TOWN_INVITE_SUCCESS.format(target.getUsername()));
                targetPlayer.sendMessage(Messages.TOWN_INVITE_RECEIVE.format(town.getName()));
                Bukkit.getScheduler().scheduleSyncDelayedTask(OstraTowns.get(), () -> {
                    InviteManager.removeInvite(target, town);
                    targetPlayer.sendMessage(Messages.TOWN_INVITE_EXPIRED.format(town.getName()));
                }, 6000); // 5 minutes
                break;
            }
            case "join":
            case "rejoindre": {
                if(resident.getTownId() > 0) {
                    player.sendMessage(Messages.TOWN_ALREADY_IN_TOWN.format());
                    break;
                }
                if(subArgs.size() < 1) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("Merci de saisir le nom de la ville"));
                    break;
                }
                Town town = Town.getTownNamed(subArgs.get(0));
                if (town == null) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("Ville inconnue"));
                    break;
                }
                if (!InviteManager.isInvited(resident, town)) {
                    player.sendMessage(Messages.TOWN_INVITE_NOT_INVITED.format(town.getName()));
                    break;
                }
                InviteManager.removeInvite(resident, town);
                resident.setTownId(town.getId());
                town.messageAll(Messages.TOWN_INVITE_ACCEPTED.format(player.getName()));
                break;
            }

            // Claim
            case "claim": {
                if (resident.getTownId() < 1) {
                    player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                    break;
                }

                if (!resident.isMayor() && !resident.isAssistant()) {
                    player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT));
                    break;
                }

                Town town = Town.getTownById(resident.getTownId());
                if (town == null) {
                    player.sendMessage(Messages.ERROR_UNKNOWN.format());
                    break;
                }

                if (town.getRank().equals(TownRank.CAMPEMENT)) {
                    List<TownBlock> townBlocks = TownBlock.getBlocksOwned(town);
                    if (townBlocks.size() >= Config.TOWN_CAMPEMENT_MAX_CLAIMS.<Integer>get()) {
                        player.sendMessage(Messages.TOWN_CLAIM_CAMPEMENT_LIMIT_REACHED.format(Config.TOWN_CAMPEMENT_MAX_CLAIMS.<Integer>get()));
                        break;
                    }
                }

                TownBlock townBlock = TownBlock.getTownBlockAt(player.getLocation());
                if(townBlock.getTownId() > 0) {
                    Town owner = Town.getTownById(townBlock.getTownId());
                    if (owner == null)
                        player.sendMessage(Messages.ERROR_UNKNOWN.format());
                    else
                        player.sendMessage(Messages.TOWN_CLAIM_ALREADY_OWNED.format(owner.getName()));
                    break;
                }
                double playerBalance = OstraTowns.getEconomy().getBalance(player);
                if(playerBalance < Config.TOWN_CLAIM_PRICE.<Integer>get()) {
                    player.sendMessage(Messages.TOWN_CLAIM_NOT_ENOUGH_MONEY.format(Config.TOWN_CLAIM_PRICE.<Integer>get()));
                    break;
                }
                townBlock.setTownId(resident.getTownId());
                player.sendMessage(Messages.TOWN_CLAIM_CLAIMED.format(townBlock.getX(), townBlock.getZ()));
                OstraTowns.getEconomy().withdrawPlayer(player, Config.TOWN_CLAIM_PRICE.<Integer>get().doubleValue());
                break;
            }

            // Unclaim
            case "unclaim": {
                if (resident.getTownId() < 1) {
                    player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                    break;
                }

                if (!resident.isMayor() && !resident.isAssistant()) {
                    player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT));
                    break;
                }

                TownBlock townBlock = TownBlock.getTownBlockAt(player.getLocation());
                if(resident.getTownId() != townBlock.getTownId()) {
                    Town town = Town.getTownById(townBlock.getTownId());
                    player.sendMessage(Messages.TOWN_NOT_YOUR_CLAIM.format(town == null ? "Territoire libre" : town.getName()));
                    break;
                }
                townBlock.setTownId(0);
                player.sendMessage(Messages.TOWN_CLAIM_UNCLAIMED.format(townBlock.getX(), townBlock.getZ()));
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
