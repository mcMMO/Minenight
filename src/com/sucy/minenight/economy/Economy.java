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
import com.sucy.minenight.util.ListenerUtil;
import com.sucy.minenight.util.config.CommentedConfig;
import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.util.version.VersionManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles the economy of the server
 */
public class Economy
{
    private static HashMap<UUID, PlayerFunds>    playerCurrency = new HashMap<UUID, PlayerFunds>();
    private static HashMap<String, CurrencyType> currencyTypes  = new HashMap<String, CurrencyType>();

    /**
     * Initializes necessary data on creation
     */
    public Economy()
    {
        DataSection config = Minenight.getConfigData("economy", false, false);

        for (String key : config.keys())
            currencyTypes.put(key, new CurrencyType(config.getSection(key)));
        for (Player player : VersionManager.getOnlinePlayers())
            initialize(player.getUniqueId());

        ListenerUtil.register(new EconomyListener(this));
    }

    /**
     * Cleans up the economy, saving and unloading all data
     */
    public void cleanup()
    {
        for (Map.Entry<UUID, PlayerFunds> entry : playerCurrency.entrySet())
            save(entry.getKey(), entry.getValue());
        playerCurrency.clear();
        currencyTypes.clear();
    }

    /**
     * Initializes the data for the player
     *
     * @param id UUID of player to initialize
     */
    public void initialize(UUID id)
    {
        if (playerCurrency.containsKey(id))
            return;

        PlayerFunds currency = new PlayerFunds();
        DataSection data = Minenight.getConfigData("data/economy/" + id, false, false);
        for (String key : currencyTypes.keySet())
        {
            currency.addFunds(key, data.getDouble(key, currencyTypes.get(key).initial));
        }
    }

    /**
     * Unloads player data after saving it to disk
     *
     * @param player player to unload
     */
    public void unload(Player player)
    {
        new SaveTask(player.getUniqueId(), playerCurrency.remove(player.getUniqueId()))
            .runTaskAsynchronously(Minenight.getPlugin());
    }

    /**
     * Retrieves the funds data for a player
     *
     * @param player player to get the data for
     *
     * @return player's funds data
     */
    public static PlayerFunds getCurrency(Player player)
    {
        return playerCurrency.get(player.getUniqueId());
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
     * Saves funds data for a player to disk
     *
     * @param id    unique ID of the player
     * @param funds player funds data
     */
    private void save(UUID id, PlayerFunds funds)
    {
        CommentedConfig file = Minenight.getConfig("data/economy/" + id);
        DataSection data = file.getConfig();
        data.clear();
        for (String key : currencyTypes.keySet())
            data.set(key, funds.getFunds(key));
        file.save();
    }

    /**
     * Task for saving player funds data asynchronously
     */
    private class SaveTask extends BukkitRunnable
    {
        private UUID        id;
        private PlayerFunds currency;

        public SaveTask(UUID id, PlayerFunds currency)
        {
            this.id = id;
            this.currency = currency;
        }

        @Override
        public void run()
        {
            save(id, currency);
        }
    }
}
