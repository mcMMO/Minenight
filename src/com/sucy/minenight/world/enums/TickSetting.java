/**
 * MineNight
 * com.sucy.minenight.world.enums.TickSetting
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
package com.sucy.minenight.world.enums;

/**
 * Available settings in the tick category
 */
public enum TickSetting
{
    ACTIONS_COMMANDS,
    ACTIONS_CHAT,
    ACTIONS_MENU,

    DAMAGE_DROWN,
    DAMAGE_SUFFOCATE,
    DAMAGE_STARVATION,
    DAMAGE_CONTACT,
    DAMAGE_FIRE,
    DAMAGE_LAVA,
    DAMAGE_VOID,

    POTION_REGENERATION,
    POTION_SATURATION,
    POTION_HUNGER,
    POTION_EXHAUSTION,
    POTION_POISON,
    POTION_WITHER,

    STAMINA_BLOCK,
    STAMINA_HEALTH,
    STAMINA_EXHAUSTION,
    STAMINA_SATURATION,
    STAMINA_SUFFOCATION,

    TELEPORT_COOLDOWN,
    TELEPORT_DELAY,
    TELEPORT_GOD,
    SPAWN_GOD;

    /**
     * @return key used in the global settings
     */
    public String key()
    {
        return name().replace("_", "");
    }
}
