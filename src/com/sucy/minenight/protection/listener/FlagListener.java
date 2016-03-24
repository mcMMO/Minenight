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
import com.sucy.minenight.util.ListenerUtil;
import com.sucy.minenight.util.Point;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Handles applying zone flags to events happening in said zones
 */
public class FlagListener implements Listener
{
    private Protection protection;
    private Location   temp;

    /**
     * @param protection reference to management class
     */
    public FlagListener(Protection protection)
    {
        this.protection = protection;
        temp = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
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
        if (zone.hasFlag(ZoneFlag.GOD))
            event.getPlayer().setFireTicks(0);
        if (zone.hasFlag(ZoneFlag.RESTRICT) && !Protection.hasPermissions(event.getPlayer(), zone, ZoneFlag.RESTRICT))
        {
            Point center = event.getZone().getCenter();
            Vector dir = event.getPlayer().getLocation(temp).subtract(center.x, 0, center.z).toVector();
            dir.setY(Math.max(dir.getY(), 0));
            dir.normalize().multiply(3);
            dir.setY(dir.getY() * 0.2 + 0.5);
            event.getPlayer().setVelocity(dir);
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
     * Stop all damage sources to someone in a "God" zone
     *
     * @param event event details
     */
    @EventHandler
    public void onDamaged(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player defender = (Player) event.getEntity();
            Zone zone = ZoneManager.getZone(defender);

            // God mode
            if (zone != null && zone.hasFlag(ZoneFlag.GOD))
            {
                event.setCancelled(true);
                if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)
                    defender.setFireTicks(0);
            }
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

            // Stop PvP
            Player attacker = ListenerUtil.getPlayerDamager(event);
            if (attacker != null)
            {
                // OPs ignore PvP rules
                if (attacker.isOp() || defender.isOp())
                    return;

                // Cancel PvP when prohibited
                Zone zone = ZoneManager.getZone(defender);
                Zone zone2 = ZoneManager.getZone(attacker);
                if ((zone != null && zone.hasFlag(ZoneFlag.PVP))
                    || (zone2 != null && zone2.hasFlag(ZoneFlag.PVP)))
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
            if (ZoneManager.isProhibited(event.getEntity().getLocation(temp), ZoneFlag.PROTECT, player))
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
        if (ZoneManager.isProhibited(event.getBlock().getLocation(temp), ZoneFlag.PROTECT, event.getPlayer()))
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
        if (ZoneManager.isProhibited(event.getBlock().getLocation(temp), ZoneFlag.PROTECT, event.getPlayer()))
            event.setCancelled(true);
    }

    /**
     * Stops blocks melting in protected zones
     *
     * @param event event details
     */
    @EventHandler
    public void onMelt(BlockFadeEvent event)
    {
        Material type = event.getBlock().getType();
        if (type == Material.ICE || type == Material.SNOW || type == Material.SNOW_BLOCK)
            if (ZoneManager.isProhibited(event.getBlock().getLocation(temp), ZoneFlag.PROTECT))
                event.setCancelled(true);
    }

    @EventHandler
    public void onFreeze(BlockFormEvent event)
    {
        if (ZoneManager.isProhibited(event.getBlock().getLocation(temp), ZoneFlag.PROTECT))
            event.setCancelled(true);
    }

    /**
     * Stops interactions in protected zones
     *
     * @param event event details
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if (event.getClickedBlock() != null && ZoneManager.isProhibited(event.getClickedBlock().getLocation(temp), ZoneFlag.PROTECT, event.getPlayer()))
            event.setCancelled(true);
    }

    /**
     * Stops ignition of blocks
     *
     * @param event event details
     */
    @EventHandler
    public void onIgnite(BlockIgniteEvent event)
    {
        Location loc = event.getBlock().getLocation(temp);

        // Non-player ignition
        if (event.getPlayer() == null)
        {
            if (ZoneManager.isProhibited(loc, ZoneFlag.PROTECT))
                event.setCancelled(true);
        }

        // Player ignition
        else if (ZoneManager.isProhibited(loc, ZoneFlag.PROTECT, event.getPlayer()))
        {
            event.setCancelled(true);
        }
    }

    /**
     * Stops explosions damaging protected areas
     *
     * @param event event details
     */
    @EventHandler
    public void onExplode(EntityExplodeEvent event)
    {
        List<Block> list = event.blockList();
        for (int i = 0; i < list.size(); i++)
        {
            if (ZoneManager.isProhibited(list.get(i).getLocation(temp), ZoneFlag.PROTECT))
                list.remove(i--);
        }
    }

    /**
     * Stops those pesky endermen from taking blocks in protected zones
     *
     * @param event event details
     */
    @EventHandler
    public void onEnderman(EntityChangeBlockEvent event)
    {
        if (event.getEntity().getType() == EntityType.ENDERMAN
            && ZoneManager.isProhibited(event.getBlock().getLocation(temp), ZoneFlag.PROTECT))
            event.setCancelled(true);
    }

    /**
     * Stop portal creation in protected zones
     *
     * @param event event details
     */
    @EventHandler
    public void onPortal(PortalCreateEvent event)
    {
        for (Block block : event.getBlocks())
        {
            if (ZoneManager.isProhibited(block.getLocation(temp), ZoneFlag.PROTECT))
            {
                event.setCancelled(true);
                return;
            }
        }
    }
}
