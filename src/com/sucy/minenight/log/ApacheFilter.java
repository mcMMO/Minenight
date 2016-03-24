/**
 * MineNight
 * com.sucy.minenight.log.ApacheFilter
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
package com.sucy.minenight.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;


/**
 * Utility class for filtering log messages from the server
 */
public class ApacheFilter implements Filter
{
    private LogType type;

    /**
     * @param type type of log being watched
     */
    public ApacheFilter(LogType type)
    {
        this.type = type;
    }

    @Override
    public Result getOnMismatch()
    {
        return Result.DENY;
    }

    @Override
    public Result getOnMatch()
    {
        return Result.ACCEPT;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String s, Object... objects)
    {
        com.sucy.minenight.log.Logger.log("Checking " + s);
        return getResult(level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object o, Throwable throwable)
    {
        return getResult(level);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message message, Throwable throwable)
    {
        com.sucy.minenight.log.Logger.log("Checking " + message.getFormat());
        return getResult(level);
    }

    @Override
    public Result filter(LogEvent logEvent)
    {
        return getResult(Level.DEBUG);
    }

    private Result getResult(Level level)
    {
        int setting = com.sucy.minenight.log.Logger.getLevel(type);
        if (level == Level.FATAL || level == Level.ERROR)
            return Result.ACCEPT;
        else if (level == Level.WARN)
            return setting > 0 ? Result.ACCEPT : Result.DENY;
        else if (level == Level.INFO)
            return setting > 1 ? Result.ACCEPT : Result.DENY;
        else
            return setting > 2 ? Result.ACCEPT : Result.DENY;
    }
}