/**
 * MCCore
 * com.rit.sucy.config.LocationData
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
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
package com.sucy.minenight.util.config;

import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.util.config.parse.JSONObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods of serializing and then parsing back
 * location data for config storage
 */
public class LocationData
{
    /**
     * Parses a location from config data
     *
     * @param data section to parse
     *
     * @return parsed location
     */
    public static Location parse(DataSection data)
    {
        return parse(data.getString("world"), data);
    }

    /**
     * Parse from config data with an already provided world name
     *
     * @param world world name
     * @param data  data to parse from
     *
     * @return parsed location
     */
    public static Location parse(String world, DataSection data)
    {
        return new Location(
            Bukkit.getWorld(world),
            data.getDouble("x", 0),
            data.getDouble("y", 0),
            data.getDouble("z", 0),
            data.getFloat("yaw", 0),
            data.getFloat("pitch", 0)
        );
    }

    /**
     * Saves location data into JSON format
     *
     * @param loc location data to save
     * @return JSON data
     */
    public static JSONObject asJSON(Location loc)
    {
        JSONObject json = new JSONObject();
        json.set("w", loc.getWorld().getName());
        json.set("x", loc.getX());
        json.set("y", loc.getY());
        json.set("z", loc.getZ());
        json.set("a", loc.getYaw());
        json.set("p", loc.getPitch());
        return json;
    }

    /**
     * Loads location data from JSON
     *
     * @param json JSON data to load from
     * @return location data
     */
    public static Location fromJSON(JSONObject json)
    {
        return new Location(
            Bukkit.getWorld(json.getString("w")),
            json.getDouble("x"),
            json.getDouble("y"),
            json.getDouble("z"),
            json.getFloat("a"),
            json.getFloat("p")
        );
    }

    /**
     * <p>Serializes a location using as little space as possible</p>
     * <p>This only keeps the block coordinates instead of the precise coordinates</p>
     * <p>Yaw and pitch are not preserved either</p>
     * <p>Returns null for a null location</p>
     *
     * @param loc location to serialize
     *
     * @return data string
     */
    public static String serializeSimpleLocation(Location loc)
    {

        // Null locations return null
        if (loc == null)
        {
            return null;
        }

        // Serialize the location
        return loc.getWorld().getName() + ","
               + loc.getBlockX() + ","
               + loc.getBlockY() + ","
               + loc.getBlockZ();
    }

    /**
     * <p>Serializes a location with the exact coordinates but without keeping
     * the yaw and pitch</p>
     * <p>Returns null for a null location</p>
     *
     * @param loc location to serialize
     *
     * @return data string
     */
    public static String serializeLocation(Location loc)
    {

        // Null locations return null
        if (loc == null)
        {
            return null;
        }

        // Serialize the location
        return loc.getWorld().getName() + ","
               + loc.getX() + ","
               + loc.getY() + ","
               + loc.getZ();
    }

    /**
     * <p>Serializes all data for a location including exact coordinates,
     * yaw, and pitch</p>
     * <p>Returns null for a null location</p>
     *
     * @param loc location to serialize
     *
     * @return data string
     */
    public static String serializeDetailedLocation(Location loc)
    {

        // Null locations return null
        if (loc == null)
        {
            return null;
        }

        // Serialize the location
        return loc.getWorld().getName() + ","
               + loc.getX() + ","
               + loc.getY() + ","
               + loc.getZ() + ","
               + loc.getYaw() + ","
               + loc.getPitch();
    }

    /**
     * <p>Parses a location from a data string</p>
     * <p>This accepts simple, normal, and detailed locations</p>
     * <p>Returns null for invalid formats or a null data string</p>
     *
     * @param dataString data string to parse
     *
     * @return parsed location
     */
    public static Location parseLocation(String dataString)
    {
        // Must have a comma and not be null
        dataString = dataString.replace(", ", ",");
        if (dataString == null || !dataString.contains(","))
        {
            return null;
        }

        String[] pieces = dataString.split(",");

        // Simple and normal locations
        if (pieces.length == 4)
        {
            return new Location(
                Bukkit.getWorld(pieces[0]),
                Double.parseDouble(pieces[1]),
                Double.parseDouble(pieces[2]),
                Double.parseDouble(pieces[3])
            );
        }

        // Detailed locations
        else if (pieces.length == 6)
        {
            return new Location(
                Bukkit.getWorld(pieces[0]),
                Double.parseDouble(pieces[1]),
                Double.parseDouble(pieces[2]),
                Double.parseDouble(pieces[3]),
                Float.parseFloat(pieces[4]),
                Float.parseFloat(pieces[5])
            );
        }

        // Invalid format
        else return null;
    }

    /**
     * <p>Serializes a list of locations, preserving the block coordinates only</p>
     * <p>If the provided list is null or empty, this method returns null</p>
     *
     * @param locations locations to serialize
     *
     * @return list of data strings
     */
    public static List<String> serializeSimpleLocations(List<Location> locations)
    {
        // Null or empty lists return null
        if (locations == null || locations.size() == 0)
        {
            return null;
        }

        // Serialize the list
        ArrayList<String> list = new ArrayList<String>();
        for (Location location : locations)
        {
            list.add(serializeSimpleLocation(location));
        }
        return list;
    }

