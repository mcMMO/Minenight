/**
 * MineNight
 * com.sucy.minenight.pet.PetData
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
package com.sucy.minenight.pet;

import com.sucy.minenight.util.Conversion;
import com.sucy.minenight.world.Worlds;
import com.sucy.minenight.world.data.TameSettings;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PetData
{
    private HashMap<String, Integer> counts = new HashMap<String, Integer>();

    private ArrayList<UUID> pets = new ArrayList<UUID>();

    public void tame(Entity entity)
    {
        String name = Conversion.getConfigName(entity);
        TameSettings settings = Worlds.getSettings().getTameSettings(name);
        if (settings == null || settings.limit <= getCount(name))
            return;

        counts.put(name, getCount(name));
        pets.add(entity.getUniqueId());
    }

    private int getCount(String key)
    {
        if (counts.containsKey(key))
            return counts.get(key);
        else
            return 0;
    }
}
