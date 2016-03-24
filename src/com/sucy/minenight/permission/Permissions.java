/**
 * MineNight
 * com.sucy.minenight.permission.Permission
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
package com.sucy.minenight.permission;

import com.sucy.minenight.util.reflect.Reflection;
import com.sucy.minenight.util.version.VersionManager;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachment;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class Permissions
{
    private HashMap<UUID, AttachmentManager> managers = new HashMap<UUID, AttachmentManager>();

    private Field permBase;
    private Field attachments;
    private Field permissions;

    /**
     * Sets up the permissions segment
     */
    public Permissions()
    {
        try
        {
            String craft = Reflection.getCraftPackage();
            permBase = Class.forName(craft + "entity.CraftHumanEntity")
                .getDeclaredField("perm");
            permBase.setAccessible(true);
            attachments = PermissibleBase.class.getField("permissions");
            permissions = PermissionAttachment.class.getField("permissions");

            for (Player player : VersionManager.getOnlinePlayers())
                manage(player);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Cleans up the permissions segment
     */
    public void cleanup()
    {
        for (AttachmentManager manager : managers.values())
            manager.clear();
        managers.clear();
    }

    /**
     * Fetches the permissions list of a permissions attachment
     *
     * @param attachment attachment to fetch for
     * @return permissions list
     */
    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, Boolean> getPermList(PermissionAttachment attachment)
    {
        try
        {
            return (LinkedHashMap<String, Boolean>)permissions.get(attachment);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new LinkedHashMap<String, Boolean>();
        }
    }

    /**
     * Starts managing the given player
     *
     * @param player player to manage
     */
    @SuppressWarnings("unchecked")
    public void manage(Player player)
    {
        if (managers.containsKey(player.getUniqueId()))
            return;

        try
        {
            managers.put(
                player.getUniqueId(),
                new AttachmentManager(
                    player,
                    (List<PermissionAttachment>) attachments.get(permBase.get(player))
                )
            );
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Stops managing the given player
     *
     * @param player player to stop managing
     */
    public void stopManaging(Player player)
    {
        managers.remove(player.getUniqueId()).clear();
    }
}
