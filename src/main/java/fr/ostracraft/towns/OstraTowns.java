package fr.ostracraft.towns;

import fr.ostracraft.towns.commands.TownCommand;
import fr.ostracraft.towns.types.Resident;
import fr.ostracraft.towns.types.TownBlock;
import fr.ostracraft.towns.utils.Config;
import fr.ostracraft.towns.utils.Messages;
import fr.ostracraft.towns.utils.ReflectionUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class OstraTowns extends JavaPlugin {

    private static Economy economy;

    public static OstraTowns get() {
        return getPlugin(OstraTowns.class);
    }

    public static Economy getEconomy() {
        return economy;
    }

    @Override
    @SuppressWarnings("unused")
    public void onEnable() {
        final Plugin VAULT = Bukkit.getPluginManager().getPlugin("Vault");
        if (VAULT == null || !VAULT.isEnabled()) {
            getLogger().severe("Missing VAULT dependency, disabling...");
            setEnabled(false);
            return;
        }
        if (!setupEconomy()) {
            getLogger().severe("Failed to setup VaultAPI economy, disabling...");
            setEnabled(false);
            return;
        }
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
        // Cache all claimed townblocks
        TownBlock.fetchAll();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> registeredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (registeredServiceProvider == null) {
            return false;
        }
        economy = registeredServiceProvider.getProvider();
        return true;
    }

    private void registerCommands() {
        PluginCommand pluginCommand = getCommand("town");
        assert pluginCommand != null;
        pluginCommand.setUsage("/town");
        TownCommand townCommand = new TownCommand();
        pluginCommand.setExecutor(townCommand);
        pluginCommand.setTabCompleter(townCommand);
    }

    private void registerListeners() {
        List<Class<?>> classes = ReflectionUtil.getClasses(getClass().getPackageName() + ".listeners");
        for (Class<?> clazz : classes) {
            if (!Listener.class.isAssignableFrom(clazz))
                continue;
            try {
                Listener listener = (Listener) clazz.getConstructor().newInstance();
                getServer().getPluginManager().registerEvents(listener, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @NotNull
    @Override
    public File getFile() {
        return super.getFile();
    }
}
