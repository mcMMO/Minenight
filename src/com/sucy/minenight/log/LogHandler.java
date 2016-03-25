/**
 * MineNight
 * com.sucy.minenight.log.LogHandler
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

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Handles outputting messages to the console
 */
public class LogHandler extends Handler
{
    /**
     * Publishes a message to the console
     *
     * @param record message data
     */
    @Override
    public void publish(LogRecord record)
    {
        if (check(record))
            System.out.println(record.getMessage());
    }

    /**
     * Doesn't do anything
     */
    @Override
    public void flush() { }

    /**
     * Doesn't do anything
     *
     * @throws SecurityException
     */
    @Override
    public void close() throws SecurityException { }

    /**
     * Checks whether or not the log message should be output
     *
     * @param record record to check
     *
     * @return true if should be shown
     */
    private boolean check(LogRecord record)
    {
        if (record.getLevel() == Level.SEVERE)
            return true;
        else if (record.getLevel() == Level.WARNING)
            return Logger.getLevel(LogType.SERVER) > 0;
        else
            return Logger.getLevel(LogType.SERVER) > 1;
    }
}
