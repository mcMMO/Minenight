/**
 * MineNight
 * com.sucy.minenight.protection.effect.FlagTask
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
package com.sucy.minenight.protection.effect;

import com.sucy.minenight.Minenight;
import com.sucy.minenight.protection.Protection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task that handles applying recurring flag effects such as
 * heal or harm.
 */
public class FlagTask extends BukkitRunnable
{
    Protection protection;

    /**
     * Sets up the task on initialization
     */
    public FlagTask(Protection protection)
    {
        this.protection = protection;
        runTaskTimer(Minenight.getPlugin(), 20, 20);
    }

    /**
     * Runs the task, applying it to all affected players
     */
    @Override
    public void run()
    {
        // Heal effect
        EffectTimer effect = protection.getHealEffect();
        if (effect.tick())
            for (Player player : effect.getPlayers())
                player.setHealth(StrictMath.min(player.getHealth() + effect.getAmount(), player.getMaxHealth()));

        // Hurt effect
        effect = protection.getHurtEffect();
        if (effect.tick())
            for (Player player : effect.getPlayers())
                player.damage(effect.getAmount());
    }
}
