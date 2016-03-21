/**
 * MineNight
 * com.sucy.minenight.protection.zone.ZoneArray
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
package com.sucy.minenight.protection.zone;

import com.sucy.minenight.util.log.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Optimized list for zone management using the assumptions
 * that zones do not need to be garbage collected as long as the
 * array is active and all accesses will use valid indices.
 * The code for this implementation was based of Java's ArrayList
 * found here:
 * http://www.docjar.com/html/api/java/util/ArrayList.java.html
 */
public class ZoneList implements Iterable<Zone>, Iterator<Zone>
{
    private transient Zone[] data;
    private int size;
    private int capacity;

    /**
     * Initializes a new list with a starting size of 4
     * due to the assumption that there won't be many zones
     * stacked on top of one another
     */
    public ZoneList()
    {
        capacity = 4;
        data = new Zone[capacity];
    }

    /**
     * Clears the list
     */
    public void clear()
    {
        size = 0;
    }

    /**
     * @return size of the list
     */
    public int size()
    {
        return size;
    }

    /**
     * Adds a zone to the list, expanding the list if necessary
     *
     * @param value zone to add
     */
    public void add(Zone value)
    {
        if (size == capacity)
        {
            capacity <<= 1;
            data = Arrays.copyOf(data, capacity);
        }
        data[size] = value;
        size++;
    }

    /**
     * Retrieves a zone by index
     *
     * @param index array index
     * @return zone at the index
     */
    public Zone get(int index)
    {
        return data[index];
    }

    /**
     * Returns itself as an iterator
     *
     * @return this
     */
    public Iterator<Zone> iterator()
    {
        next = 0;
        return this;
    }

    // Iterator requirements

    private int next;

    /**
     * Checks if there is another element while iterating
     *
     * @return true if has another, false otherwise
     */
    public boolean hasNext()
    {
        return next < size;
    }

    /**
     * Gets the next item when iterating
     *
     * @return next item
     */
    public Zone next()
    {
        return data[next++];
    }

    /**
     * Not implemented - no need to remove zones
     */
    public void remove()
    {
        throw new NotImplementedException();
    }
}
