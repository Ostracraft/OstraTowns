package fr.ostracraft.towns;

import fr.ostracraft.towns.types.Resident;
import fr.ostracraft.towns.types.TownsCommand;
import fr.ostracraft.towns.utils.Config;
import fr.ostracraft.towns.utils.Messages;
import fr.ostracraft.towns.utils.ReflectionUtil;
import fr.ostracraft.towns.utils.annotations.CommandProperties;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

public class OstraTowns extends JavaPlugin {

    private static SimpleCommandMap commandMap;

    static {
        try {
            final Field bukkitCommandMapField;
            bukkitCommandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMapField.setAccessible(true);
            commandMap = (SimpleCommandMap) bukkitCommandMapField.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

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

    private void registerCommands() {
        List<Class<?>> classes = ReflectionUtil.getClasses(getClass().getPackageName() + ".commands");
        for (Class<?> clazz : classes) {
            if (!TownsCommand.class.isAssignableFrom(clazz))
                continue;
            try {
                TownsCommand command = (TownsCommand) clazz.getConstructor().newInstance();
                CommandProperties commandProperties = clazz.getAnnotation(CommandProperties.class);

                String name = commandProperties.name();
                String description = commandProperties.description();
                String permission = commandProperties.permission();
                String usage = commandProperties.usage();

                PluginCommand pluginCommand = getServer().getPluginCommand(name);
                if (pluginCommand == null) {
                    final Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                    constructor.setAccessible(true);
                    pluginCommand = constructor.newInstance(name, this);
                    pluginCommand.setLabel(name);
                    pluginCommand.setDescription(description.trim().equalsIgnoreCase("") ? "/" + name : usage);
                    pluginCommand.setPermission(permission);
                    pluginCommand.setUsage(usage.trim().equalsIgnoreCase("") ? "/" + name : usage);
                    pluginCommand.setAliases(command.getAliases());
                    pluginCommand.setPermissionMessage(Messages.NO_PERM.format());
                    commandMap.register(getName(), pluginCommand);
                }
                pluginCommand.setExecutor(command);
                pluginCommand.setTabCompleter(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
