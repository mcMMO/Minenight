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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;

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
        check(event, GlobalSetting.WEATHER);
    }

    /**
     * Stop sleeping
     *
     * @param event event details
     */
    @EventHandler
    public void onSleep(PlayerBedEnterEvent event)
    {
        check(event, GlobalSetting.SLEEP);
    }

    /**
     * Stop fire spreading
     *
     * @param event event details
     */
    @EventHandler (priority = EventPriority.LOWEST)
    public void onSpread(BlockIgniteEvent event)
    {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD)
            check(event, GlobalSetting.FIRE_SPREAD);
        else if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL)
            check(event, GlobalSetting.FIRE_NATURAL);
    }

    /**
     * Handles breaking down trees when applicable
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void eventBreakBlock(BlockBreakEvent event)
    {
        if (!Worlds.getSettings().isEnabled(GlobalSetting.TREE_FALLING))
            return;

        Block startingPoint = event.getBlock().getRelative(BlockFace.UP);
        if ((startingPoint.getType() != Material.LOG)
            && (startingPoint.getType() != Material.LOG_2)
            && (startingPoint.getType() != Material.LEAVES)
            && (startingPoint.getType() != Material.LEAVES_2))
            return;

        HashSet<Block> treeBlocks = new HashSet<Block>();
        HashSet<Block> blocksToSearch = new HashSet<Block>();
        HashSet<Block> searched = new HashSet<Block>();
        blocksToSearch.add(startingPoint);
        searched.add(event.getBlock());
        Block temp;

        int i;
        for (i = 0; i < 100; i++)
        {
            if (blocksToSearch.isEmpty())
            {
                break;
            }
            Block block = blocksToSearch.iterator().next();
            blocksToSearch.remove(block);
            searched.add(block);

            if ((block.getType() == Material.LOG)
                || (block.getType() == Material.LOG_2)
                || (block.getType() == Material.LEAVES)
                || (block.getType() == Material.LEAVES_2))
            {

                treeBlocks.add(block);

                if (!searched.contains(temp = block.getRelative(BlockFace.UP)))
                    blocksToSearch.add(temp);
                if (!searched.contains(temp = block.getRelative(BlockFace.DOWN)))
                    blocksToSearch.add(temp);
                if (!searched.contains(temp = block.getRelative(BlockFace.WEST)))
                    blocksToSearch.add(temp);
                if (!searched.contains(temp = block.getRelative(BlockFace.EAST)))
                    blocksToSearch.add(temp);
                if (!searched.contains(temp = block.getRelative(BlockFace.NORTH)))
                    blocksToSearch.add(temp);
                if (!searched.contains(temp = block.getRelative(BlockFace.SOUTH)))
                    blocksToSearch.add(temp);
            }
            else if (!block.getType().isTransparent())
            {
                return;
            }
        }

        if (i < 100)
        {
            for (Block block : treeBlocks)
            {
                FallingBlock sand = block.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                sand.setVelocity(new Vector(0.15 + (block.getY() - startingPoint.getY()) * 0.05, (block.getX() - startingPoint.getX()) * 0.1, 0));
                block.setType(Material.AIR);
            }
        }
    }

    /**
     * Checks whether or not a setting is active and applies
     * it to the cancellable event
     *
     * @param event   event details
     * @param setting setting to check
     */
    private void check(Cancellable event, GlobalSetting setting)
    {
        event.setCancelled(!Worlds.getSettings().isEnabled(setting));
    }
}
