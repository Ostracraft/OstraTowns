package fr.ostracraft.towns;

import fr.ostracraft.towns.utils.Config;
import fr.ostracraft.towns.utils.Messages;
import org.bukkit.plugin.java.JavaPlugin;

public class OstraTowns extends JavaPlugin {

    @Override
    public void onEnable() {
        if(!Config.load())
            getLogger().severe("Failed to load config ! Using default values.");
        if(!Messages.load())
            getLogger().severe("Failed to load messages ! Using default values.");
        DatabaseManager.init();
    }

    public static OstraTowns get() {
        return getPlugin(OstraTowns.class);
    }
}
