package fr.ostracraft.towns.commands;

import fr.bakaaless.api.command.CommandRunner;
import fr.bakaaless.api.command.annotations.RunCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@RunCommand(command = "town", aliases = {"ville", "t", "v"})
public class TownCommand implements CommandRunner {

    @Override
    public boolean run(CommandSender sender, List<String> args) {
        sender.sendMessage("Hello !");
        return true;
    }

    @Override
    public List<String> tabCompleter(CommandSender sender, List<String> args) {
        return Collections.singletonList("create");
    }
}
