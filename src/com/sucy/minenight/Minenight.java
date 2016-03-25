/**
 * MineNight
 * com.sucy.minenight.MineNight
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
package com.sucy.minenight;

import com.sucy.minenight.economy.Economy;
import com.sucy.minenight.hologram.Holograms;
import com.sucy.minenight.log.*;
import com.sucy.minenight.log.Logger;
import com.sucy.minenight.nms.NBT;
import com.sucy.minenight.nms.NMS;
import com.sucy.minenight.permission.Permissions;
import com.sucy.minenight.protection.Protection;
import com.sucy.minenight.util.ListenerUtil;
import com.sucy.minenight.util.commands.CommandManager;
import com.sucy.minenight.util.config.CommentedConfig;
import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.util.player.PlayerUUIDs;
import com.sucy.minenight.util.reflect.Reflection;
import com.sucy.minenight.util.version.VersionManager;
import com.sucy.minenight.world.Worlds;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.logging.*;

/**
 * The central point of the MineNight collective plugin.
 */
public class Minenight extends JavaPlugin
{
    private static Minenight singleton;

    /**
     * @return reference to the plugin instance or null if not enabled
     */
    public static Minenight getPlugin()
    {
        return singleton;
    }

    /**
     * Retrieves a config by name
     *
     * @param name name of the config
     *
     * @return config data
     */
    public static CommentedConfig getConfig(String name)
    {
        return new CommentedConfig(singleton, name);
    }

    /**
     * Retrieves the data from a config by name
     *
     * @param name name of the config
     *
     * @return config data
     */
    public static DataSection getConfigData(String name)
    {
        return getConfig(name).getConfig();
    }

    /**
     * Registers a listener with the plugin
     *
     * @param listener listener to register
     */
    public static void registerListener(Listener listener)
    {
        if (singleton == null)
            return;

        ListenerUtil.register(listener);
    }

    // Utilities
    private PlayerUUIDs uuidUtil;

    // Segments
    private Worlds      worlds;
    private Permissions permissions;
    private Economy     economy;
    private Protection  protection;
    private Holograms   hologram;

    private boolean enabled = false;

    /**
     * Sets up standalone utilites and logging filter injections
     * when created (before world loading)
     */
    public Minenight()
    {
        singleton = this;

        // Initialize logging config data
        CommentedConfig config = getConfig("config");
        config.checkDefaults();
        config.trim();
        config.save();
        Logger.loadLevels(config.getConfig().getSection("logging"));

        // Set up standalone utilities
        Reflection.initialize();
        NMS.initialize();
        NBT.initialize();
        VersionManager.initialize();

        // Restrict server logging
        stopLogging();

        // Load config data
        CommentedConfig mechanics = getConfig("mechanics");
        mechanics.checkDefaults();
        mechanics.trim();
        mechanics.save();

        // Load utilities related to Bukkit API
        uuidUtil = new PlayerUUIDs(this);

        // Create segments
        worlds = new Worlds(mechanics.getConfig());
        permissions = new Permissions();
        economy = new Economy();
    }

    /**
     * Loads up all plugin segments and utility classes
     */
    @Override
    public void onEnable()
    {
        if (enabled)
        {
            throw new IllegalStateException("Cannot enable the plugin when it is already enabled");
        }
        enabled = true;

        // Create segments that were unable to do so on startup
        protection = new Protection();
        if (NMS.isSupported())
            hologram = new Holograms();
    }

    /**
     * Cleans up all plugin segments
     */
    @Override
    public void onDisable()
    {
        if (!enabled)
        {
            throw new IllegalStateException("Cannot disable the plugin when it isn't enabled");
        }
        enabled = false;

        // Clean up segments
        hologram.cleanup();
        protection.cleanup();
        economy.cleanup();
        permissions.cleanup();
        worlds.cleanup();

        // Clean up utilities
        uuidUtil.save();
        CommandManager.unregisterAll();

        HandlerList.unregisterAll(this);
    }

    /**
     * Stops server logging based on log settings
     */
    private void stopLogging()
    {
        handle(getServer().getLogger());

        try
        {
            addFilter("MinecraftServer");
            addFilter("DedicatedServer");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Sets up the handler for the logger
     *
     * @param logger logger to set up for
     */
    private void handle(java.util.logging.Logger logger)
    {
        java.util.logging.Logger temp = logger;
        while (temp != null)
        {
            for (Handler handler : temp.getHandlers())
                temp.removeHandler(handler);
            logger = temp;
            temp = temp.getParent();
        }
        logger.addHandler(new LogHandler());
    }

    /**
     * Injects a filter into the logger for the given NMS server class
     *
     * @param name NMS server class name
     * @throws Exception
     */
    private void addFilter(String name)
        throws Exception
    {
        Field staticLogger;
        org.apache.logging.log4j.core.Logger logger;
        staticLogger = Class.forName(Reflection.getNMSPackage() + name)
            .getField("LOGGER");
        staticLogger.setAccessible(true);
        logger = (org.apache.logging.log4j.core.Logger)staticLogger.get(null);
        logger.addFilter(new ApacheFilter(LogType.MINECRAFT));
    }
}
