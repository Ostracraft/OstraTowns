package fr.ostracraft.towns.commands;

import fr.ostracraft.towns.ConfirmManager;
import fr.ostracraft.towns.GUIManager;
import fr.ostracraft.towns.InviteManager;
import fr.ostracraft.towns.OstraTowns;
import fr.ostracraft.towns.types.*;
import fr.ostracraft.towns.utils.Config;
import fr.ostracraft.towns.utils.Messages;
import fr.ostracraft.towns.utils.TriConsumer;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
                SubCommandExecutor.CREATION.getExecutor().accept(player, resident, subArgs);
                break;
            }

            // Delete
            case "delete":
            case "disband": {
                SubCommandExecutor.DELETE.getExecutor().accept(player, resident, subArgs);
                break;
            }

            // Leaving
            case "leave":
            case "quitter":
            case "quit": {
                SubCommandExecutor.LEAVE.getExecutor().accept(player, resident, subArgs);
                break;
            }

            // Info
            case "information":
            case "info": {
                SubCommandExecutor.INFO.getExecutor().accept(player, resident, subArgs);
                break;
            }
            // Here
            case "here":
            case "ici": {
                SubCommandExecutor.HERE.getExecutor().accept(player, resident, subArgs);
                break;
            }

            // Kick
            case "kick":
            case "exclure": {
                SubCommandExecutor.KICK.getExecutor().accept(player, resident, subArgs);
                break;
            }

            // Permission management
            case "perm":
            case "permission": {
                SubCommandExecutor.PERMISSION.getExecutor().accept(player, resident, subArgs);
                break;
            }

            // Invitations
            case "invite":
            case "inviter": {
                SubCommandExecutor.INVITE.getExecutor().accept(player, resident, subArgs);
                break;
            }
            case "join":
            case "rejoindre": {
                SubCommandExecutor.JOIN.getExecutor().accept(player, resident, subArgs);
                break;
            }

            // Claim
            case "claim": {
                SubCommandExecutor.CLAIM.getExecutor().accept(player, resident, subArgs);
                break;
            }

            // Unclaim
            case "unclaim": {
                SubCommandExecutor.UNCLAIM.getExecutor().accept(player, resident, subArgs);
                break;
            }

            // Outpost teleport
            case "outpost":
            case "teleport":
            case "tp": {
                SubCommandExecutor.TELEPORT.getExecutor().accept(player, resident, subArgs);
                break;
            }

            // Confirm
            case "confirm":
            case "confirmer": {
                if (!ConfirmManager.confirm(player.getUniqueId().toString())) {
                    player.sendMessage(Messages.TOWN_NOTHING_TO_CONFIRM.format());
                }
                break;
            }

            // SetSpawn
            case "setspawn": {
                SubCommandExecutor.SETSPAWN.getExecutor().accept(player, resident, subArgs);
                break;
            }

            // Spawn
            case "spawn":
            case "s": {
                SubCommandExecutor.SPAWN.getExecutor().accept(player, resident, subArgs);
                break;
            }

            case "settings":
            case "parametres": {
                SubCommandExecutor.SETTINGS.getExecutor().accept(player, resident, subArgs);
                break;
            }

            case "upgrade":
            case "ameliorer": {
                SubCommandExecutor.UPGRADE.getExecutor().accept(player, resident, subArgs);
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
        if (args.length == 1) {
            return Arrays.asList("claim", "confirm", "create", "delete", "invite", "kick", "leave", "outpost", "permission", "setspawn", "settings", "spawn", "unclaim", "upgrade");
        } else {
            String currentArg = args[0];
            if (currentArg.equalsIgnoreCase("kick") ||
                    currentArg.equalsIgnoreCase("exclure") ||
                    currentArg.equalsIgnoreCase("invite") ||
                    currentArg.equalsIgnoreCase("inviter")) {
                return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
            } else if (currentArg.equalsIgnoreCase("permission") || currentArg.equalsIgnoreCase("perm")) {
                if (args.length == 2) {
                    return Arrays.asList("get", "set");
                } else {
                    if (args[1].equalsIgnoreCase("set")) {
                        if (args.length == 3) {
                            return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
                        } else {
                            return Arrays.stream(ResidentRank.values()).map(ResidentRank::toString).collect(Collectors.toList());
                        }
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    public enum SubCommandExecutor {
        CREATION((player, resident, subArgs) -> {
            if (subArgs.size() < 1) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Merci de spécifier le nom souhaité"));
                return;
            }
            if (resident.getTownId() > 0) {
                player.sendMessage(Messages.TOWN_ALREADY_IN_TOWN.format());
                return;
            }
            String name = subArgs.get(0);
            if (name.length() > Config.TOWN_NAME_MAX_LENGTH.<Integer>get()) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Le nom spécifié est trop long (Max: " + Config.TOWN_NAME_MAX_LENGTH.get() + ")"));
                return;
            }
            if (Town.getTownNamed(name) != null) {
                player.sendMessage(Messages.TOWN_ALREADY_EXISTS.format());
                return;
            }
            double playerBalance = OstraTowns.getEconomy().getBalance(player);
            if (playerBalance < Config.TOWN_CREATION_PRICE.<Integer>get()) {
                player.sendMessage(Messages.TOWN_CLAIM_NOT_ENOUGH_MONEY.format(Config.TOWN_CREATION_PRICE.<Integer>get()));
                return;
            }
            Town town = Town.createTown(name, player);
            resident.setTownId(town.getId());
            player.sendMessage(Messages.TOWN_CREATED.format(name));
            OstraTowns.getEconomy().withdrawPlayer(player, Config.TOWN_CREATION_PRICE.<Integer>get().doubleValue());
        }),
        DELETE((player, resident, subArgs) -> {
            if (resident.getTownId() < 1) {
                player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                return;
            }
            if (!resident.isMayor()) {
                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.MAIRE));
                return;
            }
            ConfirmManager.add(player, () -> {
                Town town = Town.getTownById(resident.getTownId());
                assert town != null;
                town.delete();
            });
        }),
        LEAVE((player, resident, subArgs) -> {
            if (resident.getTownId() < 1) {
                player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                return;
            }
            Town town = Town.getTownById(resident.getTownId());
            assert town != null;
            if (town.getMayor().equalsIgnoreCase(player.getUniqueId().toString())) {
                player.sendMessage(Messages.TOWN_LEAVE_MAYOR.format());
                return;
            }
            town.removeAssistant(player.getUniqueId().toString());
            town.removeMember(player.getUniqueId().toString());
            resident.setTownId(0);
            player.sendMessage(Messages.TOWN_LEAVE_PLAYER.format());
            town.messageAll(Messages.TOWN_LEAVE_OTHERS.format(player.getName()));
        }),
        INFO((player, resident, subArgs) -> {
            Town town;
            if (subArgs.size() < 1) {
                if (resident.getTownId() < 1) {
                    player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                    return;
                }
                town = Town.getTownById(resident.getTownId());
            } else {
                town = Town.getTownNamed(subArgs.get(0));
            }
            if (town == null) {
                if (subArgs.size() < 1)
                    player.sendMessage(Messages.ERROR_UNKNOWN.format());
                else
                    player.sendMessage(Messages.TOWN_NOT_EXISTS.format(subArgs.get(0)));
                return;
            }
            int claims = TownBlock.getBlocksOwned(town).size();
            long outposts = TownBlock.getBlocksOwned(town)
                    .stream().filter(TownBlock::isOutpost).count();
            String mayor = Resident.getResident(town.getMayor()).getUsername();
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
            List<String> news = town.getResidents()
                    .stream().filter(resident1 -> !resident1.isMayor() && !resident1.isAssistant() && !resident1.isMember())
                    .map(Resident::getUsername)
                    .collect(Collectors.toList());

            player.sendMessage(Messages.TOWN_INFO_HEADER.format(town.getName()));
            player.sendMessage(Messages.TOWN_INFO_CREATION.format(town.getFormattedCreation()));
            player.sendMessage(Messages.TOWN_INFO_RANK.format(town.getRank()));
            player.sendMessage(Messages.TOWN_INFO_CLAIMS.format(claims, outposts));
            player.sendMessage(Messages.TOWN_INFO_MAYOR.format(mayor));
            player.sendMessage(Messages.TOWN_INFO_ASSISTANTS.format(assistants.size() > 0 ? String.join(", ", assistants) : "&fAUCUN"));
            player.sendMessage(Messages.TOWN_INFO_MEMBERS.format(members.size() > 0 ? String.join(", ", members) : "&fAUCUN"));
            player.sendMessage(Messages.TOWN_INFO_NEWS.format(news.size() > 0 ? String.join(", ", news) : "&fAUCUN"));
        }),
        HERE((player, resident, subArgs) -> {
            TownBlock townBlock = TownBlock.getTownBlockAt(player.getLocation());
            Town town = Town.getTownById(townBlock.getTownId());
            if (town == null)
                player.sendMessage(Messages.TOWN_NOT_CLAIMED.format());
            else
                INFO.getExecutor().accept(player, resident, Collections.singletonList(town.getName()));
        }),
        KICK((player, resident, subArgs) -> {
            if (resident.getTownId() < 1) {
                player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                return;
            }
            if (!resident.isAssistant() && !resident.isMayor()) {
                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT));
                return;
            }
            if (subArgs.size() < 1) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Vous devez spécifier le membre à exclure"));
                return;
            }
            Resident target = Resident.getResident(subArgs.get(0));
            if (target == null || target.getTownId() != resident.getTownId()) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Ce membre ne fait pas parti de votre ville"));
                return;
            }
            if (!resident.isAbove(target)) {
                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format("Vous n'avez pas un rang supérieur à &4" + target.getUsername() + "&c"));
                return;
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
        }),
        PERMISSION((player, resident, subArgs) -> {
            if (resident.getTownId() < 1) {
                player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                return;
            }

            Town town = Town.getTownById(resident.getTownId());
            if (town == null) {
                player.sendMessage(Messages.ERROR_UNKNOWN.format());
                return;
            }
            if (town.getRank().equals(TownRank.CAMPEMENT)) {
                player.sendMessage(Messages.TOWN_MINIMUM_BOURG.format());
                return;
            }

            if (subArgs.size() < 1) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("&4/ville permission <get/set>"));
                return;
            }
            if (subArgs.get(0).equalsIgnoreCase("set")) {
                if (subArgs.size() < 3) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("&4/ville permission set <joueur> <rang>"));
                    return;
                }
                Resident target = Resident.getResident(subArgs.get(1));
                if (resident.equals(target)) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("Vous ne pouvez pas définir votre propre rang"));
                    return;
                }
                if (target == null || target.getTownId() != resident.getTownId()) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("&cCe joueur n'est pas dans votre ville"));
                    return;
                }
                if (!resident.isAbove(target)) {
                    player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format("Vous n'avez pas un rang supérieur à &4" + target.getUsername() + "&c"));
                    return;
                }
                ResidentRank residentRank;
                try {
                    residentRank = ResidentRank.valueOf(subArgs.get(2).toUpperCase());
                } catch (Exception exception) {
                    residentRank = null;
                }
                if (residentRank == null) {
                    player.sendMessage(Messages.INVALID_ARGUMENTS.format("Rang invalide: &4" + subArgs.get(2) + "&c, rangs: maire/assistant/membre/nouveau"));
                    return;
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
                player.sendMessage(Messages.TOWN_RANK_GET_ITEM.format("ASSISTANTS", assistants.size() > 0 ? String.join(", ", assistants) : "&fAUCUN"));
                player.sendMessage(Messages.TOWN_RANK_GET_ITEM.format("MEMBRES", members.size() > 0 ? String.join(", ", members) : "&fAUCUN"));
            } else {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("&4/ville permission <get/set>"));
            }
        }),
        INVITE((player, resident, subArgs) -> {
            if (resident.getTownId() < 1) {
                player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                return;
            }
            if (!resident.isAssistant() && !resident.isMayor()) {
                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT));
                return;
            }
            if (subArgs.size() < 1) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Vous devez spécifier le membre à inviter"));
                return;
            }
            Resident target = Resident.getResident(subArgs.get(0));
            if (target == null) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Le membre &4" + subArgs.get(0) + " &cn'a pas été trouvé"));
                return;
            }
            Player targetPlayer = Bukkit.getPlayer(UUID.fromString(target.getUuid()));
            if (targetPlayer == null || !targetPlayer.isOnline()) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Le membre &4" + target.getUsername() + " &c n'est pas connecté"));
                return;
            }
            if (target.getTownId() == resident.getTownId()) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Le membre &4" + subArgs.get(0) + " &cfait déjà parti de votre ville"));
                return;
            }
            Town town = Town.getTownById(resident.getTownId());
            assert town != null;
            if (InviteManager.isInvited(target, town)) {
                player.sendMessage(Messages.TOWN_INVITE_ALREADY.format(target.getUsername()));
                return;
            }
            InviteManager.addInvite(target, town);
            player.sendMessage(Messages.TOWN_INVITE_SUCCESS.format(target.getUsername()));
            targetPlayer.sendMessage(Messages.TOWN_INVITE_RECEIVE.format(town.getName()));
            Bukkit.getScheduler().scheduleSyncDelayedTask(OstraTowns.get(), () -> {
                InviteManager.removeInvite(target, town);
                targetPlayer.sendMessage(Messages.TOWN_INVITE_EXPIRED.format(town.getName()));
            }, 6000); // 5 minutes
        }),
        JOIN((player, resident, subArgs) -> {
            if (resident.getTownId() > 0) {
                player.sendMessage(Messages.TOWN_ALREADY_IN_TOWN.format());
                return;
            }
            if (subArgs.size() < 1) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Merci de saisir le nom de la ville"));
                return;
            }
            Town town = Town.getTownNamed(subArgs.get(0));
            if (town == null) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Ville inconnue"));
                return;
            }
            if (!InviteManager.isInvited(resident, town)) {
                player.sendMessage(Messages.TOWN_INVITE_NOT_INVITED.format(town.getName()));
                return;
            }
            InviteManager.removeInvite(resident, town);
            resident.setTownId(town.getId());
            town.messageAll(Messages.TOWN_INVITE_ACCEPTED.format(player.getName()));
        }),
        CLAIM((player, resident, subArgs) -> {
            if (resident.getTownId() < 1) {
                player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                return;
            }

            if (!resident.isMayor() && !resident.isAssistant()) {
                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT));
                return;
            }

            Town town = Town.getTownById(resident.getTownId());
            if (town == null) {
                player.sendMessage(Messages.ERROR_UNKNOWN.format());
                return;
            }

            if (town.getRank().equals(TownRank.CAMPEMENT)) {
                List<TownBlock> townBlocks = TownBlock.getBlocksOwned(town);
                if (townBlocks.size() >= Config.TOWN_CAMPEMENT_MAX_CLAIMS.<Integer>get()) {
                    player.sendMessage(Messages.TOWN_CLAIM_CAMPEMENT_LIMIT_REACHED.format(Config.TOWN_CAMPEMENT_MAX_CLAIMS.<Integer>get()));
                    return;
                }
            }

            TownBlock townBlock = TownBlock.getTownBlockAt(player.getLocation());
            if (townBlock.getTownId() > 0) {
                Town owner = Town.getTownById(townBlock.getTownId());
                if (owner == null)
                    player.sendMessage(Messages.ERROR_UNKNOWN.format());
                else
                    player.sendMessage(Messages.TOWN_CLAIM_ALREADY_OWNED.format(owner.getName()));
                return;
            }

            boolean isOutpost = subArgs.size() > 0 && subArgs.get(0).equalsIgnoreCase("outpost");

            boolean isSingle = true;
            // Check for townblocks above, below, at left and at right
            mainFor:
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (Math.abs(i) == Math.abs(j))
                        continue;
                    assert player.getLocation().getWorld() != null;
                    Location currentLocation = player.getLocation().getWorld().getChunkAt(townBlock.getX() + i, townBlock.getZ() + j).getBlock(0, 0, 0).getLocation();
                    TownBlock currentBlock = TownBlock.getTownBlockAt(currentLocation);
                    if (currentBlock.getTownId() == resident.getTownId()) {
                        isSingle = false;
                        break mainFor;
                    }
                }
            }
            if (isSingle && !isOutpost) {
                player.sendMessage(Messages.TOWN_CLAIM_SINGLE.format());
                return;
            }

            if (isOutpost) {
                List<TownBlock> outposts = TownBlock.getBlocksOwned(town)
                        .stream().filter(TownBlock::isOutpost)
                        .collect(Collectors.toList());
                if (outposts.size() >= town.getRank().getMaxOutposts()) {
                    player.sendMessage(Messages.TOWN_CLAIM_MAX_OUTPOST_REACHED.format(town.getRank().getMaxOutposts()));
                    return;
                }
            }

            int price = isOutpost ? Config.TOWN_OUTPOST_PRICE.get() : Config.TOWN_CLAIM_PRICE.get();
            double playerBalance = OstraTowns.getEconomy().getBalance(player);
            if (playerBalance < price) {
                player.sendMessage(Messages.TOWN_CLAIM_NOT_ENOUGH_MONEY.format(price));
                return;
            }
            townBlock.setTownId(resident.getTownId());
            if (isOutpost) {
                player.sendMessage(Messages.TOWN_CLAIM_OUTPOST_CLAIMED.format(townBlock.getX(), townBlock.getZ()));
                townBlock.setOutpost(true);
            } else {
                player.sendMessage(Messages.TOWN_CLAIM_CLAIMED.format(townBlock.getX(), townBlock.getZ()));
            }
            OstraTowns.getEconomy().withdrawPlayer(player, price);
        }),
        UNCLAIM((player, resident, subArgs) -> {
            if (resident.getTownId() < 1) {
                player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                return;
            }

            if (!resident.isMayor() && !resident.isAssistant()) {
                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT));
                return;
            }

            TownBlock townBlock = TownBlock.getTownBlockAt(player.getLocation());
            if (resident.getTownId() != townBlock.getTownId()) {
                Town town = Town.getTownById(townBlock.getTownId());
                player.sendMessage(Messages.TOWN_NOT_YOUR_CLAIM.format(town == null ? "Territoire libre" : town.getName()));
                return;
            }
            townBlock.setTownId(0);
            player.sendMessage(Messages.TOWN_CLAIM_UNCLAIMED.format(townBlock.getX(), townBlock.getZ()));
        }),
        TELEPORT((player, resident, subArgs) -> {
            if (resident.getTownId() < 1) {
                player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                return;
            }

            if (subArgs.size() < 1) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Merci de saisir le numéro de l'outpost"));
                return;
            }
            int outpostId;
            try {
                outpostId = Integer.parseInt(subArgs.get(0));
            } catch (NumberFormatException numberFormatException) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Le nombre fournis n'est pas valide"));
                return;
            }
            if (outpostId < 1) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Le numéro de l'outpost doit être supérieur à 0"));
                return;
            }
            Town town = Town.getTownById(resident.getTownId());
            List<TownBlock> townBlocks = TownBlock.getBlocksOwned(town);
            List<TownBlock> outposts = townBlocks.stream().filter(TownBlock::isOutpost).collect(Collectors.toList());
            TownBlock outpost = outposts.get(outpostId - 1);
            if (outpost == null) {
                player.sendMessage(Messages.INVALID_ARGUMENTS.format("Vous n'avez pas d'outpost portant ce numéro"));
                return;
            }
            Chunk chunk = outpost.getChunk();
            Location location = chunk.getBlock(8, 0, 8).getLocation();
            player.teleport(outpost.getWorld().getHighestBlockAt(location).getLocation().add(0, 1, 0));
            player.sendMessage(Messages.TOWN_TELEPORTED.format());
        }),
        SETSPAWN((player, resident, subArgs) -> {
            if (resident.getTownId() < 1) {
                player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                return;
            }
            if (!resident.isMayor()) {
                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.MAIRE));
                return;
            }
            TownBlock townBlock = TownBlock.getTownBlockAt(player.getLocation());
            if (townBlock.getTownId() != resident.getTownId()) {
                String owner = Town.getTownById(townBlock.getTownId()) == null ? "Territoire libre" : Town.getTownById(townBlock.getTownId()).getName();
                player.sendMessage(Messages.TOWN_NOT_YOUR_CLAIM.format(owner));
                return;
            }
            Town town = Town.getTownById(resident.getTownId());
            assert town != null;
            town.setSpawn(player.getLocation());
            player.sendMessage(Messages.TOWN_SETTING_UPDATED.format("Spawn"));
        }),
        SPAWN((player, resident, subArgs) -> {
            if (subArgs.size() < 1) {
                if (resident.getTownId() < 1) {
                    player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                    return;
                }
                Town town = Town.getTownById(resident.getTownId());
                assert town != null;
                if (town.getSpawn() == null) {
                    player.sendMessage(Messages.TOWN_SETTING_NO_SPAWN.format(town.getName()));
                    return;
                }
                player.teleport(town.getSpawn());
                player.sendMessage(Messages.TOWN_TELEPORTED.format());
            } else {
                Town town = Town.getTownNamed(subArgs.get(0));
                if (town == null) {
                    player.sendMessage(Messages.TOWN_NOT_EXISTS.format(subArgs.get(0)));
                    return;
                }
                if (town.getSpawn() == null || (town.getId() != resident.getTownId() && !town.getSettings().isPublicSpawn())) {
                    player.sendMessage(Messages.TOWN_SETTING_NO_SPAWN.format(town.getName()));
                    return;
                }
                player.teleport(town.getSpawn());
                player.sendMessage(Messages.TOWN_TELEPORTED.format());
            }
        }),
        SETTINGS((player, resident, subArgs) -> {
            if (resident.getTownId() < 1) {
                player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                return;
            }
            if (!resident.isMayor() && !resident.isAssistant()) {
                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT));
                return;
            }
            GUIManager.openSettings(player, resident);
        }),
        UPGRADE((player, resident, subArgs) -> {
            if (resident.getTownId() < 1) {
                player.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
                return;
            }
            if (!resident.isMayor() && !resident.isAssistant()) {
                player.sendMessage(Messages.TOWN_RANK_INSUFFICIENT.format(ResidentRank.ASSISTANT));
                return;
            }
            GUIManager.openUpgrades(player, resident);
        });

        private final TriConsumer<Player, Resident, List<String>> executor;

        SubCommandExecutor(TriConsumer<Player, Resident, List<String>> executor) {
            this.executor = executor;
        }

        public TriConsumer<Player, Resident, List<String>> getExecutor() {
            return executor;
        }
    }

}
