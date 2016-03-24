/**
 * SkillAPI
 * com.sucy.skill.log.Logger
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
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
package com.sucy.minenight.log;

import com.sucy.minenight.util.config.parse.DataSection;
import org.bukkit.Bukkit;

import java.util.HashMap;

/**
 * Handles logging messages of varying levels
 */
public class Logger
{
    private static final HashMap<String, Integer> LEVELS = new HashMap<String, Integer>();

    private static java.util.logging.Logger logger;

    /**
     * Initializes the logger utility to use the given logger
     *
     * @param pluginLogger plugin logger to use
     */
    public static void initialize(java.util.logging.Logger pluginLogger)
    {
        logger = pluginLogger;
    }

    /**
     * Loads all logging level settings from the config data
     *
     * @param config config data to load from
     */
    public static void loadLevels(DataSection config)
    {
        for (String key : config.keys())
        {
            LEVELS.put(key, config.getInt(key));
        }
    }

    /**
     * Retrieves the logging level of the given type
     *
     * @param key log type
     * @return logging level
     */
    public static int getLevel(LogType key)
    {
        return LEVELS.containsKey(key.key()) ? LEVELS.get(key.key()) : 0;
    }

    /**
     * Sets the active logging level for a given category
     *
     * @param key   category
     * @param level logging level
     */
    public static void setLevel(LogType key, int level)
    {
        LEVELS.put(key.key(), level);
    }

    /**
     * Logs a message under the given category if it meets the level
     * requirement.
     *
     * @param key     category key
     * @param level   logging level
     * @param message message to send
     */
    public static void log(String key, int level, String message)
    {
        if (LEVELS.containsKey(key) && LEVELS.get(key) >= level)
        {
            logger.info(message);
        }
    }

    /**
     * Logs a message under the given category if it meets the level
     * requirement.
     *
     * @param key     category key
     * @param level   logging level
     * @param message message to send
     */
    public static void log(LogType key, int level, String message)
    {
        log(key.key(), level, message);
    }

    /**
     * Displays an error message for an invalid setting
     *
     * @param message error message
     */
    public static void invalid(String message)
    {
        logger.severe(message);
    }

    /**
     * Displays an error message for a bug
     *
     * @param message error message
     */
    public static void bug(String message)
    {
        logger.severe(message);
    }

    /**
     * Logs a message, ignoring any active logging levels
     *
     * @param message message to log
     */
    public static void log(String message)
    {
        logger.info(message);
    }
}
