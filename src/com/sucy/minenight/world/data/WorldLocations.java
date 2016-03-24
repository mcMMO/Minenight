/**
 * MineNight
 * com.sucy.minenight.world.LocationData
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
package com.sucy.minenight.world.data;

import com.sucy.minenight.util.config.LocationData;
import com.sucy.minenight.util.config.parse.DataSection;
import org.bukkit.Location;

import java.util.HashMap;

/**
 * Set of locations defined for a given world
 */
public class WorldLocations
{
    private final HashMap<String, Location> warps = new HashMap<String, Location>();

    /**
     * The spawn point of the world
     */
    public final Location spawn;

    /**
     * Loads locations for a world from config data
     *
     * @param data data to load from
     */
    public WorldLocations(String world, DataSection data)
    {
        spawn = LocationData.parse(world, data.getSection("spawn"));

        DataSection warpData = data.getSection("teleports");
        if (warpData != null)
        {
            for (String key : warpData.keys())
            {
                warps.put(key, LocationData.parse(world, warpData.getSection(key)));
            }
        }
    }

    /**
     * Gets a warp point by key
     *
     * @param key warp point key
     * @return warp point
     */
    public Location getWarp(String key)
    {
        return warps.get(key);
    }
}
