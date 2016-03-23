/**
 * MineNight
 * com.sucy.minenight.hologram.display.Hologram
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
package com.sucy.minenight.hologram.display;

import com.sucy.minenight.util.MathFunc;
import com.sucy.minenight.util.Point;
import com.sucy.minenight.util.config.LocationData;
import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.util.text.TextFormatter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

/**
 * Represents a single hologram display that can consist of multiple lines
 */
public class Hologram
{
    private LineData data;
    private Point    chunk;
    private Location loc;
    private int      life;

    /**
     * Loads a permanent hologram from config data
     *
     * @param config config data
     */
    public Hologram(DataSection config)
    {
        this(
            LocationData.parse(config),
            config.getList("format")
        );
    }

    /**
     * Creates a new permanent hologram using the given data
     *
     * @param loc    location of the hologram
     * @param format format to use for the hologram
     */
    public Hologram(Location loc, List<String> format)
    {
        this.loc = loc;
        this.chunk = MathFunc.getChunk(loc);

        data = new LineData(loc);
        for (String string : format)
        {
            // Skull items
            if (string.startsWith("{skull:"))
                data.appendItemLine(makeSkull(string));

                // Regular items
            else if (string.startsWith("{item:"))
                data.appendItemLine(makeItem(string));

                // Regular text
            else
                data.appendTextLine(TextFormatter.colorString(string));
        }
    }

    /**
     * Creates a temporary hologram
     *
     * @param loc    location of the hologram
     * @param format format to use for the hologram
     * @param life   lifespan of the hologram in ticks
     */
    public Hologram(Location loc, List<String> format, int life)
    {
        this(loc, format);
        this.life = life;
    }

    /**
     * @return visibility data for the hologram
     */
    public Visibility getVisibility()
    {
        return data.getVisibility();
    }

    /**
     * @return chunk coordinates of the hologram
     */
    public Point getChunk()
    {
        return chunk;
    }

    /**
     * Checks if the hologram is in the given chunk
     *
     * @param chunk chunk to check
     *
     * @return true if in the chunk, false otherwise
     */
    public boolean isInChunk(Chunk chunk)
    {
        return data.isInChunk(chunk);
    }

    /**
     * Checks if the chunk the hologram resides in is loaded
     *
     * @return true if loaded
     */
    public boolean isChunkLoaded()
    {
        return data.getWorld().isChunkLoaded(chunk.x, chunk.z);
    }

    /**
     * @return hologram location
     */
    public Location getLocation()
    {
        return loc;
    }

    /**
     * Shows the hologram when the chunk loads
     */
    public void show()
    {
        data.spawnEntities();
    }

    /**
     * Disables the hologram when a chunk unloads
     */
    public void hide()
    {
        data.despawnEntities();
    }

    /**
     * Ticks the hologram, testing for it expiring
     *
     * @return true if expired, false otherwise
     */
    public boolean tick()
    {
        return --this.life == 0;
    }

    /**
     * Parses a skull from the given skull filter
     *
     * @param filter filter to parse from
     *
     * @return parsed skull
     */
    private ItemStack makeSkull(String filter)
    {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 0, (short) 0, (byte) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(filter.substring(7, filter.length() - 1));
        skull.setItemMeta(meta);
        return skull;
    }

    /**
     * Parses an item from the given item filter
     *
     * @param filter filter to parse from
     *
     * @return parsed item
     */
    private ItemStack makeItem(String filter)
    {
        String itemType = filter.substring(6, filter.length() - 1).toUpperCase().replace(" ", "_");
        ItemStack item;
        if (itemType.contains(":"))
        {
            int index = itemType.indexOf(':');
            item = new ItemStack(
                Material.valueOf(itemType.substring(0, index)),
                0,
                (short) 0,
                Byte.parseByte(itemType.substring(index + 1))
            );
        }
        else
        {
            item = new ItemStack(Material.valueOf(itemType));
        }
        return item;
    }
}
