/**
 * MineNight
 * com.sucy.minenight.protection.listener.MovementListener
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

import com.sucy.minenight.protection.zone.ZoneManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

/**
 * Handles updating the zones players are in
 */
public class MovementListener implements Listener
{
    /**
     * Initializes player zones on login
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerLoginEvent event)
    {
        if (event.getResult() == PlayerLoginEvent.Result.ALLOWED)
            ZoneManager.init(event.getPlayer());
    }

    /**
     * Clears player zones on quit
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event)
    {
        ZoneManager.clear(event.getPlayer());
    }

    /**
     * Updates player zones on death
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event)
    {
        ZoneManager.update(event.getEntity());
    }

    /**
     * Update player zones on respawn
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event)
    {
        ZoneManager.update(event.getPlayer());
    }

    /**
     * Updates player zones when moving
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event)
    {
        ZoneManager.update(event.getPlayer());
    }

    /**
     * Updates player zones when teleporting
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangeWorld(PlayerChangedWorldEvent event)
    {
        ZoneManager.update(event.getPlayer());
    }

    /**
     * Updates player zones when teleporting
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event)
    {
        ZoneManager.update(event.getPlayer());
    }
}
