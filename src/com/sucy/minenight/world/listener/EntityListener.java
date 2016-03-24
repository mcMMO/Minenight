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
import com.sucy.minenight.world.enums.GlobalSetting;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.PortalCreateEvent;
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
    @EventHandler (priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event)
    {
        event.getPlayer().getInventory().setMaxStackSize(Worlds.getSettings().stackSize);
    }

    /**
     * Handle automatic revives
     *
     * @param event event details
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        Point point = MathFunc.getChunk(event.getEntity().getLocation());
        if (event.getEntity().getWorld().getBiome(point.x, point.z) == Biome.VOID)
        {
            if (Worlds.getSettings().isEnabled(GlobalSetting.AUTOMATIC_RESPAWN_VOID))
                new RespawnTask(event.getEntity()).runTaskLater(Minenight.getPlugin(), 1);
        }
        else if (Worlds.getSettings().isEnabled(GlobalSetting.AUTOMATIC_RESPAWN))
            new RespawnTask(event.getEntity()).runTaskLater(Minenight.getPlugin(), 1);
    }

    /**
     * Stops portal creation
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.LOWEST)
    public void onPortal (PortalCreateEvent event)
    {
        event.setCancelled(!Worlds.getSettings().isEnabled(GlobalSetting.PORTAL_CREATION));
    }

    /**
     * Explosion prevention
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.LOWEST)
    public void onExplode(EntityExplodeEvent event)
    {
        // TNT
        if (event.getEntityType() == EntityType.PRIMED_TNT)
        {
            if (!Worlds.getSettings().isEnabled(GlobalSetting.TNT_DESTROY))
                event.blockList().clear();
        }

        // Creepers
        else if (event.getEntityType() == EntityType.CREEPER)
        {
            if (!Worlds.getSettings().isEnabled(GlobalSetting.CREEPER_DESTROY))
                event.blockList().clear();
        }

        // Ghast fireballs
        else if (event.getEntityType() == EntityType.FIREBALL)
        {
            if (!Worlds.getSettings().isEnabled(GlobalSetting.FIREBALL_DESTROY))
                event.blockList().clear();
        }
    }

    /**
     * Enderman prevention
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.LOWEST)
    public void onEnderman(EntityChangeBlockEvent event)
    {
        event.setCancelled(event.getEntity().getType() == EntityType.ENDERMAN
            && !Worlds.getSettings().isEnabled(GlobalSetting.ENDERMAN_DESTROY));
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
}
