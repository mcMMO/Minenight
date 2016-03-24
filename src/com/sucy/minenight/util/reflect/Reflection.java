/**
 * MCCore
 * com.rit.sucy.reflect.Reflection
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
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
package com.sucy.minenight.util.reflect;

import com.sucy.minenight.util.log.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * <p>Utility class for performing reflection operations. Only use
 * this class if you know what you're doing.</p>
 */
public class Reflection
{
    private static String   CRAFT;
    private static String   NMS;
    private static Class<?> packetClass;
    private static Method   getHandle;
    private static Method   sendPacket;
    private static Field    connection;

    public static void initialize()
    {
        NMS = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().substring(23) + '.';
        CRAFT = "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().substring(23) + '.';
        try
        {
            getHandle = Class.forName(CRAFT + "entity.CraftPlayer").getMethod("getHandle");

            packetClass = Class.forName(NMS + "Packet");
            connection = Class.forName(NMS + "EntityPlayer").getDeclaredField("playerConnection");
            sendPacket = Class.forName(NMS + "PlayerConnection").getMethod("sendPacket", packetClass);
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to set up reflection utility - " + ex.getMessage());
        }
    }

    /**
     * @return package name for NMS classes
     */
    public static String getNMSPackage()
    {
        return NMS;
    }

    /**
     * @return package name for NMS classes
     */
    public static String getCraftPackage()
    {
        return CRAFT;
    }

    public static Object getConnection(Player player)
        throws Exception
    {
        return connection.get(getHandle.invoke(player));
    }

    /**
     * Retrieves a class by name
     *
     * @param name name of the class including packages
     *
     * @return class object or null if invalid
     */
    public static Class<?> getClass(String name)
    {
        try
        {
            return Class.forName(name);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * Retrieves an NMS class by name
     *
     * @param name name of the class including packages
     *
     * @return class object or null if invalid
     */
    public static Class<?> getNMSClass(String name)
    {
        return getClass(getNMSPackage() + name);
    }

    /**
     * Retrieves a CraftBukkit class by name
     *
     * @param name name of the class including packages
     *
     * @return class object or null if invalid
     */
    public static Class<?> getCraftClass(String name)
    {
        return getClass(getCraftPackage() + name);
    }

    /**
     * Gets an instance of the class
     *
     * @param c    class to get an instance of
     * @param args constructor arguments
     *
     * @return instance of the class or null if unable to create the object
     */
    public static Object getInstance(Class<?> c, Object... args)
    {
        if (c == null) return null;
        try
        {
            for (Constructor<?> constructor : c.getDeclaredConstructors())
            {
                if (constructor.getGenericParameterTypes().length == args.length)
                {
                    return constructor.newInstance(args);
                }
            }
        }
        catch (Exception ex)
        { /* */ }
        return null;
    }

    /**
     * Tries to set a value for the object
     *
     * @param o         object reference
     * @param fieldName name of the field to set
     * @param value     value to set
     */
    public static void setValue(Object o, String fieldName, Object value)
    {
        try
        {
            Field field = o.getClass().getDeclaredField(fieldName);
            if (!field.isAccessible()) field.setAccessible(true);
            field.set(o, value);
        }
        catch (Exception ex)
        { /* Do Nothing */ }
    }

    /**
     * Tries to set a value for the object
     *
     * @param o         object reference
     * @param fieldName name of the field to set
     * @param value     value to set
     */
    public static void setValue(Class<?> clazz, Object o, String fieldName, Object value)
    {
        try
        {
            Field field = clazz.getDeclaredField(fieldName);
            if (!field.isAccessible()) field.setAccessible(true);
            field.set(o, value);
        }
        catch (Exception ex)
        { /* Do Nothing */ }
    }

    /**
     * Tries to get a value from the object
     *
     * @param o         object reference
     * @param fieldName name of the field to retrieve the value from
     *
     * @return the value of the field or null if not found
     */
    public static Object getValue(Object o, String fieldName)
    {
        try
        {
            Field field = o.getClass().getDeclaredField(fieldName);
            if (!field.isAccessible()) field.setAccessible(true);
            return field.get(o);
        }
        catch (Exception ex)
        { /* Do nothing */ }
        return null;
    }

    /**
     * Tries to get a method from the object
     *
     * @param o          object reference
     * @param methodName name of the field to retrieve the value from
     *
     * @return the value of the field or null if not found
     */
    public static Method getMethod(Object o, String methodName, Class<?>... params)
    {
        try
        {
            Method method = o.getClass().getMethod(methodName, params);
            if (!method.isAccessible()) method.setAccessible(true);
            return method;
        }
        catch (Exception ex)
        { /* Do nothing */ }
        return null;
    }

    /**
     * Tries to send a packet to the player
     *
     * @param player player to send to
     * @param packet packet to send
     */
    public static void sendPacket(Player player, Object packet)
        throws Exception
    {
        sendPacket.invoke(getConnection(player), packet);
    }

    /**
     * Sets a value into a static map in a given class
     *
     * @param clazz     map class
     * @param fieldName name of the map field
     * @param key       key to put under
     * @param value     value to put
     */
    @SuppressWarnings("unchecked")
    public static void putStaticMap(Class<?> clazz, String fieldName, Object key, Object value)
    {
        try
        {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Map map = (Map) field.get(null);
            map.put(key, value);
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to set vale to map via reflection - " + ex.getMessage());
        }
    }
}
