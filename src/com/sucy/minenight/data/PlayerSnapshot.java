/**
 * MineNight
 * com.sucy.minenight.data.PlayerSnapshot
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

import com.sucy.minenight.util.config.LocationData;
import com.sucy.minenight.util.config.parse.JSONObject;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerSnapshot
{
    public GameMode gameMode;
    public Location
        position;
    public String
        ip;
    public double
        health;
    public boolean
        fly, op, banned;

    public PlayerSnapshot(Player player)
    {
        update(player);
    }

    public void update(Player player)
    {
        gameMode = player.getGameMode();
        ip = player.getAddress().getAddress().getHostAddress();
        position = player.getLocation();
        health = player.getHealth();
        fly = player.isFlying();
        op = player.isOp();
        banned = player.isBanned();
    }

    public JSONObject asJSON()
    {
        JSONObject json = new JSONObject();
        json.set("b", banned ? 1 : 0);
        json.set("f", fly ? 1 : 0);
        json.set("g", gameMode.name());
        json.set("h", health);
        json.set("i", ip);
        json.set("o", op ? 1 : 0);
        json.set("p", LocationData.asJSON(position));
        return json;
    }
}
