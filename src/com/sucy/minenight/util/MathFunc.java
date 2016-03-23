/**
 * MineNight
 * com.sucy.minenight.util.MathFunc
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

import org.bukkit.Location;

/**
 * Simple math functions that are commonly used
 */
public class MathFunc
{
    /**
     * Squares a number
     *
     * @param num number to square
     *
     * @return number squared
     */
    public static double sq(double num)
    {
        return num * num;
    }

    /**
     * Calculates distance squared
     *
     * @param x1 point 1 x coord
     * @param y1 point 1 y coord
     * @param x2 point 2 x coord
     * @param y2 point 2 y coord
     *
     * @return distance squared
     */
    public static double dSq(double x1, double y1, double x2, double y2)
    {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return dx * dx + dy * dy;
    }

    /**
     * Converts chunk coordinates to a single integer for hashing
     *
     * @param x chunk X coordinate
     * @param z chunk Y coordinate
     *
     * @return hash integer
     */
    public static int chunkHash(int x, int z)
    {
        int off = 1 << 15;
        return (x + off) | ((z + off) << 16);
    }

    /**
     * Gets chunk coords from the location
     *
     * @param loc location to use
     *
     * @return chunk coordinates
     */
    public static Point getChunk(Location loc)
    {
        return new Point(loc.getBlockX() >> 4, loc.getBlockZ() >> 4);
    }
}
