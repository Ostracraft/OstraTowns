package fr.ostracraft.towns.commands;

import fr.bakaaless.api.command.CommandRunner;
import fr.bakaaless.api.command.annotations.RunCommand;
import fr.bakaaless.api.command.annotations.RunSubCommand;
import fr.ostracraft.towns.types.Resident;
import fr.ostracraft.towns.types.Town;
import fr.ostracraft.towns.utils.Config;
import fr.ostracraft.towns.utils.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@RunSubCommand(command = "town", subCommand = {"create", "créer", "creer", "creation"}, executor = RunCommand.ExecutorType.PLAYERS)
public class TownCreateSubCommand implements CommandRunner {

    @Override
    public boolean run(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            sender.sendMessage(Messages.INVALID_ARGUMENTS.format("Merci de spécifier le nom souhaité"));
            return true;
        }
        Resident resident = Resident.getResident(((Player) sender));
        if(resident.getTownId() > 0) {
            sender.sendMessage(Messages.TOWN_ALREADY_IN_TOWN.format());
            return true;
        }
        String name = args.get(1);
        if (name.length() > Config.TOWN_NAME_MAX_LENGHT.<Integer>get()) {
            sender.sendMessage(Messages.INVALID_ARGUMENTS.format("Le nom spécifié est trop long (Max: " + Config.TOWN_NAME_MAX_LENGHT.get() + ")"));
            return true;
        }
        if(Town.getTownNamed(name) != null) {
            sender.sendMessage(Messages.TOWN_ALREADY_EXISTS.format());
            return true;
        }
        Town town = Town.createTown(name, ((Player) sender));
        resident.setTownId(town.getId());
        sender.sendMessage(Messages.TOWN_CREATED.format(name));
        return true;
    }

    @Override
    public List<String> tabCompleter(CommandSender sender, List<String> args) {
        return null;
    }
}
