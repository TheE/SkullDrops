// $Id$
/*
 * SkullDrops
 * Copyright (C) 2012 thee and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package me.thee.skulldrops;

import java.util.Random;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagString;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Simple plugin to drop skulls for skeletons, withers, zombies, creepers or
 * players if they are (optionally) killed by a player by configurable chance.
 * There is also a simple command to spawn the skull of a player to the user's
 * inventory.
 * 
 * @author thee
 */
public class SkullDrops extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String commandLabel, String[] args) {
        if (command.getName().equalsIgnoreCase("skull") && args.length <= 1) {

            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (!player.hasPermission("skullDrop.skull")) {
                    player.sendMessage(ChatColor.RED
                            + "Insufficent Permission.");
                    return true;
                }
                String name;

                if (args.length == 1) {
                    name = args[0];
                } else {
                    name = player.getName();
                }
                player.getInventory()
                        .addItem(
                                new org.bukkit.inventory.ItemStack[] { playerSkullForName(
                                        1, name) });
                player.sendMessage(ChatColor.AQUA + "Added " + name
                        + "'s skull to your inventory.");
                return true;

            } else {
                sender.sendMessage(ChatColor.RED
                        + "Only players can add skulls to their inventory!");
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (!(e.getEntityType().equals(EntityType.SKELETON)
                || e.getEntityType().equals(EntityType.WITHER)
                || e.getEntityType().equals(EntityType.ZOMBIE)
                || e.getEntityType().equals(EntityType.CREEPER) || e
                .getEntityType().equals(EntityType.PLAYER))) {
            return;
        }
        if (!(getConfig().getBoolean(
                e.getEntityType().name().toLowerCase() + ".playerKilledOnly") && e
                .getEntity().getKiller() instanceof Player)) {
            return;
        }
        Random r = new Random();

        if ((100 * r.nextDouble()) < getConfig().getDouble(
                e.getEntityType().name().toLowerCase() + ".dropChance")) {
            Location loc = e.getEntity().getLocation();
            loc.getWorld().dropItemNaturally(loc,
                    getSkullItemStack(e.getEntity()));
        }
    }

    /**
     * Returns an ItemStack with the skull of the given entity. Returns null if
     * there is no skull for the given entity.
     * 
     * @param e
     *            the entity
     * @return itemStack with the skull
     */
    private ItemStack getSkullItemStack(Entity e) {
        switch (e.getType()) {
        case SKELETON:
            return (new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte) 0));
        case WITHER:
            return (new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte) 1));
        case ZOMBIE:
            return (new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte) 2));
        case PLAYER:
            return playerSkullForName(1, e.getType().getName());
        case CREEPER:
            return (new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte) 4));
        default:
            return null;
        }
    }

    /**
     * Returns an ItemStack with the skull(s) of the given player name.
     * 
     * @param amount
     *            amount of skulls
     * @param name
     *            the name of the player
     * @return itemStack with the skull
     */
    private ItemStack playerSkullForName(int amount, String name) {
        CraftItemStack skull = new CraftItemStack(Material.SKULL_ITEM, amount);
        skull.setDurability((short) 3);
        NBTTagCompound tag = new NBTTagCompound();
        tag.set("SkullOwner", new NBTTagString("SkullOwner", name));
        skull.getHandle().setTag(tag);
        return skull;
    }
}
