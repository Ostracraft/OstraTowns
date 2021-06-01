package fr.ostracraft.towns;

import fr.bakaaless.api.command.CommandManager;
import fr.bakaaless.api.command.CommandRunner;
import fr.ostracraft.towns.types.Resident;
import fr.ostracraft.towns.utils.Config;
import fr.ostracraft.towns.utils.Messages;
import fr.ostracraft.towns.utils.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class OstraTowns extends JavaPlugin {

    private CommandManager commandManager;

    @Override
    @SuppressWarnings("unused")
    public void onEnable() {
        if (!Config.load())
            getLogger().severe("Failed to load config ! Using default values.");
        if (!Messages.load())
            getLogger().severe("Failed to load messages ! Using default values.");
        DatabaseManager.init();
        this.registerCommands();
        this.registerListeners();

        // Store in cache players that already connected
        for (Player player : Bukkit.getOnlinePlayers()) {
            Resident resident = Resident.getResident(player);
        }
    }

    @SuppressWarnings("unchecked")
    private void registerCommands() {
        this.commandManager = new CommandManager(this);
        List<Class<?>> classes = ReflectionUtil.getClasses(getClass().getPackageName() + ".commands");
        for (Class<?> clazz : classes) {
            if(CommandRunner.class.isAssignableFrom(clazz))
                this.commandManager.registerRunners((Class<? extends CommandRunner>) clazz);
        }
        CommandManager.Messages.PREFIX_ERROR.setMessage(Messages.PREFIX.format());
        CommandManager.Messages.ERROR_COMMAND_PERMISSION.setMessage(Messages.NO_PERM.format());
        CommandManager.Messages.ERROR_COMMAND_EXECUTOR_CONSOLE.setMessage(Messages.EXECUTABLE_BY_CONSOLE.format());
        CommandManager.Messages.ERROR_COMMAND_EXECUTOR_PLAYER.setMessage(Messages.EXECUTABLE_BY_PLAYER.format());
        CommandManager.Messages.ERROR_COMMAND_UNKNOWN.setMessage(Messages.ERROR_UNKNOWN.format());
        CommandManager.Messages.ERROR_COMMAND_NEXISTS.setMessage(Messages.ERROR_UNKNOWN.format());
        CommandManager.Messages.ERROR_COMMAND_ARGUMENTS.setMessage(Messages.INVALID_ARGUMENTS.format("Il faut {1} arguments"));
    }

    private void registerListeners() {
        List<Class<?>> classes = ReflectionUtil.getClasses(getClass().getPackageName() + ".listeners");
        for (Class<?> clazz : classes) {
            if(!Listener.class.isAssignableFrom(clazz))
                continue;
            try {
                Listener listener = (Listener) clazz.getConstructor().newInstance();
                getServer().getPluginManager().registerEvents(listener, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static OstraTowns get() {
        return getPlugin(OstraTowns.class);
    }

    @NotNull
    @Override
    public File getFile() {
        return super.getFile();
    }
}
