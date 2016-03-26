/**
 * MineNight
 * com.sucy.minenight.world.listener.EntityListener
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
package com.sucy.minenight.world.listener;

import com.sucy.minenight.world.Worlds;
import com.sucy.minenight.world.enums.GlobalSetting;
import com.sucy.minenight.world.enums.ValueSetting;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener handling global settings related to entities
 */
public class EntityListener implements Listener
{
    /**
     * Hides the NBT data of an item when it is spawned
     *
     * @param event event details
     */
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event)
    {
        ItemStack result = Worlds.getSettings().hide(event.getEntity().getItemStack());
        event.getEntity().setItemStack(result);
    }

    /**
     * Stops portal creation
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPortal(PortalCreateEvent event)
    {
        check(event, GlobalSetting.PORTAL_CREATION);
    }

    /**
     * Explosion prevention
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onExplode(EntityExplodeEvent event)
    {
        if (!Worlds.getSettings().isEnabled(GlobalSetting.EXPLODE_DESTROY))
            event.blockList().clear();
    }

    /**
     * Enderman prevention
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEnderman(EntityChangeBlockEvent event)
    {
        check(event, GlobalSetting.ENTITY_DESTROY);
    }

    /**
     * Controls what can spawn
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawn(EntitySpawnEvent event)
    {
        if (!Worlds.getSettings().canSpawn(event.getEntity()))
            event.setCancelled(true);
    }

    /**
     * Modifies damage dealt by environmental sources
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event)
    {
        switch (event.getCause())
        {
            case FALLING_BLOCK:
                set(event, ValueSetting.ANVIL);
                break;
            case CONTACT:
                set(event, ValueSetting.CONTACT);
                break;
            case DROWNING:
                set(event, ValueSetting.DROWN);
                break;
            case FALL:
                set(event, ValueSetting.FALL);
                break;
            case FIRE:
                set(event, ValueSetting.FIRE);
                break;
            case LAVA:
                set(event, ValueSetting.LAVA);
                break;
            case LIGHTNING:
                set(event, ValueSetting.LIGHTNING);
                break;
            case STARVATION:
                set(event, ValueSetting.STARVATION);
                break;
            case SUFFOCATION:
                set(event, ValueSetting.SUFFOCATE);
                break;
            case VOID:
                set(event, ValueSetting.VOID);
                break;
        }
    }

    /**
     * Sets the amount of damage for an event
     *
     * @param event   event to set for
     * @param setting type of setting to apply
     */
    private void set(EntityDamageEvent event, ValueSetting setting)
    {
        event.setDamage(event.getDamage() * Worlds.getSettings().getValue(setting));
        if (event.getDamage() == 0)
            event.setCancelled(true);
    }

    /**
     * Checks whether or not a setting is active and applies
     * it to the cancellable event
     *
     * @param event   event details
     * @param setting setting to check
     */
    private void check(Cancellable event, GlobalSetting setting)
    {
        event.setCancelled(!Worlds.getSettings().isEnabled(setting));
    }


}
