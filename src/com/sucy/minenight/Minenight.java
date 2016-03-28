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

import com.sucy.minenight.data.DataListener;
import com.sucy.minenight.economy.Economy;
import com.sucy.minenight.hologram.Holograms;
import com.sucy.minenight.log.ApacheFilter;
import com.sucy.minenight.log.LogHandler;
import com.sucy.minenight.log.LogType;
import com.sucy.minenight.log.Logger;
import com.sucy.minenight.nms.NBT;
import com.sucy.minenight.nms.NMS;
import com.sucy.minenight.permission.Permissions;
import com.sucy.minenight.protection.Protection;
import com.sucy.minenight.thread.MainThread;
import com.sucy.minenight.util.ListenerUtil;
import com.sucy.minenight.util.commands.CommandManager;
import com.sucy.minenight.util.config.Config;
import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.util.player.PlayerUUIDs;
import com.sucy.minenight.util.reflect.Reflection;
import com.sucy.minenight.util.version.VersionManager;
import com.sucy.minenight.world.Worlds;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.logging.Handler;

/**
 * The central point of the MineNight collective plugin.
 */
public class Minenight extends JavaPlugin
{
    private static Minenight singleton;

    /**
     * Main task thread of the plugin
     */
    public static MainThread mainThread;

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
    public static Config getConfig(String name)
    {
        return new Config(singleton, name);
    }

    /**
     * Retrieves the data from a config by name
     *
     * @param name        name of the config
     * @param setDefaults whether or not to check default values for the file
     *
     * @return config data
     */
    public static DataSection getConfigData(String name, boolean setDefaults, boolean trim)
    {
        Config config = getConfig(name);
        if (setDefaults)
            config.checkDefaults();
        if (trim)
            config.trim();
        if (setDefaults || trim)
            config.save();
        else
            config.saveDefaultConfig();
        return config.getConfig();
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

    private DataSection config;

    /**
     * Sets up standalone utilites and logging filter injections
     * when created (before world loading)
     */
    public Minenight()
    {
        singleton = this;

        mainThread = new MainThread();

        config = getConfigData("config", true, true);
        DataSection segments = config.getSection("plugins");

        // Initialize logging config data
        Logger.loadLevels(config.getSection("logging"));

        // Set up standalone utilities
        Reflection.initialize();
        NMS.initialize();
        NBT.initialize();
        VersionManager.initialize();

        // Restrict server logging
        stopLogging();

        // Load utilities related to Bukkit API
        uuidUtil = new PlayerUUIDs(this);

        // Create segments
        if (segments.getBoolean("worlds"))
            worlds = new Worlds(getConfigData("mechanics", true, true));
        if (segments.getBoolean("groups"))
            permissions = new Permissions();
        if (segments.getBoolean("economy"))
            economy = new Economy();

        NMS.getManager().overrideVanilla();
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

        DataSection segments = config.getSection("plugins");

        // Create segments that were unable to do so on startup
        if (segments.getBoolean("protection"))
            protection = new Protection();
        if (NMS.isSupported() && segments.getBoolean("holograms"))
            hologram = new Holograms();

        if (segments.getBoolean("database"))
            ListenerUtil.register(new DataListener());

        mainThread.start();
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

        mainThread.interrupt();
        try
        {
            mainThread.join();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        // Clean up segments
        if (hologram != null)
            hologram.cleanup();
        if (protection != null)
            protection.cleanup();
        if (economy != null)
            economy.cleanup();
        if (permissions != null)
            permissions.cleanup();
        if (worlds != null)
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
     *
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
        logger = (org.apache.logging.log4j.core.Logger) staticLogger.get(null);
        logger.addFilter(new ApacheFilter(LogType.MINECRAFT));
    }
}
