/**
 * MineNight
 * com.sucy.minenight.permission.PermAttachment
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

import com.sucy.minenight.Minenight;
import com.sucy.minenight.util.reflect.Reflection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;

import java.util.LinkedHashMap;

/**
 * An extension of the PermissionAttachment class that allows
 * for setting of multiple permissions before recalculating
 */
public class PermAttachment extends PermissionAttachment
{
    private final LinkedHashMap<String, Boolean> permList;
    private       Player                         player;
    private boolean dirty = false;

    /**
     * Creates an attachment for the given player
     *
     * @param player player to manage
     */
    public PermAttachment(Permissions permissions, Player player)
    {
        super(Minenight.getPlugin(), player);
        Reflection.getValue(player, "perm");
        this.player = player;
        permList = permissions.getPermList(this);
    }

    /**
     * Sets a permission for the player, not applying it immediately
     *
     * @param name  permission name
     * @param value permission value
     */
    public void setPerm(String name, boolean value)
    {
        permList.put(name, value);
        dirty = true;
    }

    /**
     * Sets a permission for the palyer, not applying it immediately
     *
     * @param perm  permission to set
     * @param value permission value
     */
    public void setPerm(Permission perm, boolean value)
    {
        setPerm(perm.getName(), value);
        dirty = true;
    }

    /**
     * Applies any changed permissions set through
     */
    public void finish()
    {
        if (dirty)
        {
            player.recalculatePermissions();
            dirty = false;
        }
    }
}
