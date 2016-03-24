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

import java.util.HashMap;

/**
 * Manages the funds of a single player for each type of currency
 */
public class PlayerFunds
{
    private HashMap<String, Double> funds = new HashMap<String, Double>();

    /**
     * Gets the current balance for the player
     *
     * @param type type of currency
     *
     * @return current balance
     */
    public double getFunds(String type)
    {
        if (!funds.containsKey(type))
            return 0;
        return funds.get(type);
    }

    /**
     * Gets the player's current balance formatted with
     * the currency's symbol.
     *
     * @param type type of currency
     *
     * @return formatted current balance
     */
    public String getFormattedFunds(String type)
    {
        return (int) getFunds(type) + Economy.getCurrencyType(type).symbol;
    }

    /**
     * Adds funds to the player's current balance
     *
     * @param type   type of currency
     * @param amount amount to add
     *
     * @return remaining balance
     */
    public double addFunds(String type, double amount)
    {
        double balance = check(type, getFunds(type) + amount);
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
    public double subtractFunds(String type, double amount)
    {
        return addFunds(type, -amount);
    }

    /**
     * Checks if the player has the given amount of funds
     *
     * @param type   type of currency
     * @param amount amount of funds required
     *
     * @return true if has enough
     */
    public boolean hasFunds(String type, double amount)
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
    public boolean canSpend(String type, double amount)
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
    private double check(String type, double amount)
    {
        CurrencyType settings = Economy.getCurrencyType(type);
        return StrictMath.max(settings.minimum, StrictMath.max(settings.maximum, amount));
    }
}
