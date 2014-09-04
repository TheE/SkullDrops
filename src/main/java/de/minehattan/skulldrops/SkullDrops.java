/**
 * Copyright (C) 2012 - 2014, SkullDrops team and contributors
 *
 * This file is part of SkullDrops.
 *
 * SkullDrops is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SkullDrops is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SkullDrops. If not, see <http://www.gnu.org/licenses/>.
 */
package de.minehattan.skulldrops;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.sk89q.commandbook.CommandBook;
import com.sk89q.commandbook.util.entity.player.PlayerUtil;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.zachsthings.libcomponents.ComponentInformation;
import com.zachsthings.libcomponents.bukkit.BukkitComponent;
import com.zachsthings.libcomponents.config.ConfigurationBase;
import com.zachsthings.libcomponents.config.Setting;

/**
 * The central entry-point for SkullDrops.
 */
@ComponentInformation(friendlyName = "SkullDrops", desc = "Allows entities to drop their skulls upon death")
public class SkullDrops extends BukkitComponent implements Listener {

    private LocalConfiguration config;
    private Random random = new Random();

    @Override
    public void enable() {
        CommandBook.registerEvents(this);
        registerCommands(Commands.class);
        config = configure(new LocalConfiguration());
    }

    @Override
    public void reload() {
        super.reload();
        configure(config);
    }

    /**
     * The configuration.
     */
    private static class LocalConfiguration extends ConfigurationBase {
        @Setting("playerKilledOnly")
        private boolean playerKilledOnly = true;
        @Setting("dropChanges")
        private Map<String, Double> dropChanges = createDropChances();

        /**
         * Creates a map with the default drop chances.
         * 
         * @return the maps with the defaults
         */
        private static Map<String, Double> createDropChances() {
            Map<String, Double> defDropChances = new HashMap<String, Double>();
            defDropChances.put("skeleton", 2.5);
            defDropChances.put("wither", 2.5);
            defDropChances.put("zombie", 2.5);
            defDropChances.put("creeper", 2.5);
            defDropChances.put("player", 2.5);
            return defDropChances;
        }
    }

    /**
     * The top level commands.
     */
    public class Commands {
        /**
         * Allows players to receive the skulls of (other) players.
         * 
         * @param args
         *            the command-arguments
         * @param sender
         *            the CommandSender who initiated the command
         * @throws CommandException
         *             if the command is cancelled
         */
        @Command(aliases = { "skull" }, usage = "<player>", desc = "Adds player's skulls to your inventory", max = 1)
        @CommandPermissions("skullDrop.skull")
        public void skullCmd(CommandContext args, CommandSender sender) throws CommandException {
            Player player = PlayerUtil.checkPlayer(sender);
            String name = args.getString(0, player.getName());

            player.getInventory().addItem(playerSkullForName(1, name));
            player.sendMessage(ChatColor.AQUA + "Added " + name + "'s skull to your inventory.");
        }
    }

    /**
     * Called whenever an entity dies.
     * 
     * @param event
     *            the event
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntityType().equals(EntityType.SKELETON)
                || event.getEntityType().equals(EntityType.WITHER)
                || event.getEntityType().equals(EntityType.ZOMBIE)
                || event.getEntityType().equals(EntityType.CREEPER) || event.getEntityType().equals(
                EntityType.PLAYER))) {
            return;
        }
        if (config.playerKilledOnly && !(event.getEntity().getKiller() instanceof Player)) {
            return;
        }

        if ((100 * random.nextDouble()) < config.dropChanges.get(event.getEntityType().name().toLowerCase())) {
            Location loc = event.getEntity().getLocation();
            loc.getWorld().dropItemNaturally(loc, getSkullItemStack(event.getEntity()));
        }
    }

    /**
     * Returns an ItemStack with the skull of the given entity. Returns
     * {@code null} if there is no skull for the given entity.
     * 
     * @param entity
     *            the entity
     * @return an ItemStack with the skull
     */
    @Nullable
    private ItemStack getSkullItemStack(Entity entity) {
        switch (entity.getType()) {
        case SKELETON:
            return (new ItemStack(Material.SKULL_ITEM, 1, (byte) 0));
        case WITHER:
            return (new ItemStack(Material.SKULL_ITEM, 1, (byte) 1));
        case ZOMBIE:
            return (new ItemStack(Material.SKULL_ITEM, 1, (byte) 2));
        case PLAYER:
            return playerSkullForName(1, entity.getType().getName());
        case CREEPER:
            return (new ItemStack(Material.SKULL_ITEM, 1, (byte) 4));
        default:
            return null;
        }
    }

    /**
     * Returns an ItemStack that contains the given amount of skull(s) of the
     * player who has the given name.
     * 
     * @param amount
     *            amount of skulls
     * @param name
     *            the name of the player
     * @return an ItemStack with the skulls
     */
    private ItemStack playerSkullForName(int amount, String name) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(name);
        skull.setItemMeta(meta);
        return skull;
    }
}
