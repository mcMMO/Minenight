/**
 * MineNight
 * com.sucy.minenight.data.PlayerData
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
import com.sucy.minenight.util.player.PlayerUUIDs;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData
{
    public final HashMap<String, Integer> vanilla = new HashMap<String, Integer>();

    public final PlayerItems items = new PlayerItems();

    public final UUID id;
    public final long login;

    public long time;

    /**
     * Initializes player data, loading available data from disk
     *
     * @param id player to initialize for
     */
    public PlayerData(UUID id, JSONObject json)
    {
        this.id = id;
        login = System.currentTimeMillis();

        if (json == null) return;

        time = json.getLong("t");

        JSONObject map = json.getObject("v");
        for (String key : map.keys())
        {
            vanilla.put(key, map.getInt(key));
        }
    }

    public JSONObject asJSON()
    {
        JSONObject json = new JSONObject();
        json.set("n", PlayerUUIDs.getName(id));
        json.set("t", time + (System.currentTimeMillis() - login));
        json.setMap("v", vanilla);
        return json;
    }
}
