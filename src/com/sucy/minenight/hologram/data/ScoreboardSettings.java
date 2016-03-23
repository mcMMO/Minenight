/**
 * MineNight
 * com.sucy.minenight.hologram.data.ScoreboardSettings
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
import com.sucy.minenight.util.text.TextFormatter;

/**
 * Settings for a scoreboard setup
 */
public class ScoreboardSettings extends NameSettings
{
    public final String prefix;
    public final String suffix;
    public final String subText;

    /**
     * Loads settings from config data
     *
     * @param data data to load from
     */
    public ScoreboardSettings(DataSection data, String key)
    {
        super(data, key);

        int ind = name.indexOf("{name}");
        prefix = TextFormatter.colorString(name.substring(0, ind));
        suffix = TextFormatter.colorString(name.substring(ind + 6));

        String under = data.getList(key).get(1);
        int bot = under.indexOf("{health}");
        subText = under.substring(bot + 8);
    }
}
