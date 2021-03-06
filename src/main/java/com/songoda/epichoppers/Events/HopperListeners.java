package com.songoda.epichoppers.Events;

import com.songoda.arconix.plugin.Arconix;
import com.songoda.epichoppers.EpicHoppers;
import com.songoda.epichoppers.Utils.Debugger;
import com.songoda.epichoppers.Utils.Methods;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by songoda on 4/18/2017.
 */
public class HopperListeners implements Listener {

    private EpicHoppers instance;

    public HopperListeners(EpicHoppers instance) {
        this.instance = instance;
    }


    @EventHandler(ignoreCancelled = true)
    public void onHop(InventoryMoveItemEvent e) {
        try {
            Inventory source = e.getSource();

            if (instance.v1_7 || instance.v1_8) return;


            if (!instance.getHopperManager().isHopper(e.getSource().getLocation())) return;

            com.songoda.epichoppers.Hopper.Hopper hopper = instance.getHopperManager().getHopper(e.getSource().getLocation());

            if (source.getHolder() instanceof Hopper && hopper.getSyncedBlock() != null) {
                    e.setCancelled(true);
            }

/*
            //if amt is not 1
            int amt = hopper.getLevel().getAmount();

            if (amt <= 1) {
                return;
            }

            e.setCancelled(true);

            // Set amount of items per hopper transfer
            ItemStack item = e.getItem();
            int transferAmount = Math.min(amt, getItemCount(source, item));
            item.setAmount(transferAmount);

            e.getDestination().addItem(item); //hacky as shit

            source.removeItem(item);
            //Please work.
            */
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }

    private Map<UUID, Player> ents = new HashMap<>();

    @EventHandler
    public void onDed(EntityDamageByEntityEvent e) {
        try {
            if (e.getDamager() instanceof Player) {
                Player p = (Player) e.getDamager();
                if (Methods.isSync(p)) {
                    double d = ((LivingEntity) e.getEntity()).getHealth() - e.getDamage();
                    if (d < 1) {
                        ents.put(e.getEntity().getUniqueId(), p);
                    }
                }
            }
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }

    @EventHandler
    public void onDrop(EntityDeathEvent e) {
        try {
            if (ents.containsKey(e.getEntity().getUniqueId())) {
                Player p = ents.get(e.getEntity().getUniqueId());

                ItemStack item = p.getItemInHand();
                ItemMeta meta = item.getItemMeta();
                Location location = Arconix.pl().getApi().serialize().unserializeLocation(meta.getLore().get(1).replaceAll("§", ""));
                if (location.getBlock().getType() == Material.CHEST) {
                    InventoryHolder ih = (InventoryHolder) location.getBlock().getState();
                    for (ItemStack is : e.getDrops()) {
                        ih.getInventory().addItem(is);
                    }
                    e.getDrops().clear();
                }
            }
        } catch (Exception ee) {
            Debugger.runReport(ee);
        }
    }

    private int getItemCount(Inventory inventory, ItemStack item) {
        int amount = 0;

        for (ItemStack inventoryItem : inventory) {
            if (!item.isSimilar(inventoryItem)) continue;
            amount += inventoryItem.getAmount();
        }

        return amount;
    }
}
