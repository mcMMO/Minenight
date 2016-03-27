/**
 * MineNight
 * com.sucy.minenight.util.config.parse.JSONArray
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
package com.sucy.minenight.util.config.parse;

import java.util.ArrayList;

public class JSONArray
{
    private ArrayList<Object> values = new ArrayList<Object>();

    public JSONObject getObject(int index)
    {
        return (JSONObject)values.get(index);
    }

    public JSONArray getArray(int index)
    {
        return (JSONArray)values.get(index);
    }

    public String getString(int index)
    {
        return values.get(index).toString();
    }

    public int getInt(int index)
    {
        return Integer.parseInt(values.get(index).toString());
    }

    public long getLong(int index)
    {
        return Long.parseLong(values.get(index).toString());
    }

    public float getFloat(int index)
    {
        return Float.parseFloat(values.get(index).toString());
    }

    public double getDouble(int index)
    {
        return Double.parseDouble(values.get(index).toString());
    }

    public int size()
    {
        return values.size();
    }

    protected int parse(String text, int index)
    {
        if (text.charAt(index) != '[') return index;
        if (text.charAt(index) == ']') return index + 2;

        JSONObject obj;
        JSONArray arr;
        Object value;
        int end;
        while (text.charAt(index) != ']')
        {
            index++;

            // Grab value
            switch (text.charAt(index))
            {
                case '{':
                    obj = new JSONObject();
                    index = obj.parse(text, index);
                    value = obj;
                    break;
                case '[':
                    arr = new JSONArray();
                    index = arr.parse(text, index);
                    value = arr;
                    break;
                case '"':
                    end = text.indexOf('"', index + 1);
                    value = text.substring(index + 1, end);
                    index = end + 1;
                    break;
                default:
                    end = next(text, index);
                    value = text.substring(index, end);
                    index = end;
                    break;
            }

            values.add(value);
        }

        return index + 1;
    }

    private int next(String text, int index)
    {
        while (true)
        {
            switch (text.charAt(index))
            {
                case ',':
                    return index;
                case ']':
                    return index;
                default:
                    index++;
            }
        }
    }

    public void toString(StringBuilder sb)
    {
        boolean first = true;
        sb.append('[');
        for (Object obj : values)
        {
            if (first) first = false;
            else sb.append(',');

            sb.append(obj.toString());
        }
        sb.append(']');
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        toString(sb);
        return sb.toString();
    }
}
