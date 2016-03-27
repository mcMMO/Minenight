/**
 * MineNight
 * com.sucy.minenight.protection.Protection
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
package com.sucy.minenight.protection;

import com.sucy.minenight.Minenight;
import com.sucy.minenight.protection.effect.EffectTimer;
import com.sucy.minenight.protection.effect.FlagTask;
import com.sucy.minenight.protection.listener.ChunkListener;
import com.sucy.minenight.protection.listener.FlagListener;
import com.sucy.minenight.protection.listener.MovementListener;
import com.sucy.minenight.protection.zone.Zone;
import com.sucy.minenight.protection.zone.ZoneFlag;
import com.sucy.minenight.protection.zone.ZoneManager;
import com.sucy.minenight.util.ListenerUtil;
import com.sucy.minenight.util.config.parse.DataSection;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Handles zones and the flags inside of them
 */
public class Protection
{
    private static HashMap<ZoneFlag, String> permissions = new HashMap<ZoneFlag, String>();

    private static final String ZONES  = "zones";
    private static final String FLAGS  = "flags";
    private static final String TICKS  = "ticks";
    private static final String AMOUNT = "amount";

    private FlagTask    flagTask;
    private EffectTimer hurtEffect;
    private EffectTimer healEffect;

    /**
     * Checks if the player has the needed
     * permission to bypass the flag effects
     *
     * @param player player to check for
     * @param flag   flag to check for
     *
     * @return true if has bypass perms, false otherwise
     */
    public static boolean hasPermissions(Player player, Zone zone, ZoneFlag flag)
    {
        if (!permissions.containsKey(flag))
            return false;

        String perm = permissions.get(flag);
        return player.hasPermission(perm) || player.hasPermission(perm + "." + zone.getName());
    }

    /**
     * Sets up the plugin, loading config data and setting up listeners
     */
    public Protection()
    {
        DataSection config = Minenight.getConfigData("protection", false, false);

        loadFlagSettings(config.getSection(FLAGS));
        ZoneManager.init(config.getSection(ZONES));

        flagTask = new FlagTask(this);

        ListenerUtil.register(new FlagListener(this));
        ListenerUtil.register(new ChunkListener());
        ListenerUtil.register(new MovementListener());
    }

    /**
     * Cleans up the plugin, saving necessary data
     */
    public void cleanup()
    {
        permissions.clear();
        flagTask.cancel();

        ZoneManager.cleanup();
    }

    /**
     * @return flag effect for hurting players
     */
    public EffectTimer getHurtEffect()
    {
        return hurtEffect;
    }

    /**
     * @return flag effect for healing players
     */
    public EffectTimer getHealEffect()
    {
        return healEffect;
    }

    /**
     * Loads settings from the flag section of the config
     */
    private void loadFlagSettings(DataSection data)
    {
        permissions.put(ZoneFlag.PROTECT, "protection.protect.bypass");
        permissions.put(ZoneFlag.RESTRICT, "protection.restrict.bypass");

        DataSection heal = data.getSection("heal");
        int ticks = heal.getInt(TICKS);
        int amount = heal.getInt(AMOUNT);
        healEffect = new EffectTimer(ticks, amount);

        DataSection hurt = data.getSection("hurt");
        ticks = hurt.getInt(TICKS);
        amount = hurt.getInt(AMOUNT);
        hurtEffect = new EffectTimer(ticks, amount);
    }
}
