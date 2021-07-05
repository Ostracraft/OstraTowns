package fr.ostracraft.towns.utils;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiConsumer;

public class AnvilGUI implements Listener {

    private final JavaPlugin plugin;
    private String title;
    private ItemStack leftItem;
    private BiConsumer<InventoryClickEvent, String> consumer = null;

    private Inventory inventory;
    private int containerId;

    public AnvilGUI(JavaPlugin plugin, String title, ItemStack leftItem) {
        this.plugin = plugin;
        this.title = title;
        this.leftItem = leftItem;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private EntityPlayer toNMS(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    private int getRealNextContainerId(Player player) {
        return toNMS(player).nextContainerCounter();
    }

    private int getNextContainerId(AnvilContainer container) {
        return container.getContainerId();
    }

    public AnvilGUI onClick(BiConsumer<InventoryClickEvent, String> consumer) {
        this.consumer = consumer;
        return this;
    }

    public void open(Player player) {
        CraftEventFactory.handleInventoryCloseEvent(toNMS(player));
        toNMS(player).activeContainer = toNMS(player).defaultContainer;

        AnvilContainer container = new AnvilContainer(player, title);

        inventory = ((Container) container).getBukkitView().getTopInventory();
        inventory.setItem(0, leftItem);

        containerId = getNextContainerId(container);
        toNMS(player).playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, Containers.ANVIL, container.getTitle()));
        toNMS(player).activeContainer = container;
        container.addSlotListener(toNMS(player));

    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(!event.getInventory().equals(inventory) || event.getRawSlot() > 2)
            return;
        event.setCancelled(true);
        String text;
        ItemStack responseItem = event.getInventory().getItem(2);
        if(responseItem == null)
            text = "";
        else
            text = responseItem.hasItemMeta() ? responseItem.getItemMeta().getDisplayName() : "";
        consumer.accept(event, text);
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        if(event.getInitiator().equals(inventory) || event.getDestination().equals(inventory))
            event.setCancelled(true);
    }

    private class AnvilContainer extends ContainerAnvil {
        public AnvilContainer(Player player, String title) {
            super(getRealNextContainerId(player), ((CraftPlayer) player).getHandle().inventory,
                    ContainerAccess.at(((CraftWorld) player.getWorld()).getHandle(), new BlockPosition(0, 0, 0)));
            this.checkReachable = false;
            setTitle(new ChatMessage(title));
        }

        @Override
        public void e() {
            super.e();
            this.levelCost.set(0);
        }

        @Override
        public void b(EntityHuman entityhuman) {
        }

        @Override
        protected void a(EntityHuman entityhuman, World world, IInventory iinventory) {
        }

        public int getContainerId() {
            return windowId;
        }


    }
}