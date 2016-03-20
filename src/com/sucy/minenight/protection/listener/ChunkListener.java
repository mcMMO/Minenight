/**
 * MineNight
 * com.sucy.minenight.protection.listener.ChunkListener
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
package com.sucy.minenight.protection.listener;

import com.sucy.minenight.protection.zone.ZoneManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * Listens for chunks loading/unloading to keep the number of
 * active zones toa minimal amount
 */
public class ChunkListener implements Listener
{
    /**
     * Tells the ZoneManager to load zones for a chunk that is loading
     *
     * @param event event details
     */
    @EventHandler
    public void onLoad(ChunkLoadEvent event)
    {
        ZoneManager.load(event.getChunk());
    }

    /**
     * Tells the ZoneManager to unload zones for a chunk that is unloading
     *
     * @param event event details
     */
    @EventHandler
    public void onUnload(ChunkUnloadEvent event)
    {
        ZoneManager.unload(event.getChunk());
    }
}
