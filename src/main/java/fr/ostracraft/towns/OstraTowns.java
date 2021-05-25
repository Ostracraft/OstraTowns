package fr.ostracraft.towns;

import fr.ostracraft.towns.utils.Config;
import fr.ostracraft.towns.utils.Messages;
import org.bukkit.plugin.java.JavaPlugin;

public class OstraTowns extends JavaPlugin {

    @Override
    public void onEnable() {
        Config.load();
        Messages.load();
        DatabaseManager.init();
    }

    public static OstraTowns get() {
        return getPlugin(OstraTowns.class);
    }
}
