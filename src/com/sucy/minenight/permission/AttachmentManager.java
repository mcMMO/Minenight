/**
 * MineNight
 * com.sucy.minenight.permission.AttachmentManager
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

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.List;

/**
 * Handles adding and removing attachments from players
 */
public class AttachmentManager
{
    private Player                     player;
    private List<PermissionAttachment> attachmentStore;

    /**
     * @param player          player to manage
     * @param attachmentStore player's attachment list
     */
    public AttachmentManager(Player player, List<PermissionAttachment> attachmentStore)
    {
        this.player = player;
        this.attachmentStore = attachmentStore;
    }

    /**
     * @param attachment attachment to add to the player
     */
    public void add(PermAttachment attachment)
    {
        attachmentStore.add(attachment);
    }

    /**
     * @param attachment attachment to remove from the player
     */
    public void remove(PermAttachment attachment)
    {
        attachmentStore.remove(attachment);
    }

    /**
     * Clears all attachments from the player
     */
    public void clear()
    {
        attachmentStore.clear();
    }
}
