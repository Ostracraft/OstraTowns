package fr.ostracraft.towns.commands;

import fr.bakaaless.api.command.CommandRunner;
import fr.bakaaless.api.command.annotations.RunCommand;
import fr.bakaaless.api.command.annotations.RunSubCommand;
import fr.ostracraft.towns.types.Resident;
import fr.ostracraft.towns.types.Town;
import fr.ostracraft.towns.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@RunSubCommand(command = "town", subCommand = {"leave", "quitter"}, executor = RunCommand.ExecutorType.PLAYERS)
public class TownLeaveSubCommand implements CommandRunner {

    @Override
    public boolean run(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        Resident resident = Resident.getResident(player);
        if (resident.getTownId() < 1) {
            sender.sendMessage(Messages.TOWN_NOT_IN_TOWN.format());
            return true;
        }
        Town town = Town.getTownById(resident.getTownId());
        if (town.getMayor().equalsIgnoreCase(player.getUniqueId().toString())) {
            sender.sendMessage(Messages.TOWN_CANNOT_LEAVE_MAYOR.format());
            return true;
        }
        town.removeAssistant(player.getUniqueId().toString());
        town.removeMember(player.getUniqueId().toString());
        resident.setTownId(0);
        sender.sendMessage(Messages.TOWN_LEAVE_PLAYER.format());
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            Resident resident1 = Resident.getResident(player1);
            if (resident1.getTownId() == town.getId()) {
                player1.sendMessage(Messages.TOWN_LEAVE_OTHERS.format(player.getName()));
            }
        }
        return true;
    }

    @Override
    public List<String> tabCompleter(CommandSender sender, List<String> args) {
        return null;
    }
}
