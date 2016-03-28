/**
 * MineNight
 * com.sucy.minenight.thread.MainThread
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
package com.sucy.minenight.thread;

import com.sucy.minenight.economy.CurrencyType;
import com.sucy.minenight.economy.Economy;
import com.sucy.minenight.economy.PlayerFunds;

import java.util.ArrayList;

/**
 * Asynchronous thread that handles repeating tasks
 */
public class MainThread extends Thread
{
    private ArrayList<CurrencyType> types = new ArrayList<CurrencyType>();
    private ArrayList<ThreadTask>   tasks = new ArrayList<ThreadTask>();

    private int tick = 0;

    /**
     * Infinitely runs, managing the recurring tasks such
     * as incrementing values or handling other tasks.
     */
    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                sleep(50);
                tickEconomy();
                runTasks(false);
                tick++;
            }
        }
        catch (Exception ex)
        {
            // Force-run remaining tasks before shutting down
            runTasks(true);
        }
    }

    /**
     * Runs scheduled tasks
     */
    private void runTasks(boolean force)
    {
        // Use index to avoid iterator() clashing
        // Start at the end to avoid shifting elements each time
        for (int i = tasks.size(); i > 0;)
            if (tasks.get(--i).tick() || force)
                tasks.remove(i).run();
    }

    /**
     * Adds a new task to process
     *
     * @param task runnable to process
     */
    public void addTask(ThreadTask task)
    {
        tasks.add(task);
    }

    /**
     * Registers an economy type for incrementing periodically
     *
     * @param type currency type to register
     */
    public void registerTicking(CurrencyType type)
    {
        if (type.increments)
            types.add(type);
    }

    /**
     * Applies increments for economy types
     */
    private void tickEconomy()
    {
        for (CurrencyType type : types)
            if (tick % type.incrementTicks == 0)
                for (PlayerFunds funds : Economy.getFunds())
                    funds.addFunds(type.name, type.incrementAmount);
    }
}
