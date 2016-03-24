/**
 * MineNight
 * com.sucy.minenight.world.listener.WorldListener
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
package com.sucy.minenight.world.listener;

import com.sucy.minenight.nms.NMS;
import com.sucy.minenight.world.Worlds;
import com.sucy.minenight.world.enums.GlobalSetting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

/**
 * Applies general settings to world related events
 */
public class WorldListener implements Listener
{
    /**
     * Disables keeping spawn points in memory
     *
     * @param event event details
     */
    @EventHandler
    public void onWorldInit(WorldInitEvent event)
    {
        event.getWorld().setKeepSpawnInMemory(false);
        if (!Worlds.getSettings().isEnabled(GlobalSetting.CHUNK_GENERATION) && NMS.isSupported())
            NMS.getManager().stopChunks(event.getWorld());
    }

    /**
     * Stops weather based on settings
     *
     * @param event event details
     */
    @EventHandler
    public void onWeather(WeatherChangeEvent event)
    {
        if (!Worlds.getSettings().isEnabled(GlobalSetting.WEATHER))
            event.setCancelled(true);
    }

    /**
     * Stop sleeping
     *
     * @param event event details
     */
    @EventHandler
    public void onSleep(PlayerBedEnterEvent event)
    {
        if (!Worlds.getSettings().isEnabled(GlobalSetting.SLEEP))
            event.setCancelled(true);
    }

    /**
     * Stop fire spreading
     *
     * @param event event details
     */
    @EventHandler
    public void onSpread(BlockIgniteEvent event)
    {
        switch (event.getCause())
        {
            case SPREAD:
                if (!Worlds.getSettings().isEnabled(GlobalSetting.FIRE_SPREAD))
                    event.setCancelled(true);
                break;
            case FIREBALL:
                if (!Worlds.getSettings().isEnabled(GlobalSetting.FIREBALL_FIRE))
                    event.setCancelled(true);
                break;
        }
    }
}
