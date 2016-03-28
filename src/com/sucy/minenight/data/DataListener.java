/**
 * MineNight
 * com.sucy.minenight.data.DataListener
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
import com.sucy.minenight.thread.ThreadTask;
import com.sucy.minenight.util.config.parse.JSONObject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.UUID;

/**
 * Listener for when players join or leave,
 * informing the game to load or save their data
 */
public class DataListener implements Listener
{
    /**
     * Loads player data on login asynchronously
     *
     * @param event event details
     */
    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event)
    {
        DataManager.load(event.getUniqueId());
    }

    /**
     * Unloads player data on exit
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event)
    {
        DataManager.snapshot(event.getPlayer());
        Minenight.mainThread.addTask(new UnloadTask(event.getPlayer().getUniqueId()));
    }

    /**
     * Task for unloading player data asynchronously
     */
    private class UnloadTask extends ThreadTask
    {
        private UUID id;

        /**
         * @param id UUID of the player to unload
         */
        public UnloadTask(UUID id)
        {
            super(2);
            this.id = id;
        }

        /**
         * Unloads the player's data
         */
        @Override
        public void run()
        {
            DataManager.unload(id);
        }
    }
}
