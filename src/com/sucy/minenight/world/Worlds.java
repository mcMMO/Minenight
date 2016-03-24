/**
 * MineNight
 * com.sucy.minenight.world.Worlds
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
package com.sucy.minenight.world;

import com.sucy.minenight.Minenight;
import com.sucy.minenight.nms.NMS;
import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.world.data.WorldSettings;
import com.sucy.minenight.world.enums.GlobalSetting;
import com.sucy.minenight.world.listener.EntityListener;
import com.sucy.minenight.world.listener.WorldListener;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class Worlds
{
    private static WorldSettings settings;

    public Worlds(DataSection config)
    {
        settings = new WorldSettings(config);

        Minenight.registerListener(new EntityListener());
        Minenight.registerListener(new WorldListener());

        if (!settings.isEnabled(GlobalSetting.CHUNK_GENERATION))
            for (World world : Bukkit.getWorlds())
                NMS.getManager().stopChunks(world);
    }

    public void cleanup()
    {

    }

    public static WorldSettings getSettings()
    {
        return settings;
    }
}
