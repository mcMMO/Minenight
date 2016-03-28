/**
 * MineNight
 * com.sucy.minenight.economy.PlayerFunds
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

import com.sucy.minenight.util.config.parse.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the funds of a single player for each type of currency
 */
public class PlayerFunds
{
    private HashMap<String, Float> funds = new HashMap<String, Float>();

    /**
     * Sets up the funds data with initial amounts
     */
    public PlayerFunds(JSONObject json)
    {
        for (String type : Economy.getTypeNames())
            funds.put(type, 0f);

        if (json == null) return;

        for (String key : json.keys())
            if (funds.containsKey(key))
                funds.put(key, json.getFloat(key));
    }

    /**
     * Gets the current balance for the player
     *
     * @param type type of currency
     *
     * @return current balance
     */
    public float getFunds(String type)
    {
        if (!funds.containsKey(type))
            return 0;
        return funds.get(type);
    }

    /**
     * Adds funds to the player's current balance
     *
     * @param type   type of currency
     * @param amount amount to add
     *
     * @return remaining balance
     */
    public float addFunds(String type, float amount)
    {
        float balance = check(type, getFunds(type) + amount);
        funds.put(type, balance);
        return balance;
    }

    /**
     * Subtracts funds from the player's current balance
     *
     * @param type   type of currency
     * @param amount amount to subtract
     *
     * @return remaining balance
     */
    public float subtractFunds(String type, float amount)
    {
        return addFunds(type, -amount);
    }

    /**
     * Sets the amount of funds a player has for a given type
     *
     * @param type   currency type
     * @param amount amount to set to
     */
    public void setFunds(String type, float amount)
    {
        funds.put(type, amount);
    }

    /**
     * Checks if the player has the given amount of funds
     *
     * @param type   type of currency
     * @param amount amount of funds required
     *
     * @return true if has enough
     */
    public boolean hasFunds(String type, float amount)
    {
        return getFunds(type) >= amount;
    }

    /**
     * Checks whether or not the player can spend the amount
     * without going under the minimum limit for the currency type
     *
     * @param type   type of currency
     * @param amount amount looking to spend
     *
     * @return true if can spend
     */
    public boolean canSpend(String type, float amount)
    {
        return getFunds(type) >= amount + Economy.getCurrencyType(type).minimum;
    }

    /**
     * Checks whether or not the player has the needed amount of funds
     *
     * @param type   currency type
     * @param amount amount of funds needed
     *
     * @return true if has enough
     */
    private float check(String type, float amount)
    {
        CurrencyType settings = Economy.getCurrencyType(type);
        return StrictMath.max(settings.minimum, StrictMath.max(settings.maximum, amount));
    }

    public JSONObject asJSON()
    {
        JSONObject json = new JSONObject();
        for (Map.Entry<String, Float> entry : funds.entrySet())
        {
            json.set(entry.getKey(), entry.getValue());
        }
        return json;
    }
}
