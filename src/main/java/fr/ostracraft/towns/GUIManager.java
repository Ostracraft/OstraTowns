package fr.ostracraft.towns;

import fr.bakaaless.api.inventory.InventoryAPI;
import fr.ostracraft.towns.commands.TownCommand;
import fr.ostracraft.towns.types.Resident;
import fr.ostracraft.towns.types.Town;
import fr.ostracraft.towns.types.TownRank;
import fr.ostracraft.towns.utils.Config;
import fr.ostracraft.towns.utils.InventoryUtil;
import fr.ostracraft.towns.utils.Messages;
import fr.ostracraft.towns.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GUIManager {

    public static void openMain(Player player, Resident resident) {
        Town town = Town.getTownById(resident.getTownId());

        InventoryAPI inventory = InventoryAPI.create(OstraTowns.get())
                .setSize(18)
                .setTitle(Messages.TOWN_GUI_TITLE.format("Menu principal"));

        inventory.addItem(9, o -> {
            ItemStack itemStack = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format("Créer une ville"));
            List<String> lore = new ArrayList<>();
            if (town == null)
                lore.add(Messages.TOWN_GUI_LORE.format("Cela vous coûtera " + Config.TOWN_CREATION_PRICE.get() + " pixels"));
            else
                lore.add(Messages.TOWN_GUI_LORE.format("&cVous êtes déjà dans une ville !"));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            if (town != null) return;
            player.closeInventory();
            GUIManager.openCreate(player, resident);
        });
        inventory.addItem(1, o -> {
            ItemStack itemStack = new ItemStack(Material.SHIELD);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format("Claim un chunk"));
            List<String> lore = new ArrayList<>();
            if (town == null) {
                lore.add(Messages.TOWN_GUI_LORE.format("&cVous n'êtes pas dans une ville !"));
            } else {
                lore.add(Messages.TOWN_GUI_LORE.format("&eClic gauche&a: Claim classique (" + Config.TOWN_CLAIM_PRICE.get() + " pixels" + ")"));
                lore.add(Messages.TOWN_GUI_LORE.format("&eClic droit&a: Claim outpost (" + Config.TOWN_OUTPOST_PRICE.get() + " pixels" + ")"));
            }
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            if (town == null) return;
            player.closeInventory();
            List<String> subArgs;
            if (event.getClick().equals(ClickType.RIGHT))
                subArgs = Collections.singletonList("outpost");
            else
                subArgs = Collections.emptyList();
            TownCommand.SubCommandExecutor.CLAIM.getExecutor().accept(player, resident, subArgs);
        });
        inventory.addItem(11, o -> {
            ItemStack itemStack = new ItemStack(Material.BARRIER);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format("Unclaim un chunk"));
            List<String> lore = new ArrayList<>();
            if (town == null)
                lore.add(Messages.TOWN_GUI_LORE.format("&cVous n'êtes pas dans une ville !"));
            else
                lore.add(Messages.TOWN_GUI_LORE.format("Vous ne serez &nPAS&a remboursé"));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            if (town == null) return;
            player.closeInventory();
            TownCommand.SubCommandExecutor.UNCLAIM.getExecutor().accept(player, resident, Collections.emptyList());
        });
        inventory.addItem(3, o -> {
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format("Gestion des membres"));
            List<String> lore = new ArrayList<>();
            if (town == null)
                lore.add(Messages.TOWN_GUI_LORE.format("&cVous n'êtes pas dans une ville !"));
            else
                lore.add(Messages.TOWN_GUI_LORE.format("Cliquez pour accéder au sous-menu"));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            if (town == null) return;
            player.closeInventory();
            player.sendMessage("INDEV");
        });
        inventory.addItem(5, o -> {
            ItemStack itemStack = new ItemStack(Material.ENCHANTING_TABLE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format("Ventes de plots"));
            List<String> lore = new ArrayList<>();
            if (town == null)
                lore.add(Messages.TOWN_GUI_LORE.format("&cVous n'êtes pas dans une ville !"));
            else
                lore.add(Messages.TOWN_GUI_LORE.format("Cliquez pour accéder au sous-menu"));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            if (town == null) return;
            player.closeInventory();
            player.sendMessage("INDEV");
        });
        inventory.addItem(15, o -> {
            ItemStack itemStack = new ItemStack(Material.EMERALD);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format("Améliorations"));
            List<String> lore = new ArrayList<>();
            if (town == null)
                lore.add(Messages.TOWN_GUI_LORE.format("&cVous n'êtes pas dans une ville !"));
            else
                lore.add(Messages.TOWN_GUI_LORE.format("Cliquez pour accéder au sous-menu"));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            if (town == null) return;
            player.closeInventory();
            TownCommand.SubCommandExecutor.UPGRADE.getExecutor().accept(player, resident, Collections.emptyList());
        });
        inventory.addItem(7, o -> {
            ItemStack itemStack = new ItemStack(Material.OAK_SIGN);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format("Paramètres de ville"));
            List<String> lore = new ArrayList<>();
            if (town == null)
                lore.add(Messages.TOWN_GUI_LORE.format("&cVous n'êtes pas dans une ville !"));
            else
                lore.add(Messages.TOWN_GUI_LORE.format("Cliquez pour accéder au sous-menu"));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            if (town == null) return;
            player.closeInventory();
            TownCommand.SubCommandExecutor.SETTINGS.getExecutor().accept(player, resident, Collections.emptyList());
        });
        inventory.addItem(17, o -> {
            ItemStack itemStack = new ItemStack(Material.TNT);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format("Supprimer la ville"));
            List<String> lore = new ArrayList<>();
            if (town == null) {
                lore.add(Messages.TOWN_GUI_LORE.format("&cVous n'êtes pas dans une ville !"));
            } else {
                lore.add(Messages.TOWN_GUI_LORE.format("Cliquez ici pour supprimer la ville (Confirmation demandée)"));
                lore.add(Messages.TOWN_GUI_LORE.format("&4/!\\ ATTENTION: &cAction irréversible"));
            }
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            if (town == null) return;
            player.closeInventory();
            TownCommand.SubCommandExecutor.DELETE.getExecutor().accept(player, resident, Collections.emptyList());
        });

        inventory.build(player);
    }

    public static void openCreate(Player player, Resident resident) {
        if (resident.getTownId() > 0)
            return;
        Inventory inventory = Bukkit.createInventory(null, InventoryType.ANVIL, StringUtil.colored("&1Créer une ville"));

        player.openInventory(inventory);
    }

    public static void openSettings(Player player, Resident resident) {
        if ((!resident.isMayor() && !resident.isAssistant()) || resident.getTownId() < 1)
            return;
        Town town = Town.getTownById(resident.getTownId());
        assert town != null;

        InventoryAPI inventory = InventoryAPI.create(OstraTowns.get())
                .setRefresh(true)
                .setSize(27)
                .setTitle(Messages.TOWN_GUI_TITLE.format("Paramètres"));

        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        assert borderMeta != null;
        borderMeta.setDisplayName(" ");
        border.setItemMeta(borderMeta);
        inventory.setBorder(border);

        inventory.addItem(11, o -> {
            ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.TOWN_SETTING_GUI_PVP.format(town.getSettings().isPvp() ? "&2ACTIVÉ" : "&cDÉSACTIVÉ"));
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, inventoryClickEvent -> {
            town.getSettings().setPvp(!town.getSettings().isPvp());
            town.updateSettings();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
        });

        inventory.addItem(13, o -> {
            ItemStack itemStack = new ItemStack(Material.FLINT_AND_STEEL);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.TOWN_SETTING_GUI_FIRE.format(town.getSettings().isFire() ? "&2ACTIVÉ" : "&cDÉSACTIVÉ"));
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, inventoryClickEvent -> {
            town.getSettings().setFire(!town.getSettings().isFire());
            town.updateSettings();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
        });

        inventory.addItem(15, o -> {
            ItemStack itemStack = new ItemStack(Material.RED_BED);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.TOWN_SETTING_GUI_SPAWN.format(town.getSettings().isPublicSpawn() ? "&2ACCESSIBLE" : "&cINACCESSIBLE"));
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, inventoryClickEvent -> {
            town.getSettings().setPublicSpawn(!town.getSettings().isPublicSpawn());
            town.updateSettings();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
        });

        inventory.build(player);
    }

    public static void openUpgrades(Player player, Resident resident) {
        if ((!resident.isMayor() && !resident.isAssistant()) || resident.getTownId() < 1)
            return;
        Town town = Town.getTownById(resident.getTownId());
        assert town != null;

        InventoryAPI inventory = InventoryAPI.create(OstraTowns.get())
                .setRefresh(true)
                .setSize(27)
                .setTitle(Messages.TOWN_GUI_TITLE.format("Améliorations"));

        inventory.addItem(18, o -> {
            ItemStack itemStack = new ItemStack(Material.DIRT);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format(TownRank.CAMPEMENT.getPrefix()));
            if (town.getRank().getPriority() >= TownRank.CAMPEMENT.getPriority())
                itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            List<String> lore = Collections.singletonList(Messages.TOWN_GUI_LORE.format("&oPOSSÉDÉ"));
            itemMeta.setLore(lore);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_HURT, 1, 1);
        });
        inventory.addItem(2, o -> {
            ItemStack itemStack = new ItemStack(Material.IRON_BLOCK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format(TownRank.BOURG.getPrefix()));
            List<String> lore;
            if (town.getRank().getPriority() >= TownRank.BOURG.getPriority()) {
                itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                lore = Collections.singletonList(Messages.TOWN_GUI_LORE.format("&oPOSSÉDÉ"));
            } else {
                lore = new ArrayList<>();
                for (ItemStack stack : TownRank.BOURG.getPrice()) {
                    lore.add(Messages.TOWN_GUI_LORE.format(stack.getType().toString().replaceAll("_", " ")) + " x" + stack.getAmount());
                }
            }
            itemMeta.setLore(lore);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            if (town.getRank().getPriority() != TownRank.CAMPEMENT.getPriority()) {
                player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_HURT, 1, 1);
                return;
            }
            boolean hasPrice = true;
            for (ItemStack itemStack : TownRank.BOURG.getPrice()) {
                if (!InventoryUtil.hasEnough(player.getInventory(), itemStack))
                    hasPrice = false;
            }
            if (!hasPrice) {
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_HURT, 1, 1);
                return;
            }
            for (ItemStack itemStack : TownRank.BOURG.getPrice()) {
                InventoryUtil.take(player.getInventory(), itemStack);
            }
            player.updateInventory();
            town.setRank(TownRank.BOURG);
        });
        inventory.addItem(22, o -> {
            ItemStack itemStack = new ItemStack(Material.GOLD_BLOCK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format(TownRank.VILLAGE.getPrefix()));
            List<String> lore;
            if (town.getRank().getPriority() >= TownRank.VILLAGE.getPriority()) {
                itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                lore = Collections.singletonList(Messages.TOWN_GUI_LORE.format("&oPOSSÉDÉ"));
            } else {
                lore = new ArrayList<>();
                for (ItemStack stack : TownRank.VILLAGE.getPrice()) {
                    lore.add(Messages.TOWN_GUI_LORE.format(stack.getType().toString().replaceAll("_", " ")) + " x" + stack.getAmount());
                }
            }
            itemMeta.setLore(lore);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            if (town.getRank().getPriority() != TownRank.BOURG.getPriority()) {
                player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_HURT, 1, 1);
                return;
            }
            boolean hasPrice = true;
            for (ItemStack itemStack : TownRank.VILLAGE.getPrice()) {
                if (!InventoryUtil.hasEnough(player.getInventory(), itemStack))
                    hasPrice = false;
            }
            if (!hasPrice) {
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_HURT, 1, 1);
                return;
            }
            for (ItemStack itemStack : TownRank.VILLAGE.getPrice()) {
                InventoryUtil.take(player.getInventory(), itemStack);
            }
            player.updateInventory();
            town.setRank(TownRank.VILLAGE);
        });
        inventory.addItem(6, o -> {
            ItemStack itemStack = new ItemStack(Material.DIAMOND_BLOCK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format(TownRank.CITY.getPrefix()));
            List<String> lore;
            if (town.getRank().getPriority() >= TownRank.CITY.getPriority()) {
                itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                lore = Collections.singletonList(Messages.TOWN_GUI_LORE.format("&oPOSSÉDÉ"));
            } else {
                lore = new ArrayList<>();
                for (ItemStack stack : TownRank.CITY.getPrice()) {
                    lore.add(Messages.TOWN_GUI_LORE.format(stack.getType().toString().replaceAll("_", " ")) + " x" + stack.getAmount());
                }
            }
            itemMeta.setLore(lore);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            if (town.getRank().getPriority() != TownRank.VILLAGE.getPriority()) {
                player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_HURT, 1, 1);
                return;
            }
            boolean hasPrice = true;
            for (ItemStack itemStack : TownRank.CITY.getPrice()) {
                if (!InventoryUtil.hasEnough(player.getInventory(), itemStack))
                    hasPrice = false;
            }
            if (!hasPrice) {
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_HURT, 1, 1);
                return;
            }
            for (ItemStack itemStack : TownRank.CITY.getPrice()) {
                InventoryUtil.take(player.getInventory(), itemStack);
            }
            player.updateInventory();
            town.setRank(TownRank.CITY);
        });
        inventory.addItem(26, o -> {
            ItemStack itemStack = new ItemStack(Material.EMERALD_BLOCK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format(TownRank.KINGDOM.getPrefix()));
            List<String> lore;
            if (town.getRank().getPriority() >= TownRank.KINGDOM.getPriority()) {
                itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                lore = Collections.singletonList(Messages.TOWN_GUI_LORE.format("&oPOSSÉDÉ"));
            } else {
                lore = new ArrayList<>();
                for (ItemStack stack : TownRank.KINGDOM.getPrice()) {
                    lore.add(Messages.TOWN_GUI_LORE.format(stack.getType().toString().replaceAll("_", " ")) + " x" + stack.getAmount());
                }
            }
            itemMeta.setLore(lore);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            if (town.getRank().getPriority() != TownRank.CITY.getPriority()) {
                player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_HURT, 1, 1);
                return;
            }
            boolean hasPrice = true;
            for (ItemStack itemStack : TownRank.KINGDOM.getPrice()) {
                if (!InventoryUtil.hasEnough(player.getInventory(), itemStack))
                    hasPrice = false;
            }
            if (!hasPrice) {
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_HURT, 1, 1);
                return;
            }
            for (ItemStack itemStack : TownRank.KINGDOM.getPrice()) {
                InventoryUtil.take(player.getInventory(), itemStack);
            }
            player.updateInventory();
            town.setRank(TownRank.KINGDOM);
        });

        int[] bourgPath = {9, 0, 1};
        for (int slot : bourgPath) {
            inventory.addItem(slot, o -> {
                ItemStack itemStack = new ItemStack(
                        town.getRank().getPriority() >= TownRank.BOURG.getPriority()
                                ? Material.LIME_DYE
                                : Material.GRAY_DYE
                );
                ItemMeta itemMeta = itemStack.getItemMeta();
                assert itemMeta != null;
                itemMeta.setDisplayName(" ");
                itemStack.setItemMeta(itemMeta);
                return itemStack;
            }, true);
        }

        int[] villagePath = {11, 20, 21};
        for (int slot : villagePath) {
            inventory.addItem(slot, o -> {
                ItemStack itemStack = new ItemStack(
                        town.getRank().getPriority() >= TownRank.VILLAGE.getPriority()
                                ? Material.LIME_DYE
                                : Material.GRAY_DYE
                );
                ItemMeta itemMeta = itemStack.getItemMeta();
                assert itemMeta != null;
                itemMeta.setDisplayName(" ");
                itemStack.setItemMeta(itemMeta);
                return itemStack;
            }, true);
        }

        int[] cityPath = {23, 24, 15};
        for (int slot : cityPath) {
            inventory.addItem(slot, o -> {
                ItemStack itemStack = new ItemStack(
                        town.getRank().getPriority() >= TownRank.CITY.getPriority()
                                ? Material.LIME_DYE
                                : Material.GRAY_DYE
                );
                ItemMeta itemMeta = itemStack.getItemMeta();
                assert itemMeta != null;
                itemMeta.setDisplayName(" ");
                itemStack.setItemMeta(itemMeta);
                return itemStack;
            }, true);
        }

        int[] kingdomPath = {7, 8, 17};
        for (int slot : kingdomPath) {
            inventory.addItem(slot, o -> {
                ItemStack itemStack = new ItemStack(
                        town.getRank().getPriority() >= TownRank.KINGDOM.getPriority()
                                ? Material.LIME_DYE
                                : Material.GRAY_DYE
                );
                ItemMeta itemMeta = itemStack.getItemMeta();
                assert itemMeta != null;
                itemMeta.setDisplayName(" ");
                itemStack.setItemMeta(itemMeta);
                return itemStack;
            }, true);
        }

        inventory.build(player);
    }

}
