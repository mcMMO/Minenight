/**
 * MineNight
 * com.sucy.minenight.economy.FundType
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

/**
 * A definition for a type of currency
 */
public class CurrencyType
{
    public final String name;

    public final float minimum;
    public final float maximum;
    public final boolean increments;
    public final int incrementTicks;
    public final float incrementAmount;
    public final float playerSteal;
    public final float mobSteal;

    /**
     * Loads the definition from config data
     *
     * @param data config data to load from
     */
    public CurrencyType(String name, DataSection data)
    {
        this.name = name;

        // Limits
        minimum = data.getFloat("minimum");
        maximum = data.getFloat("maximum");

        // Passive gain
        if (data.has("incremental"))
        {
            DataSection increment = data.getSection("incremental");
            increments = true;
            incrementAmount = increment.getFloat("value");
            incrementTicks = increment.getInt("ticks");
            Minenight.mainThread.registerTicking(this);
        }
        else
        {
            increments = false;
            incrementAmount = 0;
            incrementTicks = 0;
        }

        // Stolen on kill
        if (data.has("drop"))
        {
            DataSection steal = data.getSection("drop");
            playerSteal = data.getFloat("players");
            mobSteal = data.getFloat("entity");
        }
        else
        {
            playerSteal = 0;
            mobSteal = 0;
        }
    }
}
