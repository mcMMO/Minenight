/**
 * MineNight
 * com.sucy.minenight.nms.NBT
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
package com.sucy.minenight.nms;

import com.sucy.minenight.log.Logger;
import com.sucy.minenight.util.reflect.Reflection;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/**
 * Handles NBT data for items
 */
public class NBT
{
    // IDs for NBT tags available to be hidden
    public static final int
        ENCHANTMENTS = 1,
        ATTRIBUTES   = 1 << 1,
        UNBREAKABLE  = 1 << 2,
        CAN_DESTROY  = 1 << 3,
        CAN_PLACE_ON = 1 << 4,
        OTHERS       = 1 << 5;

    private static Class<?>
        compound;

    private static Method
        getTag,
        asNMS,
        asCraft,
        setInt,
        setBool;

    /**
     * Initializes the reflection references for the utility
     */
    public static void initialize()
    {
        try
        {
            String nms = Reflection.getNMSPackage();
            String craft = Reflection.getCraftPackage();

            compound = Class.forName(nms + "NBTTagCompound");
            Class<?> item = Class.forName(nms + "ItemStack");
            Class<?> craftItem = Class.forName(craft + "inventory.CraftItemStack");

            getTag = item.getMethod("getTag");
            asNMS = craftItem.getMethod("asNMSCopy", ItemStack.class);
            asCraft = craftItem.getMethod("asCraftMirror", item);
            setBool = compound.getMethod("setBoolean", String.class, boolean.class);
            setInt = compound.getMethod("setInt", String.class, int.class);
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to set up reflection for removing damage lores.");
        }
    }

    /**
     * Hides NBT by a collective ID
     *
     * @param item item to hide for
     * @param id   collective NBT ID
     */
    public static ItemStack hide(ItemStack item, int id)
    {
        try
        {
            Object nms = asNMS.invoke(null, item);
            Object nbt = getTag.invoke(nms);
            if (nbt == null)
                nbt = compound.newInstance();

            setInt.invoke(nbt, "HideFlags", id);

            return (ItemStack) asCraft.invoke(null, nms);
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to set NBT tag for hiding - " + ex.getMessage());
            ex.printStackTrace();
            return item;
        }
    }

    public static ItemStack makeUnbreakable(ItemStack item)
    {
        try
        {
            Object nms = asNMS.invoke(null, item);
            Object nbt = getTag.invoke(nms);

            setBool.invoke(nbt, "Unbreakable", true);

            return (ItemStack) asCraft.invoke(null, nms);
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to make item unbreakable - " + ex.getMessage());
            return null;
        }
    }
}
