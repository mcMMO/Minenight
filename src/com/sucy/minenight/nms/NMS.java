/**
 * MineNight
 * com.sucy.minenight.nms.NMS
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
package com.sucy.minenight.nms;

import com.sucy.minenight.nms.v1_9_R1.NMSManager_19;
import com.sucy.minenight.log.Logger;

/**
 * Handles setting up and grabbing the manager for NMS functions
 */
public class NMS
{
    private static NMSManager manager;

    /**
     * Initializes the NMS functions
     */
    public static void initialize()
    {
        try
        {
            manager = new NMSManager_19();
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to initialize NMS Manager - " + ex.getMessage());
        }
    }

    /**
     * Checks whether or not NMS functions are supported
     *
     * @return true if supported, false otherwise
     */
    public static boolean isSupported()
    {
        return manager != null;
    }

    /**
     * Retrieves the active manager for NMS classes
     *
     * @return NMS manager
     */
    public static NMSManager getManager()
    {
        return manager;
    }
}
