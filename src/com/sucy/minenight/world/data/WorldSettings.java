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
import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.world.enums.GlobalSetting;
import com.sucy.minenight.world.enums.TickSetting;
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
    private final HashMap<String, TameSettings>   tame      = new HashMap<String, TameSettings>();

    private final HashMap<String, Float>   numbers = new HashMap<String, Float>();
    private final HashMap<String, Integer> ticks   = new HashMap<String, Integer>();

    private final HashMap<String, Boolean> globals = new HashMap<String, Boolean>();

    private final HashSet<String> spawns;

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
        DataSection globalData = config.getSection("gameplay");
        for (String key : globalData.keys())
            globals.put(key.toUpperCase(), globalData.getBoolean(key));
        globalData = config.getSection("server");
        for (String key : globalData.keys())
            globals.put(key.toUpperCase(), globalData.getBoolean(key));

        // Generics
        DataSection generic = config.getSection("generic").getSection("amount");
        for (String key : generic.keys())
            numbers.put(key.toUpperCase(), generic.getFloat(key));

        // Tame
        DataSection tamed = config.getSection("tamed");
        for (String key : tamed.keys())
            tame.put(key.toUpperCase(), new TameSettings(tamed.getSection(key)));

        // Delay
        DataSection delay = config.getSection("delay");
        for (String key : delay.keys())
        {
            DataSection subDelay = delay.getSection(key);
            for (String subkey : subDelay.keys())
                ticks.put(key.toUpperCase() + subkey.toUpperCase(), subDelay.getInt(subkey));
        }

        // Stamina
        DataSection stamina = config.getSection("stamina").getSection("ticks");
        for (String key : stamina.keys())
            ticks.put("STAMINA" + key.toUpperCase(), stamina.getInt(key));

        // Damage
        DataSection dmg = config.getSection("damage");
        DataSection dmgAmount = dmg.getSection("amount");
        for (String key : dmgAmount.keys())
            numbers.put("DAMAGE" + key.toUpperCase(), dmgAmount.getFloat(key));
        DataSection dmgTicks = dmg.getSection("ticks");
        for (String key : dmgTicks.keys())
            ticks.put("DAMAGE" + key.toUpperCase(), dmgTicks.getInt(key));

        // Potions
        DataSection potions = config.getSection("potions");
        DataSection potionAmount = potions.getSection("amount");
        for (String key : potionAmount.keys())
            numbers.put("POTION" + key.toUpperCase(), potionAmount.getFloat(key));
        DataSection potionTicks = potions.getSection("ticks");
        for (String key : potionTicks.keys())
            ticks.put("POTION" + key.toUpperCase(), potionTicks.getInt(key));

        // Stack size
        DataSection inventory = config.getSection("items");
        stackSize = inventory.getInt("stacksize");
        globalData.set(GlobalSetting.ARMOR_STAND_DROPS.key(), inventory.getBoolean("armorstanddrops"));

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
        DataSection exp = config.getSection("experience");
        globals.put(GlobalSetting.ENCHANTMENTS.key(), exp.getBoolean("enchantments"));
        globals.put(GlobalSetting.ANVILS.key(), exp.getBoolean("anvils"));
        globals.put(GlobalSetting.DEATH.key(), exp.getBoolean("death"));

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
            numbers.put(key.toUpperCase(), spawner.getFloat(key));

        // Entity property settings
        DataSection props = spawnFile.getSection("attributes");
        for (String key : props.keys())
            numbers.put(key.toUpperCase(), spawner.getFloat(key));
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
        return setting != null && globals.get(setting.key());
    }

    /**
     * Retrieves a numerical setting
     *
     * @param setting setting type
     * @return set value
     */
    public float getValue(ValueSetting setting)
    {
        if (setting == null || !numbers.containsKey(setting.key()))
            return 0;
        return numbers.get(setting.key());
    }

    /**
     * Gets a tick setting
     *
     * @param setting setting to get
     * @return number of ticks
     */
    public int getTicks(TickSetting setting)
    {
        if (setting == null || !ticks.containsKey(setting.key()))
            return 0;
        return ticks.get(setting.key());
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
        return entity != null && spawns.contains(Conversion.getConfigName(entity));
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
        if (world == null || !locations.containsKey(world.getName()))
            return null;
        return locations.get(world.getName());
    }

    /**
     * Retrieves tame settings for a given entity type
     *
     * @param type entity type (as according to the Conversion class)
     * @return tamed settings for the entity
     */
    public TameSettings getTameSettings(String type)
    {
        if (type == null || !tame.containsKey(type))
            return null;
        return tame.get(type);
    }
}
