/**
 * SkillAPI
 * com.sucy.skill.listener.ListenerUtil
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
package com.sucy.minenight.util;

import com.sucy.minenight.Minenight;
import com.sucy.minenight.log.Logger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for listeners
 */
public class ListenerUtil
{
    /**
     * Registers events, ignoring plugin enabled requirements
     *
     * @param listener listener to register
     */
    @SuppressWarnings("unchecked")
    public static void register(Listener listener)
    {
        Minenight plugin = Minenight.getPlugin();
        try
        {
            for (Map.Entry entry : plugin.getPluginLoader().createRegisteredListeners(listener, plugin).entrySet())
                getEventListeners(getRegistrationClass((Class) entry.getKey())).registerAll((Collection) entry.getValue());
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to register listener " + listener.getClass().getName());
            ex.printStackTrace();
        }
    }

    private static HandlerList getEventListeners(Class<? extends Event> type)
        throws Exception
    {
        Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList");
        method.setAccessible(true);
        return (HandlerList) method.invoke(null);
    }

    private static Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz)
    {
        try
        {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
            if ((clazz.getSuperclass() != null) &&
                (!clazz.getSuperclass().equals(Event.class)) &&
                (Event.class.isAssignableFrom(clazz.getSuperclass())))
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
        }
        return null;
    }

    /**
     * Retrieves a damager from an entity damage event which will get the
     * shooter of projectiles if it was a projectile hitting them or
     * converts the Entity damager to a LivingEntity if applicable.
     *
     * @param event event to grab the damager from
     *
     * @return LivingEntity damager of the event or null if not found
     */
    public static LivingEntity getDamager(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof LivingEntity)
        {
            return (LivingEntity) event.getDamager();
        }
        else if (event.getDamager() instanceof Projectile)
        {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof LivingEntity)
            {
                return (LivingEntity) projectile.getShooter();
            }
        }
        return null;
    }

    /**
     * Retrieves a damager from an entity damage event which will get the
     * shooter of projectiles if it was a projectile hitting them or
     * converts the Entity damager to a Player if applicable.
     *
     * @param event event to grab the damager from
     *
     * @return Player damager of the event or null if not found
     */
    public static Player getPlayerDamager(EntityDamageByEntityEvent event)
    {
        if (event.getDamager() instanceof Player)
        {
            return (Player) event.getDamager();
        }
        else if (event.getDamager() instanceof Projectile)
        {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player)
            {
                return (Player) projectile.getShooter();
            }
        }
        return null;
    }
}
