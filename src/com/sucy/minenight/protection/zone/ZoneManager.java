/**
 * MineNight
 * com.sucy.minenight.protection.zone.ZoneManager
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

import com.sucy.minenight.Minenight;
import com.sucy.minenight.protection.Protection;
import com.sucy.minenight.util.config.CommentedConfig;
import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.util.log.LogType;
import com.sucy.minenight.util.log.Logger;
import com.sucy.minenight.util.version.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Handles loading and fetching zones
 */
public class ZoneManager
{
    private static boolean init = false;

    private static HashMap<String, ArrayList<Zone>> active = new HashMap<String, ArrayList<Zone>>();
    private static HashMap<String, ZoneList>        zones  = new HashMap<String, ZoneList>();

    private static HashMap<UUID, PlayerZones> playerZones = new HashMap<UUID, PlayerZones>();

    /**
     * Initializes the zone data for the player
     *
     * @param player player to initialize for
     */
    public static void init(Player player)
    {
        playerZones.put(player.getUniqueId(), new PlayerZones(player));
    }

    /**
     * Player to clear the zone data for
     *
     * @param player player to clear for
     */
    public static void clear(Player player)
    {
        playerZones.remove(player.getUniqueId());
    }

    /**
     * Updates the zone that the player is in
     *
     * @param player player to retrieve for
     */
    public static void update(Player player)
    {
        playerZones.get(player.getUniqueId()).update(player);
    }

    /**
     * Gets the active zone for the player
     *
     * @param player player to get the zone for
     *
     * @return the zone the player is in or null if not in any
     */
    public static Zone getZone(Player player)
    {
        return playerZones.get(player.getUniqueId()).getTop();
    }

    /**
     * Initializes the manager, loading data from configs
     */
    public static void init(DataSection zoneData)
    {
        if (init)
            return;

        load(zoneData);
        applyChunks();

        // Initializes players that are already online
        for (Player player : VersionManager.getOnlinePlayers())
            init(player);
    }

    /**
     * Applies loaded chunks for initial zones
     */
    private static void applyChunks()
    {
        for (World world : Bukkit.getWorlds())
        {
            for (Chunk chunk : world.getLoadedChunks())
            {
                load(chunk);
            }
        }
    }

    /**
     * Loads zones from the config
     */
    private static void load(DataSection zoneData)
    {
        int count = 0;

        Logger.log(LogType.SETUP, 1, "Loading zones...");

        // Load in each zone, using base keys as the zone names
        for (String key : zoneData.keys())
        {
            try
            {
                Zone zone = new Zone(key, zoneData.getSection(key));

                // Create the list for the world if not done so already
                if (!active.containsKey(zone.getWorldName()))
                {
                    active.put(zone.getWorldName(), new ArrayList<Zone>());
                    zones.put(zone.getWorldName(), new ZoneList());
                }

                // Zones start of as inactive
                zones.get(zone.getWorldName()).add(zone);

                count++;
                Logger.log(LogType.SETUP, 2, "Loaded Zone: " + key);
            }
            catch (Exception ex)
            {
                Logger.invalid("Invalid zone config for zone \"" + key + "\" - " + ex.getMessage());
            }
        }

        Logger.log(LogType.SETUP, 1, "Loaded " + count + " zones");
    }

    /**
     * Loads zones inside the given chunk
     *
     * @param chunk chunk to load zones for
     */
    public static void load(Chunk chunk)
    {
        int x = chunk.getX();
        int z = chunk.getZ();
        int hash = toHash(x, z);
        ZoneList iterableZones = zones.get(chunk.getWorld().getName());
        if (iterableZones != null)
        {
            for (Zone zone : iterableZones)
            {
                if (zone.inChunk(x, z) && zone.activate(hash))
                {
                    active.get(chunk.getWorld().getName()).add(zone);

                    Logger.log(LogType.ZONE, 1, zone.getName() + " was enabled");
                }
            }
        }
    }

    /**
     * Unloads zones inside the given chunk
     *
     * @param chunk chunk to unload zones for
     */
    public static void unload(Chunk chunk)
    {
        int x = chunk.getX();
        int z = chunk.getZ();
        int hash = toHash(x, z);
        ZoneList iterableZones = zones.get(chunk.getWorld().getName());
        if (iterableZones != null)
        {
            for (Zone zone : iterableZones)
            {
                if (zone.inChunk(x, z) && zone.deactivate(hash))
                {
                    active.get(chunk.getWorld().getName()).remove(zone);

                    Logger.log(LogType.ZONE, 1, zone.getName() + " was disabled");
                }
            }
        }
    }

