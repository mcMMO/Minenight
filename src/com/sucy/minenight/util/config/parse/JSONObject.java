/**
 * MineNight
 * com.sucy.minenight.util.config.parse.JSONObject
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

import com.sucy.minenight.log.Logger;
import com.sun.xml.internal.fastinfoset.Encoder;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a JSON object that contains a series of key/value pairs
 */
public class JSONObject
{
    private HashMap<String, Object> values = new HashMap<String, Object>();

    public Set<String> keys()
    {
        return values.keySet();
    }

    public int size()
    {
        return values.size();
    }

    public JSONObject getObject(String key)
    {
        return (JSONObject)values.get(key);
    }

    public JSONArray getArray(String key)
    {
        return (JSONArray)values.get(key);
    }

    public String getString(String key)
    {
        return values.get(key).toString();
    }

    public int getInt(String key)
    {
        return Integer.parseInt(values.get(key).toString());
    }

    public long getLong(String key)
    {
        return Long.parseLong(values.get(key).toString());
    }

    public float getFloat(String key)
    {
        return Float.parseFloat(values.get(key).toString());
    }

    public double getDouble(String key)
    {
        return Double.parseDouble(values.get(key).toString());
    }

    public void set(String key, Object obj)
    {
        values.put(key, obj);
    }

    public void setMap(String key, Map<String, ?> map)
    {
        JSONObject obj = new JSONObject();
        for (String mapKey : map.keySet())
            obj.set(mapKey, map.get(mapKey));
        set(key, obj);
    }

    /**
     * Empty constructor for when not loading
     */
    public JSONObject() { }

    /**
     * Loads JSON from a file
     *
     * @param file file to parse from
     */
    public JSONObject(File file)
    {
        try
        {
            if (file.exists())
            {
                FileInputStream read = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                read.read(data);
                read.close();
                String text = new String(data, "UTF-8");
                parse(text, 0);
            }
        }
        catch (Exception ex)
        {
            // Do nothing
            Logger.bug("Failed to parse JSON data - " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Loads JSON from a string
     *
     * @param json json string
     */
    public JSONObject(String json)
    {
        parse(json, 0);
    }

    protected int parse(String text, int index)
    {
        if (text.charAt(index) != '{') return index;
        if (text.charAt(index + 1) == '}') return index + 2;

        JSONObject obj;
        JSONArray arr;
        Object value;
        String key;
        int next, end;
        while (text.charAt(index) != '}')
        {
            index++;

            next = text.indexOf(':', index);

            // Grab key
            if (text.charAt(index) == '"')
                key = text.substring(index + 1, next - 1);
            else
                key = text.substring(index, next);

            // Grab value
            switch (text.charAt(next + 1))
            {
                case '{':
                    obj = new JSONObject();
                    index = obj.parse(text, next + 1);
                    value = obj;
                    break;
                case '[':
                    arr = new JSONArray();
                    index = arr.parse(text, next + 1);
                    value = arr;
                    break;
                case '"':
                    end = text.indexOf('"', next + 2);
                    value = text.substring(next + 2, end);
                    index = end + 1;
                    break;
                default:
                    end = next(text, next + 1);
                    value = text.substring(next + 1, end);
                    index = end;
                    break;
            }

            values.put(key, value);
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
                case '}':
                    return index;
                default:
                    index++;
            }
        }
    }

    /**
     * Dumps the data contents to a file to the given file
     *
     * @param file file to save to
     */
    public void save(File file)
    {
        try
        {
            new File(file.getPath().substring(0, file.getPath().lastIndexOf(File.separator))).mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            BufferedWriter write = new BufferedWriter(new OutputStreamWriter(out, Encoder.UTF_8));
            write.write(toString());
            write.close();
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to save JSON data to file - " + ex.getMessage());
        }
    }

    public void toString(StringBuilder sb)
    {
        boolean first = true;
        sb.append('{');
        for (Map.Entry<String, Object> entry : values.entrySet())
        {
            if (first) first = false;
            else sb.append(',');

            sb.append(entry.getKey());
            sb.append(':');
            sb.append(entry.getValue().toString());
        }
        sb.append('}');
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        toString(sb);
        return sb.toString();
    }
}
