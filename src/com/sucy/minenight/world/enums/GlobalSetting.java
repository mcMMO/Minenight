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
    CHUNK_GENERATION,
    AUTOMATIC_RESPAWN,
    AUTOMATIC_RESPAWN_VOID,
    WEATHER,
    SLEEP,
    SIGN_FORMATS,
    FIRE_SPREAD,
    FIREBALL_FIRE,
    FIREBALL_DESTROY,
    WITHER_DESTROY,
    ENDERDRAGON_DESTROY,
    TNT_DESTROY,
    CREEPER_DESTROY,
    ENDERMAN_DESTROY,
    PORTAL_CREATION;

    /**
     * @return key used in the global settings
     */
    public String key()
    {
        return name().replace("_", "");
    }
}
