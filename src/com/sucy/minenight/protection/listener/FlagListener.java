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

import com.sucy.minenight.protection.Protection;
import com.sucy.minenight.protection.event.PlayerEnterZoneEvent;
import com.sucy.minenight.protection.event.PlayerLeaveZoneEvent;
import com.sucy.minenight.protection.zone.Zone;
import com.sucy.minenight.protection.zone.ZoneFlag;
import com.sucy.minenight.protection.zone.ZoneManager;
import com.sucy.minenight.protection.zone.ZonePoint;
import com.sucy.minenight.util.ListenerUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.util.Vector;

/**
 * Handles applying zone flags to events happening in said zones
 */
public class FlagListener implements Listener
{
    private Protection protection;

    /**
     * @param protection reference to management class
     */
    public FlagListener(Protection protection)
    {
        this.protection = protection;
    }

    /**
     * Handles prevented spawns in zones
     *
     * @param event event details
     */
    @EventHandler
    public void onSpawn(EntitySpawnEvent event)
    {
        // Handle blocking spawns
        if (event.getEntity() instanceof LivingEntity || event.getEntityType() == EntityType.EXPERIENCE_ORB)
        {
            Zone zone = ZoneManager.getZone(event.getLocation());
            if (zone != null && !zone.canSpawn(event.getEntity()))
            {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Handles when a player enters a zone
     *
     * @param event event details
     */
    @EventHandler
    public void onEnter(PlayerEnterZoneEvent event)
    {
        Zone zone = event.getZone();

        if (zone.hasFlag(ZoneFlag.HEAL))
            protection.getHealEffect().getPlayers().add(event.getPlayer());
        if (zone.hasFlag(ZoneFlag.HURT))
            protection.getHurtEffect().getPlayers().add(event.getPlayer());
        if (zone.hasFlag(ZoneFlag.RESTRICT) && !Protection.hasPermissions(event.getPlayer(), ZoneFlag.RESTRICT))
        {
            ZonePoint center = event.getZone().getCenter();
            Vector dir = event.getPlayer().getLocation().subtract(center.x, 0, center.z).toVector();
            dir.setY(0);
            event.getPlayer().setVelocity(dir.normalize().multiply(3).setY(0.5));
        }
    }

    /**
     * Handles when a player leaves a zone
     *
     * @param event event details
     */
    @EventHandler
    public void onLeave(PlayerLeaveZoneEvent event)
    {
        Zone zone = event.getZone();

        if (zone.hasFlag(ZoneFlag.HEAL))
            protection.getHealEffect().getPlayers().remove(event.getPlayer());
        if (zone.hasFlag(ZoneFlag.HURT))
            protection.getHurtEffect().getPlayers().remove(event.getPlayer());
    }

    /**
     * Handles DROP flag, stopping players from dropping items in prohibited zones
     *
     * @param event event details
     */
    @EventHandler
    public void onDrop(PlayerDropItemEvent event)
    {
        if (ZoneManager.isProhibited(ZoneFlag.DROP, event.getPlayer()))
        {
            event.setCancelled(true);
        }
    }

    /**
     * Handles MODIFY flag, stopping players from modifying their inventory
     *
     * @param event event details
     */
    @EventHandler
    public void onModify(InventoryClickEvent event)
    {
        if (ZoneManager.isProhibited(ZoneFlag.MODIFY, (Player) event.getWhoClicked()))
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
            Player defender = (Player) event.getEntity();
            Zone zone = ZoneManager.getZone(defender);

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
                if (attacker.isOp() || defender.isOp())
                    return;

                // Cancel PvP when prohibited
                if (zone.hasFlag(ZoneFlag.PVP)
                    || ZoneManager.getZone(attacker).hasFlag(ZoneFlag.PVP))
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
            if (ZoneManager.isProhibited(event.getEntity().getLocation(), ZoneFlag.PROTECT, player))
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
        if (ZoneManager.isProhibited(event.getBlock().getLocation(), ZoneFlag.PROTECT, event.getPlayer()))
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
        if (ZoneManager.isProhibited(event.getBlock().getLocation(), ZoneFlag.PROTECT, event.getPlayer()))
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
            if (ZoneManager.isProhibited(event.getBlock().getLocation(), ZoneFlag.PROTECT, (Player) event.getEntity()))
                event.setCancelled(true);
        }
    }
}
