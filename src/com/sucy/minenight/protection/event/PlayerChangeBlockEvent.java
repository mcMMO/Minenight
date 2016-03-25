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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event for a player entering a new zone
 */
public class PlayerChangeBlockEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Location from;
    private Location to;

    /**
     * @param player player entering the zone
     * @param from   the block the player came from
     */
    public PlayerChangeBlockEvent(Player player, Location from, Location to)
    {
        this.player = player;
        this.from = from;
        this.to = to;
    }

    /**
     * @return the player that entered the zone
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * @return the location of the player's last block pos
     */
    public Location from()
    {
        return from;
    }

    /**
     * @return location of the block the player is moving into
     */
    public Location to()
    {
        return to;
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
     * @param from   location changing from
     * @param to     location moving to
     */
    public static void invoke(Player player, Location from, Location to)
    {
        Bukkit.getPluginManager().callEvent(new PlayerChangeBlockEvent(player, from, to));
    }
}
