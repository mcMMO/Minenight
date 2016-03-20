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
import com.sucy.minenight.util.config.CommentedConfig;
import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.util.log.LogType;
import com.sucy.minenight.util.log.Logger;
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
    private static HashMap<String, ArrayList<Zone>> zones  = new HashMap<String, ArrayList<Zone>>();

    private static HashMap<UUID, Zone> playerZones = new HashMap<UUID, Zone>();

    /**
     * Updates the zone that the player is in
     *
     * @param player player to retrieve for
     */
    public static void update(Player player)
    {
        playerZones.put(player.getUniqueId(), getZone(player.getLocation()));
    }

    /**
     * Initializes the manager, loading data from configs
     */
    public static void init()
    {
        if (init)
            return;

        load();
        applyChunks();
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
    private static void load()
    {
        int count = 0;
        Logger.log(LogType.SETUP, 1, "Loading zones...");

        CommentedConfig file = Minenight.getConfig("zones");
        file.saveDefaultConfig();
        DataSection config = file.getConfig();

        // Load in each zone, using base keys as the zone names
        for (String key : config.keys())
        {
            try
            {
                Zone zone = new Zone(key, config.getSection(key));

                // Create the list for the world if not done so already
                if (!active.containsKey(zone.getWorldName()))
                {
                    active.put(zone.getWorldName(), new ArrayList<Zone>());
                    zones.put(zone.getWorldName(), new ArrayList<Zone>());
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
        ArrayList<Zone> iterableZones = zones.get(chunk.getWorld().getName());
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
        ArrayList<Zone> iterableZones = zones.get(chunk.getWorld().getName());
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
        init = false;
    }

    /**
     * Gets a zone by a location
     *
     * @param loc location to check for
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
     * Checks whether or not an action is allowed
     *
     * @param loc  location the action is being performed at
     * @param flag flag related to the action
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
     * @return true if allowed, false otherwise
     */
    public static boolean isAllowed(Location loc, ZoneFlag flag, Player player)
    {
        return player == null || player.hasPermission("protection.ignoreowner") || isAllowed(loc, flag);
    }

    /**
     * Checks whether or not an action is not alled
     *
     * @param loc  location the action is being performed at
     * @param flag flag related to the action
     * @return true if allowed, false otherwise
     */
    public static boolean isProhibited(Location loc, ZoneFlag flag, Player player)
    {
        return !isAllowed(loc, flag, player);
    }
}
