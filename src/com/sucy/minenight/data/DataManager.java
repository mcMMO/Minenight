/**
 * MineNight
 * com.sucy.minenight.data.DataManager
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

import com.sucy.minenight.Minenight;
import com.sucy.minenight.economy.Economy;
import com.sucy.minenight.economy.PlayerFunds;
import com.sucy.minenight.nms.NMS;
import com.sucy.minenight.util.config.parse.JSONObject;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class DataManager
{
    private static final int MIN_CHAR = 47;
    private static final int MAX_CHAR = 126;
    private static final int CHAR_MOD = MAX_CHAR - MIN_CHAR + 1;

    private static final HashMap<UUID, PlayerData>     data  = new HashMap<UUID, PlayerData>();
    private static final HashMap<UUID, PlayerSnapshot> snaps = new HashMap<UUID, PlayerSnapshot>();

    private static final HashMap<String, String> keyMap = new HashMap<String, String>();

    public static PlayerData getPlayerData(Player player)
    {
        return data.get(player.getUniqueId());
    }

    public static PlayerSnapshot snapshot(Player player)
    {
        if (!snaps.containsKey(player.getUniqueId()))
            snaps.put(player.getUniqueId(), new PlayerSnapshot(player));
        else
            snaps.get(player.getUniqueId()).update(player);

        return snaps.get(player.getUniqueId());
    }

    public static void load(UUID playerId)
    {
        File file = new File(Minenight.getPlugin().getDataFolder(), "database/" + playerId.toString() + ".json");
        JSONObject json = new JSONObject(file);
        PlayerData result = new PlayerData(playerId, json.getObject("s"));
        result.items.load(json.getObject("i"));
        Economy.load(playerId, new PlayerFunds(json.getObject("e")));

        data.put(playerId, result);
    }

    public static void unload(UUID playerId)
    {
        if (!data.containsKey(playerId))
            return;

        PlayerData player = data.remove(playerId);

        // Sum new vanilla data with old data
        File vanilla = NMS.getManager().getStatsFile(playerId);
        JSONObject newData = new JSONObject(vanilla);
        for (String key : newData.keys())
        {
            if (!key.startsWith("stat"))
                continue;

            if (!keyMap.containsKey(key))
                keyMap.put(key, makeKey());
            String mapped = keyMap.get(key);

            int value = newData.getInt(key);
            if (player.vanilla.containsKey(mapped))
                value += player.vanilla.get(mapped);
            player.vanilla.put(mapped, value);
        }
        vanilla.delete();

        // Grab JSON object
        JSONObject json = new JSONObject();

        // Assemble the data
        json.set("e", Economy.getFunds(playerId).asJSON());
        json.set("i", player.items.asJSON());
        json.set("p", snaps.remove(playerId).asJSON());
        json.set("s", player.asJSON());

        // Save to disk
        json.save(new File(Minenight.getPlugin().getDataFolder(), "database/" + playerId.toString() + ".json"));
    }

    private static String makeKey()
    {
        int prev = CHAR_MOD;
        int next = data.size();
        String result = "";
        while (prev >= CHAR_MOD)
        {
            prev = next;
            result += (char) ((keyMap.size() % CHAR_MOD) + MIN_CHAR);
            next /= CHAR_MOD;
        }
        return result.replace(':', '+');
    }
}
