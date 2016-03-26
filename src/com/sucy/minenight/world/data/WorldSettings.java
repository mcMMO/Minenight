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

import com.sucy.minenight.Minenight;
import com.sucy.minenight.nms.NBT;
import com.sucy.minenight.util.Conversion;
import com.sucy.minenight.util.config.CommentedConfig;
import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.world.enums.GlobalSetting;
import com.sucy.minenight.world.enums.ValueSetting;
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

    private final HashMap<String, Float> numbers = new HashMap<String, Float>();

    private final HashMap<String, Boolean> globals = new HashMap<String, Boolean>();

    private final HashSet<String> spawns;

    private final List<String> loginMessage;
    private final List<String> welcomeMessage;
    private final List<String> respawnMessage;

    public final int stackSize;
    public final int hiddenNBT;

    /**
     * Loads global settings from config data
     *
     * @param config config data to load
     */
    public WorldSettings(DataSection config)
    {
        // Load global settings
        DataSection globalData = config.getSection("players");
        for (String key : globalData.keys())
            globals.put(key.toUpperCase(), globalData.getBoolean(key));
        globalData = config.getSection("server");
        for (String key : globalData.keys())
            globals.put(key.toUpperCase(), globalData.getBoolean(key));
        DataSection respawn = config.getSection("respawn");
        globals.put(GlobalSetting.AUTOMATIC_RESPAWN.key(), respawn.getBoolean(GlobalSetting.AUTOMATIC_RESPAWN.key()));
        globals.put(GlobalSetting.AUTOMATIC_RESPAWN_VOID.key(), respawn.getBoolean(GlobalSetting.AUTOMATIC_RESPAWN_VOID.key()));

        // Messages
        loginMessage = config.getSection("login").getList("messages");
        welcomeMessage = config.getSection("welcome").getList("broadcast");
        respawnMessage = respawn.getList("messages");

        // Stack size
        DataSection inventory = config.getSection("inventory");
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

        // Exp settings
        DataSection expFile = Minenight.getConfigData("experience", true, false);
        globals.put(GlobalSetting.ENCHANTMENTS.key(), expFile.getBoolean(GlobalSetting.ENCHANTMENTS.key()));
        globals.put(GlobalSetting.ANVILS.key(), expFile.getBoolean(GlobalSetting.ANVILS.key()));
        globals.put(GlobalSetting.DEATH.key(), expFile.getBoolean(GlobalSetting.DEATH.key()));

        // Spawn settings
        DataSection spawnFile = Minenight.getConfigData("entity", true, false);
        DataSection spawnData = spawnFile.getSection("whitelist");
        spawns = new HashSet<String>();
        List<String> entities = spawnData.getList("entity");
        for (String entity : entities)
            spawns.add(entity.toUpperCase());
        entities = spawnData.getList("creatures");
        for (String entity : entities)
            spawns.add(entity.toUpperCase());
        entities = spawnData.getList("objects");
        for (String entity : entities)
            spawns.add(entity.toUpperCase());

        // Mob spawner settings
        DataSection spawner = spawnFile.getSection("spawner");
        for (String key : spawner.keys())
            numbers.put(key, spawner.getFloat(key));

        // Entity property settings
        DataSection props = spawnFile.getSection("attributes");
        for (String key : props.keys())
            numbers.put(key, spawner.getFloat(key));
    }

    /**
     * Loads location data from the config data
     *
     * @param locData config data to load from
     */
    public void loadLocSettings(DataSection locData)
    {
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
     * Retrieves a numerical setting
     *
     * @param setting setting type
     * @return set value
     */
    public float getValue(ValueSetting setting)
    {
        return numbers.get(setting.key());
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
