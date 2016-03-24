/**
 * MineNight
 * com.sucy.minenight.util.MobNames
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
package com.sucy.minenight.util;

import org.bukkit.entity.*;

import java.util.HashMap;

public class Conversion
{
    private static HashMap<EntityType, String> ENTITY_NAMES = new HashMap<EntityType, String>()
    {{
            put(EntityType.PIG_ZOMBIE, "zombiepigman");
            put(EntityType.SNOWMAN, "snowgolem");
            put(EntityType.MUSHROOM_COW, "mooshroom");
        }};

    /**
     * Gets the config name for an entity
     *
     * @param entity entity to get config name for
     *
     * @return config name
     */
    public static String getConfigName(Entity entity)
    {
        // Get base name
        String base;
        EntityType type = entity.getType();
        if (ENTITY_NAMES.containsKey(type))
            base = ENTITY_NAMES.get(type);
        else
            base = type.name().replace("_", "");

        // Sub-types for specific mobs
        switch (type)
        {
            case SKELETON:
                if (((Skeleton) entity).getSkeletonType() == Skeleton.SkeletonType.WITHER)
                    base += "wither";
                break;
            case ZOMBIE:
                if (((Zombie) entity).isVillager())
                    base += "villager";
                break;
            case GUARDIAN:
                if (((Guardian) entity).isElder())
                    base += "elder";
                break;
        }

        // Special types
        if (entity instanceof Ageable && !((Ageable) entity).isAdult())
            base += "baby";

        return base;
    }
}
