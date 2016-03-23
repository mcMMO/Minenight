/**
 * MineNight
 * com.sucy.minenight.hologram.Hologram
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
package com.sucy.minenight.hologram;

import com.sucy.minenight.Minenight;
import com.sucy.minenight.hologram.data.InstanceSettings;
import com.sucy.minenight.hologram.data.NameSettings;
import com.sucy.minenight.hologram.data.ScoreboardSettings;
import com.sucy.minenight.hologram.display.Hologram;
import com.sucy.minenight.hologram.listener.ChunkListener;
import com.sucy.minenight.hologram.listener.HologramListener;
import com.sucy.minenight.util.MathFunc;
import com.sucy.minenight.util.config.CommentedConfig;
import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.util.log.Logger;
import org.bukkit.Chunk;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles adding floating displays to the game
 */
public class Holograms
{
    private final HashMap<Integer, ArrayList<Hologram>> permanents = new HashMap<Integer, ArrayList<Hologram>>();

    private final ArrayList<Hologram> instances = new ArrayList<Hologram>();

    private final HashMap<String, InstanceSettings> instanceSettings = new HashMap<String, InstanceSettings>();

    private ScoreboardSettings playerScoreboardSettings;
    private NameSettings       monsterNameSettings;

    private TickingTask task;

    /**
     * Sets up listeners and loads config data
     */
    public void setup()
    {
        load();

        task = new TickingTask();
        task.runTaskTimer(Minenight.getPlugin(), 1, 1);

        //Minenight.registerListener(new ZoneListener(this));
        Minenight.registerListener(new HologramListener(this));
        Minenight.registerListener(new ChunkListener(this));
    }

    /**
     * Cleans up all permanent and lingering holograms
     */
    public void cleanup()
    {
        task.cancel();

        for (List<Hologram> holograms : permanents.values())
            for (Hologram hologram : holograms)
                hologram.hide();
        for (Hologram hologram : instances)
            hologram.hide();

        permanents.clear();
        instances.clear();
    }

    /**
     * @param key key for the settings (e.g. "death.player")
     *
     * @return instance settings
     */
    public InstanceSettings getInstanceSettings(String key)
    {
        return instanceSettings.get(key);
    }

    /**
     * Adds an instance hologram to the list
     *
     * @param hologram the instance hologram
     */
    public void addInstance(Hologram hologram)
    {
        instances.add(hologram);
        if (hologram.isChunkLoaded())
            hologram.show();
    }

    /**
     * @return scoreboard settings for players
     */
    public ScoreboardSettings getScoreboardSettings()
    {
        return playerScoreboardSettings;
    }

    /**
     * @return name settings for monsters
     */
    public NameSettings getMonsterNameSettings()
    {
        return monsterNameSettings;
    }

    /**
     * Loads holograms in a chunk
     *
     * @param chunk chunk to load for
     */
    public void load(Chunk chunk)
    {
        int hash = MathFunc.chunkHash(chunk.getX(), chunk.getZ());
        if (permanents.containsKey(hash))
            for (Hologram hologram : permanents.get(hash))
                hologram.show();
    }

    /**
     * Unloads holograms in a chunk
     *
     * @param chunk chunk to unload for
     */
    public void unload(Chunk chunk)
    {
        int hash = MathFunc.chunkHash(chunk.getX(), chunk.getZ());
        if (permanents.containsKey(hash))
            for (Hologram hologram : permanents.get(hash))
                hologram.hide();
    }

    /**
     * Loads holograms and settings from the config
     */
    private void load()
    {
        CommentedConfig file = Minenight.getConfig("holograms");
        file.saveDefaultConfig();

        DataSection config = file.getConfig();

        // Load instance settings
        loadInstanceSettings(config, "hurt");
        loadInstanceSettings(config, "heal");
        loadInstanceSettings(config, "death");

        // Load scoreboard details
        DataSection identity = config.getSection("identity");
        playerScoreboardSettings = new ScoreboardSettings(identity.getSection("players"));
        monsterNameSettings = new NameSettings(identity.getSection("entity"));

        // Load permanents
        DataSection perms = config.getSection("holograms");
        for (String key : perms.keys())
        {
            try
            {
                Hologram perm = new Hologram(perms.getSection(key));
                int hash = MathFunc.chunkHash(perm.getChunk().x, perm.getChunk().z);
                if (!permanents.containsKey(hash))
                    permanents.put(hash, new ArrayList<Hologram>());
                permanents.get(hash).add(perm);
                //ZoneManager.createDetectionZone("holo:" + key, perm.getLocation(), 25);
            }
            catch (Exception ex)
            {
                Logger.invalid("Invalid permanent hologram: " + key);
                ex.printStackTrace();
            }
        }
    }

    private void loadInstanceSettings(DataSection config, String section)
    {
        DataSection data = config.getSection(section);
        for (String key : data.keys())
            instanceSettings.put(section + "." + key, new InstanceSettings(data.getSection(key)));
    }

    private class TickingTask extends BukkitRunnable
    {
        @Override
        public void run()
        {
            for (int i = 0; i < instances.size(); i++)
                if (instances.get(i).tick())
                    instances.remove(i--).hide();
        }
    }
}
