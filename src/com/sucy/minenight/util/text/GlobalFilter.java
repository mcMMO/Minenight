/**
 * MineNight
 * com.sucy.minenight.util.text.GlobalFilter
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
package com.sucy.minenight.util.text;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Handles filtering various strings using globally defined values
 */
public class GlobalFilter
{
    private static HashMap<UUID, HashMap<String, String>> values = new HashMap<UUID, HashMap<String, String>>();

    private static HashMap<String, String> globals = new HashMap<String, String>();

    public static void define(Player player, String key, String value)
    {
        HashMap<String, String> map = values.get(player.getUniqueId());
        if (map == null)
        {
            map = new HashMap<String, String>();
            values.put(player.getUniqueId(), map);
        }
        map.put(key, value);
    }

    public static void define(String key, String value)
    {
        globals.put(key, value);
    }

    public static void clear(Player player)
    {
        values.clear();
    }

    public static String filter(String text)
    {
        return filter(text, globals);
    }

    public static String filter(Player player, String text)
    {
        return filter(text, values.get(player.getUniqueId()));
    }

    private static String filter(String text, HashMap<String, String> map)
    {
        int next = text.indexOf('{');
        if (next == -1) return text;

        int end = 0;
        StringBuilder sb = new StringBuilder();
        while (next != -1)
        {
            int bracket = text.indexOf('}', next);
            if (bracket != -1)
            {
                sb.append(text, end, next);
                end = bracket + 1;
                String filter = text.substring(next + 1, bracket);
                String value = map.get(filter);
                if (value != null)
                    sb.append(value);
            }
            next = text.indexOf('{', next + 1);
        }
        sb.append(text, end, text.length());
        return sb.toString();
    }
}
