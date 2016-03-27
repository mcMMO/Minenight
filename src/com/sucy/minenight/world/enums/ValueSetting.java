/**
 * MineNight
 * com.sucy.minenight.world.enums.ValueSetting
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
 * Available settings using numbers as their values
 */
public enum ValueSetting
{
    //entity.attributes
    BABY_SPEED_BOOST,
    HORSE_JUMP_STRENGTH,
    HORSE_WEIGHT,

    // entity.spawner
    DELAY,
    DESPAWN_RANGE,
    MAX_NEARBY_ENTITIES,
    MAX_SPAWN_DELAY,
    MIN_SPAWN_DELAY,
    REQUIRED_PLAYER_RANGE,
    SPAWN_COUNT,
    SPAWN_RANGE,
    SPAWN_REINFORCEMENTS,
    SPAWN_REFINFOREMENTS_CALLER,
    SPAWN_REINFORCEMENTS_CALLEE,

    // mechanics.generic
    ATTACK_DAMAGE,
    ATTACK_SPEED,
    KNOCKBACK_RESISTANCE,
    LUCK,
    MOVEMENT_SPEED,
    SPRINTING_SPEED_BOOST,

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
    POTION_POISON,
    POTION_WITHER,
    POTION_MOVE_SPEED,
    POTION_MOVE_SLOW_DOWN,
    POTION_DRINK_SPEED,
    POTION_FAST_DIGGING,
    POTION_SLOW_DIGGING,
    POTION_DAMAGE_BOOST,
    POTION_WEAKNESS,
    POTION_JUMP,
    POTION_LEVITATION,
    POTION_LUCK,
    POTION_UNLUCK,

    // mechanics.damage
    ANVIL,
    CONTACT,
    DROWN,
    FALL,
    FIRE,
    LAVA,
    LIGHTNING,
    STARVATION,
    SUFFOCATE,
    VOID;

    /**
     * @return key used in the global settings
     */
    public String key()
{
    return name().replace("_", "");
}
}
