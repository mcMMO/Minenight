/**
 * MineNight
 * com.sucy.minenight.economy.Economy
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
package com.sucy.minenight.economy;

import com.sucy.minenight.Minenight;
import com.sucy.minenight.util.config.parse.DataSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 * Handles the economy of the server
 */
public class Economy
{
    private static HashMap<UUID, PlayerFunds>    playerfunds   = new HashMap<UUID, PlayerFunds>();
    private static HashMap<String, CurrencyType> currencyTypes = new HashMap<String, CurrencyType>();

    /**
     * Initializes necessary data on creation
     */
    public Economy()
    {
        playerfunds.clear();
        currencyTypes.clear();

        DataSection config = Minenight.getConfigData("economy", false, false);

        for (String key : config.keys())
            currencyTypes.put(key, new CurrencyType(config.getSection(key)));
    }

    /**
     * Cleans up the economy, saving and unloading all data
     */
    public void cleanup()
    {

    }

    /**
     * Adds player funds data
     *
     * @param playerId player ID
     * @param funds    funds data
     */
    public static void load(UUID playerId, PlayerFunds funds)
    {
        playerfunds.put(playerId, funds);
    }

    /**
     * Retrieves the funds data for a player
     *
     * @param player player to get the data for
     *
     * @return player's funds data
     */
    public static PlayerFunds getFunds(Player player)
    {
        return playerfunds.get(player.getUniqueId());
    }

    /**
     * Retrieves the funds data for a player
     *
     * @param id id of the player to get the data for
     *
     * @return player's funds data
     */
    public static PlayerFunds getFunds(UUID id)
    {
        return playerfunds.get(id);
    }

    /**
     * Checks whether or not the type is a valid type of currency
     *
     * @param type currency type
     *
     * @return true if valid
     */
    public static boolean isCurrencyType(String type)
    {
        return currencyTypes.containsKey(type);
    }

    /**
     * Gets the settings for the given type of currency
     *
     * @param type type of currency
     *
     * @return currency settings
     */
    public static CurrencyType getCurrencyType(String type)
    {
        return currencyTypes.get(type);
    }

    /**
     * @return available types of currencies;
     */
    public static Set<String> getTypes()
    {
        return currencyTypes.keySet();
    }
}
