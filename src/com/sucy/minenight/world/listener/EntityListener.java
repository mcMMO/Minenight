/**
 * MineNight
 * com.sucy.minenight.world.listener.EntityListener
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.minenight.world.listener;

import com.sucy.minenight.Minenight;
import com.sucy.minenight.util.MathFunc;
import com.sucy.minenight.util.Point;
import com.sucy.minenight.world.Worlds;
import com.sucy.minenight.world.data.WorldLocations;
import com.sucy.minenight.world.enums.GlobalSetting;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Listener handling global settings related to entities
 */
public class EntityListener implements Listener
{
    /**
     * Sets player stack size on join
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event)
    {
        event.getPlayer().getInventory().setMaxStackSize(StrictMath.min(Worlds.getSettings().stackSize, 127));
    }

    /**
     * Hides the NBT data of an item when it is spawned
     *
     * @param event event details
     */
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event)
    {
        ItemStack result = Worlds.getSettings().hide(event.getEntity().getItemStack());
        event.getEntity().setItemStack(result);
    }

    /**
     * Handles exprience cost for enchantments
     *
     * @param event event details
     */
    @EventHandler
    public void onEnchant(EnchantItemEvent event)
    {
        if (!Worlds.getSettings().isEnabled(GlobalSetting.ENCHANTMENTS))
            event.setExpLevelCost(0);
    }

    /**
     * Keep exp level after using the anvil
     *
     * @param event event details
     */
    @EventHandler
    public void onAnvil(InventoryClickEvent event)
    {
        if (event.getClickedInventory().getType() == InventoryType.ANVIL
            && event.getRawSlot() == 2
            && event.getClickedInventory().getItem(event.getRawSlot()) != null)
        {
            Player player = (Player)event.getWhoClicked();
            new LevelTask(player, player.getLevel()).runTaskLater(Minenight.getPlugin(), 1);
        }
    }

    /**
     * Handle automatic revives and experience on death
     *
     * @param event event details
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        if (!Worlds.getSettings().isEnabled(GlobalSetting.DEATH))
        {
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }

        Point point = MathFunc.getChunk(event.getEntity().getLocation());
        switch (event.getEntity().getWorld().getBiome(point.x, point.z))
        {
            case VOID:
                if (Worlds.getSettings().isEnabled(GlobalSetting.AUTOMATIC_RESPAWN_VOID))
                    new RespawnTask(event.getEntity()).runTaskLater(Minenight.getPlugin(), 1);
                break;
            default:
                if (Worlds.getSettings().isEnabled(GlobalSetting.AUTOMATIC_RESPAWN))
                    new RespawnTask(event.getEntity()).runTaskLater(Minenight.getPlugin(), 1);
                break;
        }
    }

    /**
     * Controls spawn location
     *
     * @param event event details
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event)
    {
        WorldLocations settings = Worlds.getSettings().getWorldLocs(event.getRespawnLocation().getWorld());
        if (settings != null)
            event.setRespawnLocation(settings.spawn);
    }

    /**
     * Stops portal creation
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPortal(PortalCreateEvent event)
    {
        event.setCancelled(!Worlds.getSettings().isEnabled(GlobalSetting.PORTAL_CREATION));
    }

    /**
     * Explosion prevention
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onExplode(EntityExplodeEvent event)
    {
        switch (event.getEntityType())
        {
            case PRIMED_TNT:
                if (!Worlds.getSettings().isEnabled(GlobalSetting.TNT_DESTROY))
                    event.blockList().clear();
                break;
            case CREEPER:
                if (!Worlds.getSettings().isEnabled(GlobalSetting.CREEPER_DESTROY))
                    event.blockList().clear();
                break;
            case FIREBALL:
                if (!Worlds.getSettings().isEnabled(GlobalSetting.FIREBALL_DESTROY))
                    event.blockList().clear();
                break;
            case WITHER_SKULL:
                if (!Worlds.getSettings().isEnabled(GlobalSetting.WITHER_DESTROY))
                    event.blockList().clear();
                break;
        }
    }

    /**
     * Enderman prevention
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEnderman(EntityChangeBlockEvent event)
    {
        event.setCancelled(
            event.getEntity().getType() == EntityType.ENDERMAN
            && !Worlds.getSettings().isEnabled(GlobalSetting.ENDERMAN_DESTROY)
        );
    }

    /**
     * Controls what can spawn
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawn(EntitySpawnEvent event)
    {
        if (!Worlds.getSettings().canSpawn(event.getEntity()))
            event.setCancelled(true);
    }

    /**
     * Runnable for respawning a player
     */
    private class RespawnTask extends BukkitRunnable
    {
        private Player player;

        /**
         * @param player player to respawn
         */
        public RespawnTask(Player player)
        {
            this.player = player;
        }

        /**
         * Respawns the player
         */
        @Override
        public void run()
        {
            player.spigot().respawn();
        }
    }

    /**
     * Task for restoring a player's level after a duration
     */
    private class LevelTask extends BukkitRunnable
    {
        private Player player;
        private int level;

        /**
         * @param player player to restore
         * @param level  level to restore to
         */
        public LevelTask(Player player, int level)
        {
            this.player = player;
            this.level = level;
        }

        /**
         * Restores the player's level
         */
        @Override
        public void run()
        {
            player.setLevel(level);
        }
    }
}
