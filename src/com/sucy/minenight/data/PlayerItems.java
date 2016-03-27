/**
 * MineNight
 * com.sucy.minenight.data.PlayerItems
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
package com.sucy.minenight.data;

import com.sucy.minenight.util.config.parse.JSONObject;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PlayerItems
{
    private HashMap<String, InventoryData> items = new HashMap<String, InventoryData>();

    public InventoryData getData(World world)
    {
        if (items.containsKey(world.getName()))
            return items.get(world.getName());

        InventoryData data = new InventoryData();
        items.put(world.getName(), data);
        return data;
    }

    public void move(Player player, World to)
    {
        getData(player.getWorld()).load(player);
        getData(to).apply(player);
    }

    public void load(JSONObject json)
    {
        if (json == null) return;

        for (String key : json.keys())
        {
            items.put(key, new InventoryData(json));
        }
    }

    public JSONObject asJSON()
    {
        JSONObject json = new JSONObject();
        for (Map.Entry<String, InventoryData> world : items.entrySet())
        {
            json.set(world.getKey(), world.getValue().asJSON());
        }
        return json;
    }
}