    /**
     * <p>Serializes a list of locations, preserving the exact coordinates
     * but not the yaw and pitch</p>
     * <p>If the provided list is null or empty, this method returns null</p>
     *
     * @param locations locations to serialize
     *
     * @return list of data strings
     */
    public static List<String> serializeLocations(List<Location> locations)
    {

        // Null or empty lists return null
        if (locations == null || locations.size() == 0)
        {
            return null;
        }

        // Serialize the list
        ArrayList<String> list = new ArrayList<String>();
        for (Location location : locations)
        {
            list.add(serializeLocation(location));
        }
        return list;
    }

    /**
     * <p>Serializes a list of locations, preserving the exact coordinates,
     * yaw, and pitch</p>
     * <p>If the provided list is null or empty, this method returns null</p>
     *
     * @param locations locations to serialize
     *
     * @return list of data strings
     */
    public static List<String> serializeDetailedLocations(List<Location> locations)
    {
        // Null or empty lists return null
        if (locations == null || locations.size() == 0)
        {
            return null;
        }

        // Serialize the list
        ArrayList<String> list = new ArrayList<String>();
        for (Location location : locations)
        {
            list.add(serializeDetailedLocation(location));
        }
        return list;
    }

    /**
     * <p>Parses a list of locations from a list of data strings</p>
     * <p>This method accepts simple, normal, and detailed data string lists</p>
     * <p>If the provided list is null or empty, this method returns null</p>
     *
     * @param dataStrings data strings to parse
     *
     * @return list of parsed locations
     */
    public static List<Location> parseLocations(List<String> dataStrings)
    {

        // Null or empty lists return null
        if (dataStrings == null || dataStrings.size() == 0)
        {
            return null;
        }

        // Parse the list
        ArrayList<Location> list = new ArrayList<Location>();
        for (String dataString : dataStrings)
        {
            Location loc = parseLocation(dataString);
            if (loc != null) list.add(loc);
        }
        return list;
    }

    /**
     * <p>Serializes a list of locations into a single string, keeping only the
     * block coordinates</p>
     * <p>Returns null if the provided list is null or empty</p>
     *
     * @param locations locations to serialize
     *
     * @return data string
     */
    public static String serializeCompactSimpleLocations(List<Location> locations)
    {

        // Return null for an empty or null list
        if (locations == null || locations.size() == 0)
        {
            return null;
        }

        // Serialize the list
        StringBuilder data = new StringBuilder();
        for (Location loc : locations)
        {
            data.append(serializeSimpleLocation(loc));
            data.append(":");
        }
        return data.length() > 0 ? data.substring(0, data.length() - 1) : null;
    }

    /**
     * <p>Serializes a list of locations into a single string, keeping only the
     * exact coordinates without yaw or pitch</p>
     * <p>Returns null if the provided list is null or empty</p>
     *
     * @param locations locations to serialize
     *
     * @return data string
     */
    public static String serializeCompactLocations(List<Location> locations)
    {

        // Return null for an empty or null list
        if (locations == null || locations.size() == 0)
        {
            return null;
        }

        // Serialize the list
        StringBuilder data = new StringBuilder();
        for (Location loc : locations)
        {
            data.append(serializeLocation(loc));
            data.append(":");
        }
        return data.length() > 0 ? data.substring(0, data.length() - 1) : null;
    }

    /**
     * <p>Serializes a list of locations into a single string, keeping only the
     * exact coordinates without yaw or pitch</p>
     * <p>Returns null if the provided list is null or empty</p>
     *
     * @param locations locations to serialize
     *
     * @return data string
     */
    public static String serializeCompactDetailedLocations(List<Location> locations)
    {

        // Return null for an empty or null list
        if (locations == null || locations.size() == 0)
        {
            return null;
        }

        // Serialize the list
        StringBuilder data = new StringBuilder();
        for (Location loc : locations)
        {
            data.append(serializeDetailedLocation(loc));
            data.append(":");
        }
        return data.length() > 0 ? data.substring(0, data.length() - 1) : null;
    }

    /**
     * <p>Parses a list of locations from a compact data string</p>
     * <p>Compact data strings are the results from the
     * serializeCompactLocation methods</p>
     * <p>Returns null if the data string is null or empty</p>
     *
     * @param dataString compact data string to parse
     *
     * @return parsed list of locations
     */
    public static List<Location> parseCompactLocations(String dataString)
    {

        // Return null for null or empty string
        if (dataString == null || dataString.length() == 0)
        {
            return null;
        }

        // Split the string into locations
        String[] dataStrings;
        if (dataString.contains(":"))
        {
            dataStrings = dataString.split(":");
        }
        else dataStrings = new String[] { dataString };

        // Parse each string
        ArrayList<Location> list = new ArrayList<Location>();
        for (String data : dataStrings)
        {
            Location loc = parseLocation(data);
            if (loc != null) list.add(loc);
        }
        return list;
    }
}