    /**
     * Converts chunk coordinates to a single integer for the hash set
     *
     * @param x chunk X coordinate
     * @param z chunk Y coordinate
     *
     * @return hash integer
     */
    private static int toHash(int x, int z)
    {
        int off = 1 << 15;
        return (x + off) | ((z + off) << 16);
    }

    /**
     * Clears all stored data
     */
    public static void cleanup()
    {
        active.clear();
        zones.clear();
        playerZones.clear();
        init = false;
    }

    /**
     * Gets a zone by a location
     *
     * @param loc location to check for
     *
     * @return zone containing the location or null if none found
     */
    public static Zone getZone(Location loc)
    {
        // Grab the zones for the world
        ArrayList<Zone> worldZones = active.get(loc.getWorld().getName());
        if (worldZones == null)
            return null;

        // Find the zone with the highest Z-Index
        int high = Integer.MIN_VALUE;
        Zone match = null;
        for (Zone zone : worldZones)
        {
            if (zone.getZIndex() > high && zone.contains(loc))
            {
                match = zone;
                high = zone.getZIndex();
            }
        }
        return match;
    }

    /**
     * Gets all zones containing the location and puts them in the list
     *
     * @param list list to store results in
     * @param loc  location to check for
     *
     * @return top level zone out of the found list or null if none were found
     */
    public static Zone getZones(ZoneList list, Location loc)
    {
        list.clear();

        // Grab the zones for the world
        ArrayList<Zone> worldZones = active.get(loc.getWorld().getName());
        if (worldZones == null)
            return null;

        // Find the zone with the highest Z-Index
        int high = Integer.MIN_VALUE;
        Zone match = null;
        for (Zone zone : worldZones)
        {
            if (zone.contains(loc))
            {
                list.add(zone);
                if (zone.getZIndex() > high)
                {
                    match = zone;
                    high = zone.getZIndex();
                }
            }
        }
        return match;
    }

    /**
     * Checks whether or not an action is allowed
     *
     * @param loc  location the action is being performed at
     * @param flag flag related to the action
     *
     * @return true if allowed, false otherwise
     */
    public static boolean isAllowed(Location loc, ZoneFlag flag)
    {
        Zone zone = getZone(loc);
        return zone == null || !zone.hasFlag(flag);
    }

    /**
     * Checks whether or not an action is not alled
     *
     * @param loc  location the action is being performed at
     * @param flag flag related to the action
     *
     * @return true if allowed, false otherwise
     */
    public static boolean isProhibited(Location loc, ZoneFlag flag)
    {
        return isAllowed(loc, flag);
    }

    /**
     * Checks whether or not an action is allowed
     *
     * @param loc    location the action is being performed at
     * @param flag   the flag related to the action
     * @param player player doing the action
     *
     * @return true if allowed, false otherwise
     */
    public static boolean isAllowed(Location loc, ZoneFlag flag, Player player)
    {
        if (player == null)
            return true;

        Zone zone = getZone(loc);
        return zone == null
               || Protection.hasPermissions(player, zone, flag)
               || !zone.hasFlag(flag);
    }

    /**
     * Checks whether or not an action is not alled
     *
     * @param loc  location the action is being performed at
     * @param flag flag related to the action
     * @param player player doing the action
     *
     * @return true if allowed, false otherwise
     */
    public static boolean isProhibited(Location loc, ZoneFlag flag, Player player)
    {
        return !isAllowed(loc, flag, player);
    }

    /**
     * Checks whether or not an action is allowed
     *
     * @param flag   the flag related to the action
     * @param player player doing the action
     *
     * @return true if allowed, false otherwise
     */
    public static boolean isAllowed(ZoneFlag flag, Player player)
    {
        if (player == null)
            return true;

        Zone zone = getZone(player);
        return zone == null
               || Protection.hasPermissions(player, zone, flag)
               || !zone.hasFlag(flag);
    }

    /**
     * Checks whether or not an action is not alled
     *
     * @param flag flag related to the action
     * @param player player doing the action
     *
     * @return true if allowed, false otherwise
     */
    public static boolean isProhibited(ZoneFlag flag, Player player)
    {
        return !isAllowed(flag, player);
    }
}
