package com.sucy.minenight.hologram.display;

import com.sucy.minenight.hologram.display.line.HologramLine;
import com.sucy.minenight.hologram.display.line.ItemLine;
import com.sucy.minenight.hologram.display.line.TextLine;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the manamgent of individual lines for a hologram
 */
public class LineData
{
    private final List<HologramLine> lines;

    private final Visibility visibility;

    private World  world;
    private double x;
    private double y;
    private double z;
    private int    chunkX;
    private int    chunkZ;

    /**
     * @param location location of the hologram
     */
    public LineData(Location location)
    {
        updateLocation(location.getWorld(), location.getX(), location.getY(), location.getZ());

        visibility = new Visibility();
        this.lines = new ArrayList<HologramLine>();
    }

    /**
     * @return visibility handler of the hologram
     */
    public Visibility getVisibility()
    {
        return visibility;
    }

    /**
     * Checks if the hologram is within the given chunk
     *
     * @param chunk chunk to check
     *
     * @return true if in the chunk
     */
    public boolean isInChunk(Chunk chunk)
    {
        return (chunk.getX() == this.chunkX) && (chunk.getZ() == this.chunkZ);
    }

    /**
     * @return world the hologram is in
     */
    public World getWorld()
    {
        return this.world;
    }

    /**
     * @return X position
     */
    public double getX()
    {
        return this.x;
    }

    /**
     * @return Y position
     */
    public double getY()
    {
        return this.y;
    }

    /**
     * @return Z position
     */
    public double getZ()
    {
        return this.z;
    }

    /**
     * @return full Bukkit location of the hologram
     */
    public Location getLocation()
    {
        return new Location(this.world, this.x, this.y, this.z);
    }

    /**
     * Updates internal positions of the hologram
     *
     * @param world world to move to
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    private void updateLocation(World world, double x, double y, double z)
    {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.chunkX = ((int) StrictMath.floor(x) >> 4);
        this.chunkZ = ((int) StrictMath.floor(z) >> 4);
    }

    /**
     * @return hologram lines that shouldn't be tampered with
     */
    public List<HologramLine> getLinesUnsafe()
    {
        return this.lines;
    }

    /**
     * Adds a new line of text to the hologram
     *
     * @param text text to add
     *
     * @return new line data of the hologram
     */
    public TextLine appendTextLine(String text)
    {
        TextLine line = new TextLine(this, text);
        this.lines.add(line);
        return line;
    }

    /**
     * Adds a new icon line to the hologram
     *
     * @param itemStack item to display
     *
     * @return new line data of the hologram
     */
    public ItemLine appendItemLine(ItemStack itemStack)
    {
        ItemLine line = new ItemLine(this, itemStack);
        this.lines.add(line);
        return line;
    }

    /**
     * @return number of lines
     */
    public int size()
    {
        return this.lines.size();
    }

    /**
     * Spawns the custom entities for the hologram, making it visible
     */
    public void spawnEntities()
    {
        despawnEntities();

        double currentY = this.y;
        boolean first = true;

        for (HologramLine line : this.lines)
        {
            currentY -= line.getHeight();

            if (first)
                first = false;
            else
            {
                currentY -= 0.02;
            }

            line.spawn(this.world, this.x, currentY, this.z);
        }
    }

    /**
     * Removes the custom entities from the world, hiding it
     */
    public void despawnEntities()
    {
        for (HologramLine piece : this.lines)
            piece.despawn();
    }
}