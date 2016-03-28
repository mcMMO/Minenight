/**
 * MineNight
 * com.sucy.minenight.hologram.data.InstanceSettings
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
package com.sucy.minenight.hologram.data;

import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.util.text.GlobalFilter;
import com.sucy.minenight.util.text.TextFormatter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the settings for a type of instance hologram
 */
public class InstanceSettings
{
    public final int          ticks;
    public final List<String> format;

    /**
     * Loads settings from the config data
     *
     * @param data config data to load from
     */
    public InstanceSettings(DataSection data, String key, String subKey)
    {
        ticks = data.getSection(key).getInt("ticks");
        format = TextFormatter.colorStringList(data.getSection(key).getList(subKey));
    }

    /**
     * Gets the format while filtering each line for the given player
     *
     * @param player player to filter for
     *
     * @return filtered format
     */
    public List<String> getFormat(Player player)
    {
        List<String> filtered = new ArrayList<String>();
        for (String line : format)
            filtered.add(GlobalFilter.filter(player, line));
        return filtered;
    }

    /**
     * Gets the format while filtering each line using a global context
     *
     * @return filtered format
     */
    public List<String> getFormat()
    {
        List<String> filtered = new ArrayList<String>();
        for (String line : format)
            filtered.add(GlobalFilter.filter(line));
        return filtered;
    }
}
