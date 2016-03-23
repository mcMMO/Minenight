/**
 * MineNight
 * com.sucy.minenight.hologram.data.NameSettings
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
import org.bukkit.entity.Damageable;

/**
 * Settings for a monster display name
 */
public class NameSettings
{
    public final int    proportion;
    public final double percent;
    public final String above;
    public final String below;
    public final String name;

    /**
     * Loads settings from config data
     *
     * @param data data to load from
     */
    public NameSettings(DataSection data, String key)
    {
        proportion = data.getInt("proportional", -1);
        percent = data.getInt("percent", 20) / 100.0;

        DataSection colors = data.getSection("format");
        above = TextFormatter.colorString(colors.getString("above"));
        below = TextFormatter.colorString(colors.getString("below"));

        name = TextFormatter.colorString(data.getList(key).get(0));
    }

    public void define(Damageable entity)
    {
        double ratio = entity.getHealth() / entity.getMaxHealth();
        GlobalFilter.define("format", ratio > percent ? above : below);
        if (proportion > 0)
            GlobalFilter.define("health", "" + (int) (ratio * proportion));
        else
            GlobalFilter.define("health", "" + (int) entity.getHealth());
    }
}
