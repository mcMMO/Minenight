/**
 * MineNight
 * com.sucy.minenight.protection.zone.Zone
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

import com.sucy.minenight.util.config.parse.DataSection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.List;

/**
 * Represents a single zone
 */
public class Zone
{
    private static final String
        BORDER_1 = "border1",
        BORDER_2 = "border2",
        Z_INDEX = "zIndex",
        WORLD = "world",
        FLAGS = "flag";

    private HashSet<Integer> chunks;
    private HashSet<String> flags;
    private String name;
    private int zIndex;

    protected Location min;
    protected Location max;

    /**
     * Loads zone data from the config data
     *
     * @param data config data to load from
     */
    public Zone(String name, DataSection data)
    {
        this.name = name;
        chunks = new HashSet<Integer>();

        // Load basic data
        World world = Bukkit.getWorld(data.getString(WORLD));
        min = loadLoc(world, data.getSection(BORDER_1));
        max = loadLoc(world, data.getSection(BORDER_2));
        zIndex = data.getInt(Z_INDEX);
        this.validate();

        // Load flag data
        List<String> list = data.getList(FLAGS);
        flags = new HashSet<String>();
        for (String key : list)
        {
            flags.add(key.toUpperCase().replace(" ", "_"));
        }
    }

    /**
     * Checks whether or not the zone contains blocks in the chunk
     *
     * @param x chunk X coordinate
     * @param z chunk Y coordinate
     * @return true if contains, false otherwise
     */
    public boolean inChunk(int x, int z)
    {
        int minX = min.getBlockX() >> 4;
        int maxX = max.getBlockX() >> 4;
        int minZ = min.getBlockZ() >> 4;
        int maxZ = max.getBlockZ() >> 4;

        return x >= minX
            && x <= maxX
            && z >= minZ
            && z <= maxZ;
    }

    /**
     * Marks a chunk as active for the zone
     *
     * @param hash chunk coordinate hash integer
     * @return true if was the first active chunk, false otherwise
     */
    public boolean activate(int hash)
    {
        chunks.add(hash);
        return chunks.size() == 1;
    }

    /**
     * Marks a chunk as inactive for the zone
     *
     * @param hash chunk coordinate hash integer
     * @return true if was the last active chunk, false otherwise
     */
    public boolean deactivate(int hash)
    {
        chunks.remove(hash);
        return chunks.size() == 0;
    }

    /**
     * @return the name of the zone
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return name of the world the zone is in
     */
    public String getWorldName()
    {
        return min.getWorld().getName();
    }

    /**
     * Checks if the zone contains the given location
     *
     * @param loc location to check against
     * @return true if contained, false otherwise
     */
    public boolean contains(Location loc)
    {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return
            x >= min.getX()
            && x <= max.getX()
            && y >= min.getY()
            && y <= max.getY()
            && z >= min.getZ()
            && z <= max.getZ();
    }

    /**
     * Checks if the zone has the given flag
     *
     * @param flag flag key
     * @return true if it has the flag, false otherwise
     */
    public boolean hasFlag(ZoneFlag flag)
    {
        return flags.contains(flag.name());
    }

    /**
     * Gets the Z-index of the zone
     *
     * @return the zone's z-index
     */
    public int getZIndex()
    {
        return zIndex;
    }

    /**
     * Validates the min/max order of the bounds
     */
    private void validate() {
        // Make sure bounds follow the min/max order
        if (min.getX() > max.getX())
        {
            double x = min.getX();
            min.setX(max.getX());
            max.setX(x);
        }
        if (min.getY() > max.getY())
        {
            double y = min.getY();
            min.setY(max.getY());
            max.setY(y);
        }
        if (min.getZ() > max.getZ())
        {
            double z = min.getZ();
            min.setZ(max.getZ());
            max.setZ(z);
        }
    }

    /**
     * Loads a location from the config data
     *
     * @param world   world to use
     * @param locData location data section
     * @return parsed location
     */
    private Location loadLoc(World world, DataSection locData)
    {
        return new Location(
            world,
            locData.getInt("x"),
            locData.getInt("y"),
            locData.getInt("z")
        );
    }

    /**
     * Returns the zone name as the toString
     *
     * @return zone name
     */
    @Override
    public String toString()
    {
        return name;
    }

    /**
     * Hash code for the zone is based on its name
     *
     * @return hash code for the zone
     */
    @Override
    public int hashCode()
    {
        return name.hashCode();
    }
}
