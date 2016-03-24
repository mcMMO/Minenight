/**
 * MineNight
 * com.sucy.minenight.world.data.WorldSettings
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
package com.sucy.minenight.world.data;

import com.sucy.minenight.nms.NBT;
import com.sucy.minenight.util.Conversion;
import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.world.enums.GlobalSetting;
import com.sucy.minenight.world.enums.TickSetting;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Global settings that affects how worlds behave
 */
public class WorldSettings
{
    private final HashMap<String, WorldLocations> locations = new HashMap<String, WorldLocations>();

    private final HashMap<String, Boolean> globals = new HashMap<String, Boolean>();
    private final HashMap<String, Integer> ticks   = new HashMap<String, Integer>();

    private final HashSet<String> spawns;

    public final boolean vanillaMessages;
    public final boolean serverMessages;
    public final String  messageMode;
    public final int     stackSize;
    public final int     hiddenNBT;

    /**
     * Loads global settings from config data
     *
     * @param config config data to load
     */
    public WorldSettings(DataSection config)
    {
        DataSection data = config.getSection("settings");

        // Load global settings
        DataSection globalData = data.getSection("global");
        for (String key : globalData.keys())
            globals.put(key.toUpperCase(), globalData.getBoolean(key));

        // Load experience options
        DataSection expData = data.getSection("experience");
        for (String key : expData.keys())
            globals.put(key.toUpperCase(), expData.getBoolean(key));

        // Stack size
        DataSection inventory = data.getSection("inventory");
        stackSize = inventory.getInt("stacksize");

        // NBT values to be hidden
        DataSection nbt = inventory.getSection("hide");
        int id = 0;
        if (nbt.getBoolean("enchantments")) id |= NBT.ENCHANTMENTS;
        if (nbt.getBoolean("attributes")) id |= NBT.ATTRIBUTES;
        if (nbt.getBoolean("unbreakable")) id |= NBT.UNBREAKABLE;
        if (nbt.getBoolean("destroy")) id |= NBT.CAN_DESTROY;
        if (nbt.getBoolean("place")) id |= NBT.CAN_PLACE_ON;
        if (nbt.getBoolean("others")) id |= NBT.OTHERS;
        hiddenNBT = id;

        // Load tick settings
        DataSection tickData = data.getSection("ticks");
        for (String key : tickData.keys())
        {
            ticks.put(key.toUpperCase(), tickData.getInt(key));
        }

        // Message settings
        DataSection messageData = data.getSection("messages");
        vanillaMessages = messageData.getBoolean("vanilla");
        serverMessages = messageData.getBoolean("server");
        messageMode = messageData.getString("mode");

        // Spawn settings
        List<String> entities = data.getList("spawns");
        spawns = new HashSet<String>();
        for (String entity : entities)
            spawns.add(entity.toUpperCase());

        // Location settings
        DataSection locData = config.getSection("worlds");
        for (String key : locData.keys())
        {
            locations.put(key, new WorldLocations(key, locData.getSection(key)));
        }
    }

    /**
     * Hides the NBT tags of the item according to the settings
     *
     * @param item item to hide for
     */
    public ItemStack hide(ItemStack item)
    {
        if (hiddenNBT > 0)
            return NBT.hide(item, hiddenNBT);
        return item;
    }

    /**
     * Checks the amount of ticks a setting is set to
     *
     * @param setting setting to get for
     *
     * @return number of ticks
     */
    public int getTicks(TickSetting setting)
    {
        return ticks.get(setting.key());
    }

    /**
     * Checks whether or not the global setting is enabled
     *
     * @param setting setting to check
     *
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled(GlobalSetting setting)
    {
        return globals.get(setting.key());
    }

    /**
     * Checks if an entity is allowed to spawn
     *
     * @param entity entity type
     *
     * @return true if can spawn
     */
    public boolean canSpawn(Entity entity)
    {
        return spawns.contains(Conversion.getConfigName(entity));
    }

    /**
     * Retrieves location settings for a given world
     *
     * @param world world to get for
     *
     * @return location settings
     */
    public WorldLocations getWorldLocs(World world)
    {
        return locations.get(world.getName());
    }
}
