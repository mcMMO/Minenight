/**
 * MineNight
 * com.sucy.minenight.protection.effect.EffectTimer
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
package com.sucy.minenight.protection.effect;

import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * A timer for a flag effect
 */
public class EffectTimer
{
    private ArrayList<Player> players = new ArrayList<Player>();

    private int step = 0;

    private int time;
    private int amount;

    /**
     * Initializes the effect timer assuming it waits the
     * full duration before applying the first time
     *
     * @param time   time between applications
     * @param amount value for the effect
     */
    public EffectTimer(int time, int amount)
    {
        this.step = 0;
        this.time = time;
        this.amount = amount;
    }

    /**
     * Ticks the effect, checking if it is ready to apply again
     *
     * @return true if can apply, false otherwise
     */
    public boolean tick()
    {
        step = (step + 1) % time;
        return step == 0;
    }

    /**
     * @return value for the effect
     */
    public int getAmount()
    {
        return amount;
    }

    /**
     * @return modifiable list of influenced players
     */
    public ArrayList<Player> getPlayers()
    {
        return players;
    }
}
