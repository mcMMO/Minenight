/**
 * MineNight
 * com.sucy.minenight.protection.event.PlayerEnterZoneEvent
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
package com.sucy.minenight.protection.event;

import com.sucy.minenight.protection.zone.Zone;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event for a player entering a new zone
 */
public class PlayerEnterZoneEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Zone   zone;

    /**
     * @param player player entering the zone
     * @param zone   zone being entered
     */
    public PlayerEnterZoneEvent(Player player, Zone zone)
    {
        this.player = player;
        this.zone = zone;
    }

    /**
     * @return the player that entered the zone
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * @return the zone that was entered
     */
    public Zone getZone()
    {
        return zone;
    }

    /**
     * @return gets the handlers for the event
     */
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    /**
     * @return gets the handlers for the event
     */
    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    /**
     * Launches an event using the given details
     *
     * @param player player entering the zone
     * @param zone   zone being entered
     */
    public static void invoke(Player player, Zone zone)
    {
        Bukkit.getPluginManager().callEvent(new PlayerEnterZoneEvent(player, zone));
    }
}
