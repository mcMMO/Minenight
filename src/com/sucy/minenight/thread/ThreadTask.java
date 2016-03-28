/**
 * MineNight
 * com.sucy.minenight.thread.ThreadTask
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
package com.sucy.minenight.thread;

/**
 * A task that can be added to the main thread
 */
public abstract class ThreadTask implements Runnable
{
    private int delay;

    /**
     * Makes a task that runs immediately
     */
    public ThreadTask()
    {
        delay = 0;
    }

    /**
     * Makes a task that runs after a delay
     *
     * @param delay delay in ticks
     */
    public ThreadTask(int delay)
    {
        this.delay = delay;
    }

    /**
     * Ticks the task, checking if it should run
     *
     * @return true if should run
     */
    public boolean tick()
    {
        return --delay <= 0;
    }
}
