/**
 * MineNight
 * com.sucy.minenight.world.enums.GlobalSetting
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
 * Available settings in the global category
 */
public enum GlobalSetting
{
    // experience
    ANVILS,
    DEATH,
    ENCHANTMENTS,

    // mechanics.players
    MAP_UPDATES,
    SIGN_FORMATS,
    TORCH_LIGHT,
    TREE_FALLING,

    // mechanics.respawn
    AUTOMATIC_RESPAWN,
    AUTOMATIC_RESPAWN_VOID,

    // damage
    DAMAGE_FALL,
    DAMAGE_DROWN,
    DAMAGE_SUFFOCATE,
    DAMAGE_STARVATION,
    DAMAGE_CONTACT,
    DAMAGE_FIRE,
    DAMAGE_LAVA,
    DAMAGE_LIGHTNING,
    DAMAGE_ANVIL,
    DAMAGE_VOID,

    // potion
    POTION_HEAL,
    POTION_REGENERATION,
    POTION_HEALTH_BOOST,
    POTION_ABSORPTION,
    POTION_SATURATION,
    POTION_HUNGER,
    POTION_EXHAUSTION,
    POTION_DAMAGE_RESISTANCE,
    POTION_HARM,
    POTION_POTION,
    POTION_WITHER,
    POTION_MOVE_SPEED,
    POTION_SLOW_DOWN,
    POTION_DRINK_SPEED,
    POTION_FAST_DIGGING,
    POTION_SLOW_DIGGING,
    POTION_DAMAGE_BOOST,
    POTION_WEAKNESS,
    POTION_JUMP,
    POTION_LEVITATION,
    POTION_LUCK,
    POTION_UNLUCK,

    // mechanics.server
    ARMOR_STAND_DROPS,
    CHUNK_GENERATION,
    CHUNK_MEMORY,
    ENTITY_DESTROY,
    EXPLODE_DESTROY,
    FIRE_NATURAL,
    FIRE_SPREAD,
    PORTAL_CREATION,
    SLEEP,
    WEATHER;

    /**
     * @return key used in the global settings
     */
    public String key()
    {
        return name().replace("_", "");
    }
}
