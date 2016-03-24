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

import com.sucy.minenight.util.Point;
import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.util.log.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Represents a single zone
 */
public class Zone
{
    private static final String
        Z_INDEX = "priority",
        MIN_Y   = "ymin",
        MAX_Y   = "ymax",
        WORLD   = "world",
        FLAGS   = "flag",
        SPAWNS  = "prevent";

    private HashSet<Integer>  chunks;
    private HashSet<ZoneFlag> flags;
    private HashSet<String>   spawns;
    private String            name;
    private String            world;
    private int               zIndex;
    private int               minY;
    private int               maxY;

    private Point min;
    private Point max;
    private Point center;

    private ArrayList<Point>  points;
    private ArrayList<Double> collision;

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
        world = data.getString(WORLD);
        zIndex = data.getInt(Z_INDEX);
        minY = data.getInt(MIN_Y);
        maxY = data.getInt(MAX_Y);

        // Load flag data
        List<String> list = data.getList(FLAGS);
        flags = new HashSet<ZoneFlag>();
        for (String key : list)
        {
            flags.add(ZoneFlag.valueOf(key.toUpperCase().replace(" ", "_")));
        }

        // Load spawn data
        list = data.getList(SPAWNS);
        spawns = new HashSet<String>();
        for (String key : list)
        {
            spawns.add(key.toUpperCase().replace(" ", "_"));
        }

        // Load coordinates
        min = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
        max = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        center = new Point(0, 0);
        int i = 1;
        points = new ArrayList<Point>();
        while (data.isSection("" + i))
        {
            DataSection pointData = data.getSection("" + i);
            Point point = new Point(pointData.getInt("x"), pointData.getInt("z"));
            min.x = StrictMath.min(min.x, point.x);
            min.z = StrictMath.min(min.z, point.z);
            max.x = StrictMath.max(max.x, point.x);
            max.z = StrictMath.max(max.z, point.z);
            center.x += point.x;
            center.z += point.z;
            points.add(point);
            i++;
        }
        if (points.size() > 0)
        {
            center.x /= points.size();
            center.z /= points.size();
        }
        if (points.size() == 2)
        {
            points.clear();
            makeBox();
        }
        min.x >>= 4;
        min.z >>= 4;
        max.x >>= 4;
        max.z >>= 4;

        compute();
    }

    /**
     * Creates a detection zone for something that triggers when
     * a player gets nearby
     *
     * @param name name of the zone for identification purposes
     * @param loc  location of the object to detect for
     * @param rad  detection radius
     */
    public Zone(String name, Location loc, int rad)
    {
        this.name = name;
        chunks = new HashSet<Integer>();

        world = loc.getWorld().getName();
        zIndex = Integer.MIN_VALUE;
        minY = loc.getBlockY() - rad;
        maxY = minY + (rad << 1);

        flags = new HashSet<ZoneFlag>();
        spawns = new HashSet<String>();

        center = new Point(loc.getBlockX(), loc.getBlockZ());
        min = new Point(center.x - rad, center.z - rad);
        max = new Point(center.x + rad, center.z + rad);

        points = new ArrayList<Point>();
        makeBox();
        compute();
    }

    /**
     * Adds points to forma a box using min/max values
     */
    private void makeBox()
    {
        points.add(new Point(min.x, min.z));
        points.add(new Point(min.x, max.z));
        points.add(new Point(max.x, max.z));
        points.add(new Point(max.x, min.z));
    }

    /**
     * Computes values for collision detection early
     */
    private void compute()
    {
        collision = new ArrayList<Double>();
        for (int i = 0, j = points.size() - 1; i < points.size(); j = i++)
        {
            Point p1 = points.get(i);
            Point p2 = points.get(j);
            collision.add((double) (p2.x - p1.x) / (p2.z - p1.z));
        }
    }

    /**
     * @return center of the zone, ignoring the y direction
     */
    public Point getCenter()
    {
        return center;
    }

    /**
     * Checks whether or not the zone contains blocks in the chunk
     *
     * @param x chunk X coordinate
     * @param z chunk Y coordinate
     *
     * @return true if contains, false otherwise
     */
    public boolean inChunk(int x, int z)
    {
        return x >= min.x
               && x <= max.x
               && z >= min.z
               && z <= max.z;
    }

    /**
     * Checks if the zone has flags or not
     *
     * @return true if has flags, false otherwise
     */
    public boolean hasFlags()
    {
        return flags.size() > 0;
    }

    /**
     * Retrieves the flags the zone has
     *
     * @return flags the zone has
     */
    public HashSet<ZoneFlag> getFlags()
    {
        return flags;
    }

    /**
     * Checks whether or not the entity can spawn in the zone
     *
     * @param entity entity to check
     *
     * @return true if can spawn, false otherwise
     */
    public boolean canSpawn(Entity entity)
    {
        return !spawns.contains(entity.getType().name());
    }

    /**
     * Marks a chunk as active for the zone
     *
     * @param hash chunk coordinate hash integer
     *
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
     *
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
        return world;
    }

    /**
     * Checks if the zone contains the given location
     *
     * @param loc location to check against
     *
     * @return true if contained, false otherwise
     */
    public boolean contains(Location loc)
    {
        if (loc.getY() < minY || loc.getY() > maxY)
            return false;

        boolean contained = false;
        int num = points.size();
        for (int i = 0, j = num - 1; i < num; j = i++)
        {
            if (((points.get(i).z > loc.getZ()) != (points.get(j).z > loc.getZ()))
                && (loc.getX() < (loc.getZ() - points.get(i).z) * collision.get(i) + points.get(i).x))
                contained = !contained;
        }
        return contained;
    }

    /**
     * Checks if the zone has the given flag
     *
     * @param flag flag key
     *
     * @return true if it has the flag, false otherwise
     */
    public boolean hasFlag(ZoneFlag flag)
    {
        return flags.contains(flag);
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
