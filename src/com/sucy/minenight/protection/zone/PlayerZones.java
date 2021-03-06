/**
 * MineNight
 * com.sucy.minenight.protection.zone.PlayerZones
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
package com.sucy.minenight.protection.zone;

import com.sucy.minenight.log.LogType;
import com.sucy.minenight.log.Logger;
import com.sucy.minenight.protection.event.PlayerChangeBlockEvent;
import com.sucy.minenight.protection.event.PlayerEnterZoneEvent;
import com.sucy.minenight.protection.event.PlayerLeaveZoneEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Handles updating player zones they are in for events and effects
 */
public class PlayerZones
{
    private ZoneList zones = new ZoneList();
    private ZoneList temp  = new ZoneList();

    private Zone     top;
    private Location lastLoc;
    private Location tempLoc;

    /**
     * Holds a reference to the player for handling
     * updates later on.
     */
    public PlayerZones(Player player)
    {
        lastLoc = player.getLocation().add(-10, -10, -10);
        tempLoc = player.getLocation();

        update(player);
    }

    public Zone getTop()
    {
        return top;
    }

    /**
     * Updates the player's zones if necessary
     *
     * @param player player reference
     */
    public void update(Player player)
    {
        // When dead, leave all zones
        if (player.isDead())
        {
            for (Zone zone : zones)
            {
                PlayerLeaveZoneEvent.invoke(player, zone);
            }
            zones.clear();
            return;
        }

        // Ignore update if locations match
        player.getLocation(tempLoc);
        if ((int) tempLoc.getX() == (int) lastLoc.getX()
            && (int) tempLoc.getY() == (int) lastLoc.getY()
            && (int) tempLoc.getZ() == (int) lastLoc.getZ())
            return;

        // Change block event
        PlayerChangeBlockEvent.invoke(player, lastLoc, tempLoc);

        // Grab new zones
        top = ZoneManager.getZones(temp, player.getLocation(tempLoc));

        // Launch events for changes
        int i = 0, j = 0, k = 0;
        while (i < temp.size())
        {
            k = j;

            // Find the first matching zone
            Zone zone = temp.get(i++);
            while (k < zones.size() && zones.get(k) != zone)
                k++;

            // Entering a zone
            if (k == zones.size())
            {
                PlayerEnterZoneEvent.invoke(player, zone);
                Logger.log(LogType.ZONE, 1, player.getName() + " entered " + zone.getName());
            }

            // Leaving a zone
            else if (k > j)
            {
                for (; j < k; j++)
                {
                    PlayerLeaveZoneEvent.invoke(player, zones.get(j));
                    Logger.log(LogType.ZONE, 1, player.getName() + " left " + zones.get(j));
                }
                j++;
            }

            else j++;
        }

        // Left remaining zones
        for (; j < zones.size(); j++)
        {
            PlayerLeaveZoneEvent.invoke(player, zones.get(j));
            Logger.log(LogType.ZONE, 1, player.getName() + " left " + zones.get(j));
        }

        // Swap references
        ZoneList swap = temp;
        temp = zones;
        zones = swap;
        Location loc = tempLoc;
        tempLoc = lastLoc;
        lastLoc = loc;
    }
}
