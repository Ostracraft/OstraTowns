package fr.ostracraft.towns;

import fr.bakaaless.api.inventory.InventoryAPI;
import fr.ostracraft.towns.commands.TownCommand;
import fr.ostracraft.towns.types.*;
import fr.ostracraft.towns.utils.Config;
import fr.ostracraft.towns.utils.InventoryUtil;
import fr.ostracraft.towns.utils.Messages;
import fr.ostracraft.towns.utils.StringUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

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
            openMembers(player, resident);
        });
        inventory.addItem(5, o -> {
            ItemStack itemStack = new ItemStack(Material.ENCHANTING_TABLE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format("Ventes de plots"));
            List<String> lore = new ArrayList<>();
            lore.add(Messages.TOWN_GUI_LORE.format("Cliquez pour accéder au sous-menu"));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            player.closeInventory();
            openSelling(player, resident);
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
                lore.add(Messages.TOWN_GUI_LORE.format("Cliquez ici pour supprimer"));
                lore.add(StringUtil.colored("   &ala ville (Confirmation demandée)"));
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

    public static void openSelling(Player player, Resident resident) {
        Town town = Town.getTownById(resident.getTownId());
        InventoryAPI inventory = InventoryAPI.create(OstraTowns.get())
                .setRefresh(true)
                .setSize(27)
                .setTitle(Messages.TOWN_GUI_TITLE.format("Vente de plots"));

        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        assert borderMeta != null;
        borderMeta.setDisplayName(" ");
        border.setItemMeta(borderMeta);
        inventory.setBorder(border);

        inventory.addItem(11, o -> {
            ItemStack itemStack = new ItemStack(Material.DIAMOND);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format("Mettre en vente ce plot"));
            List<String> lore = new ArrayList<>();
            if (town == null) {
                lore.add(Messages.TOWN_GUI_LORE.format("&cVous n'êtes pas dans une ville !"));
            } else if (!resident.isMayor() && !resident.isAssistant()) {
                lore.add(Messages.TOWN_GUI_LORE.format("&cVous n'avez pas le grade requis: &4" + ResidentRank.ASSISTANT));
            } else {
                lore.add(Messages.TOWN_GUI_LORE.format("Cliquez ici pour mettre en vente le plot"));
                lore.add(Messages.TOWN_GUI_LORE.format("Définissez le prix à 0 pour le retirer de la vente"));
            }
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, event -> {
            if (town == null || (!resident.isMayor() && !resident.isAssistant()))
                return;
            player.closeInventory();
            ItemStack leftItem = new ItemStack(Material.DIAMOND);
            ItemMeta leftItemMeta = leftItem.getItemMeta();
            assert leftItemMeta != null;
            leftItemMeta.setDisplayName(" ");
            leftItem.setItemMeta(leftItemMeta);

            AnvilGUI.Builder builder = new AnvilGUI.Builder();
            builder.plugin(OstraTowns.get());
            builder.itemLeft(leftItem);
            builder.title(StringUtil.colored("&1Entrez le prix"));
            builder.onComplete((eventPlayer, text) -> {
                TownCommand.SubCommandExecutor.SELL.getExecutor().accept(player, resident, Collections.singletonList(text.trim()));
                return AnvilGUI.Response.close();
            });
            builder.open(player);
        });
        inventory.addItem(13, o -> {
            ItemStack itemStack = new ItemStack(Material.EMERALD);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format("Acheter le plot"));
            List<String> lore = new ArrayList<>();
            lore.add(Messages.TOWN_GUI_LORE.format("Cliquez ici pour acheter"));
            lore.add(Messages.TOWN_GUI_LORE.format("ce plot à une autre ville."));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, inventoryClickEvent -> {
            player.closeInventory();
            TownCommand.SubCommandExecutor.BUY.getExecutor().accept(player, resident, Collections.emptyList());
        });
        inventory.addItem(15, o -> {
            ItemStack itemStack = new ItemStack(Material.BARRIER);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format("Revendre le plot"));
            List<String> lore = new ArrayList<>();
            lore.add(Messages.TOWN_GUI_LORE.format("Cliquez ici pour revendre un"));
            lore.add(Messages.TOWN_GUI_LORE.format("plot acheté à une autre ville."));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }, true, inventoryClickEvent -> {
            player.closeInventory();
            TownCommand.SubCommandExecutor.RESELL.getExecutor().accept(player, resident, Collections.emptyList());
        });

        inventory.build(player);
    }

    public static void openMembers(Player player, Resident resident) {
        openMembers(player, resident, 0);
    }

    public static void openMembers(Player player, Resident resident, final int page) {
        if (resident.getTownId() < 1)
            return;
        Comparator<Resident> comparator = new Comparator<Resident>() {
            @Override
            public int compare(Resident o1, Resident o2) {
                return o1.getRank().compareTo(o2.getRank());
            }
        };
        Town town = Town.getTownById(resident.getTownId());
        assert town != null;
        List<Resident> residents = town.getResidents();
        residents.sort(comparator);

        ItemStack border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName(" ");
        border.setItemMeta(borderMeta);
        InventoryAPI inventory = InventoryAPI.create(OstraTowns.get())
                .setRefresh(true)
                .setSize(54)
                .setBorder(border)
                .setTitle(Messages.TOWN_GUI_TITLE.format("Membres"));

        System.out.println("residents.size() = " + residents.size());

        int[] slots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };

        if (residents.size() > slots.length) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.setDisplayName(Messages.TOWN_GUI_NAME.format("Précédent"));
            prev.setItemMeta(prevMeta);

            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = prev.getItemMeta();
            nextMeta.setDisplayName(Messages.TOWN_GUI_NAME.format("Suivant"));
            next.setItemMeta(nextMeta);

            if (page > 0)
                inventory.addItem(48, prev, true, event -> {
                    openMembers(player, resident, page - 1);
                });
            if ((page + 1) * slots.length < residents.size())
                inventory.addItem(50, next, true, event -> {
                    openMembers(player, resident, page + 1);
                });
        }

        List<Resident> toShow = residents.subList(page * slots.length, residents.size());
        toShow.sort(comparator);

        for (int i = 0; i < toShow.size(); i++) {
            if (i >= slots.length)
                break;
            int slot = slots[i];
            Resident currentResident = toShow.get(i);
            inventory.addItem(slot, o -> {
                ResidentRank currentRank = currentResident.getRank();
                ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta itemMeta = ((SkullMeta) itemStack.getItemMeta());
                itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(currentResident.getUuid())));
                itemMeta.setDisplayName(Messages.TOWN_GUI_NAME.format(currentResident.getUsername()));
                List<String> lore = new ArrayList<>();
                lore.add(Messages.TOWN_GUI_LORE.format("&aGrade actuel: &c" + currentRank));
                lore.add(Messages.TOWN_GUI_LORE.format("&eClic gauche: &aPromouvoir"));
                lore.add(Messages.TOWN_GUI_LORE.format("&eClic droit: &aRétrograder"));
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                return itemStack;
            }, true, event -> {
                ResidentRank currentRank = currentResident.getRank();
                if (event.isLeftClick()) {
                    ResidentRank residentRank = ResidentRank.values()[currentRank.ordinal() - 1];
                    if (residentRank == null)
                        return;
                    TownCommand.SubCommandExecutor.PERMISSION.getExecutor().accept(player, resident, Arrays.asList("set", currentResident.getUsername(), residentRank.toString()));
                } else if (event.isRightClick()) {
                    ResidentRank residentRank = ResidentRank.values()[currentRank.ordinal() + 1];
                    if (residentRank == null)
                        return;
                    TownCommand.SubCommandExecutor.PERMISSION.getExecutor().accept(player, resident, Arrays.asList("set", currentResident.getUsername(), residentRank.toString()));
                }
            });
        }

        inventory.build(player);

    }

    public static void openCreate(Player player, Resident resident) {
        if (resident.getTownId() > 0)
            return;
        ItemStack paper = new ItemStack(Material.NAME_TAG);
        ItemMeta paperMeta = paper.getItemMeta();
        assert paperMeta != null;
        paperMeta.setDisplayName("Nom");
        paper.setItemMeta(paperMeta);

        AnvilGUI.Builder builder = new AnvilGUI.Builder();
        builder.plugin(OstraTowns.get());
        builder.itemLeft(paper);
        builder.title(StringUtil.colored("&1Créer une ville"));
        builder.onComplete((eventPlayer, text) -> {
            TownCommand.SubCommandExecutor.CREATION.getExecutor().accept(player, resident, Collections.singletonList(text.trim()));
            return AnvilGUI.Response.close();
        });
        builder.open(player);
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
            List<String> lore = new ArrayList<>();
            lore.add(Messages.TOWN_GUI_LORE.format("&oPOSSÉDÉ"));
            lore.add(" ");
            lore.add(Messages.TOWN_GUI_LORE.format("Claims max: &e" + TownRank.CAMPEMENT.getMaxClaims()));
            lore.add(Messages.TOWN_GUI_LORE.format("Outposts max: &e" + TownRank.CAMPEMENT.getMaxOutposts()));
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
                lore.add(" ");
                lore.add(Messages.TOWN_GUI_LORE.format("Claims max: &e" + TownRank.BOURG.getMaxClaims()));
                lore.add(Messages.TOWN_GUI_LORE.format("Outposts max: &e" + TownRank.BOURG.getMaxOutposts()));
                lore.add(Messages.TOWN_GUI_LORE.format("Possibilité d'inviter/gérer d'autres membres"));
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
                lore.add(" ");
                lore.add(Messages.TOWN_GUI_LORE.format("Claims max: &e" + TownRank.VILLAGE.getMaxClaims()));
                lore.add(Messages.TOWN_GUI_LORE.format("Outposts max: &e" + TownRank.VILLAGE.getMaxOutposts()));
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
                lore.add(" ");
                lore.add(Messages.TOWN_GUI_LORE.format("Claims max: &e" + TownRank.CITY.getMaxClaims()));
                lore.add(Messages.TOWN_GUI_LORE.format("Outposts max: &e" + TownRank.CITY.getMaxOutposts()));
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
                lore.add(" ");
                lore.add(Messages.TOWN_GUI_LORE.format("Claims max: &e" + TownRank.KINGDOM.getMaxClaims()));
                lore.add(Messages.TOWN_GUI_LORE.format("Outposts max: &e" + TownRank.KINGDOM.getMaxOutposts()));
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
