/**
 * MineNight
 * com.sucy.minenight.protection.ZoneListener
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
package com.sucy.minenight.protection.listener;

import com.sucy.minenight.protection.zone.Zone;
import com.sucy.minenight.protection.zone.ZoneFlag;
import com.sucy.minenight.protection.zone.ZoneManager;
import com.sucy.minenight.util.ListenerUtil;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

/**
 * Handles applying zone flags to events happening in said zones
 */
public class FlagListener implements Listener
{
    /**
     * Handles MONSTER and ANIMAL zone flags, blocking spawns when disabled.
     *
     * @param event event details
     */
    @EventHandler
    public void onMonsterSpawn(EntitySpawnEvent event)
    {
        // Handle blocking monsters
        if ((event.getEntity() instanceof Monster
             || event.getEntity() instanceof Slime
             || event.getEntity() instanceof Ghast)
            && !ZoneManager.isProhibited(event.getLocation(), ZoneFlag.MONSTER))
        {
            event.setCancelled(true);
        }

        // Handle blocking animals
        if ((event.getEntity() instanceof Animals
             || event.getEntity() instanceof Ambient)
            && !ZoneManager.isProhibited(event.getLocation(), ZoneFlag.ANIMAL))
        {
            event.setCancelled(true);
        }
    }

    /**
     * Handles PVP and GOD flags, stopping fighting from taking place where blocked
     *
     * @param event event details
     */
    @EventHandler
    public void onCombat(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Zone zone = ZoneManager.getZone(event.getEntity().getLocation());

            // God mode
            if (zone.hasFlag(ZoneFlag.GOD))
            {
                event.setCancelled(true);
                return;
            }

            // Stop PvP
            Player attacker = ListenerUtil.getPlayerDamager(event);
            if (attacker != null)
            {
                // OPs ignore PvP rules
                Player defender = (Player) event.getEntity();
                if (attacker.isOp() || defender.isOp())
                    return;

                // Cancel PvP when prohibited
                if (ZoneManager.isProhibited(attacker.getLocation(), ZoneFlag.PVP)
                    || ZoneManager.isProhibited(defender.getLocation(), ZoneFlag.PVP))
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Stops block-related entities from being damaged in prohibited areas
     *
     * @param event event details
     */
    @EventHandler
    public void onBreakEntity(EntityDamageByEntityEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity))
        {
            Player player = ListenerUtil.getPlayerDamager(event);
            if (ZoneManager.isProhibited(event.getEntity().getLocation(), ZoneFlag.BLOCK, player))
                event.setCancelled(true);
        }
    }

    /**
     * Stop the mining of blocks when prohibited
     *
     * @param event event details
     */
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event)
    {
        if (ZoneManager.isProhibited(event.getBlock().getLocation(), ZoneFlag.BLOCK, event.getPlayer()))
            event.setCancelled(true);
    }

    /**
     * Stop the placing of blocks when prohibited
     *
     * @param event event details
     */
    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event)
    {
        if (ZoneManager.isProhibited(event.getBlock().getLocation(), ZoneFlag.BLOCK, event.getPlayer()))
            event.setCancelled(true);
    }

    /**
     * Stop the interactions with objects where prohibited
     *
     * @param event event details
     */
    @EventHandler
    public void onInteract(EntityInteractEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            if (ZoneManager.isProhibited(event.getBlock().getLocation(), ZoneFlag.BLOCK, (Player)event.getEntity()))
                event.setCancelled(true);
        }
    }
}
