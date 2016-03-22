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

    public LineData(Location location)
    {
        updateLocation(location.getWorld(), location.getX(), location.getY(), location.getZ());

        visibility = new Visibility(this);
        this.lines = new ArrayList<HologramLine>();
    }

    public Visibility getVisibility()
    {
        return visibility;
    }

    public boolean isInChunk(Chunk chunk)
    {
        return (chunk.getX() == this.chunkX) && (chunk.getZ() == this.chunkZ);
    }

    public World getWorld()
    {
        return this.world;
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public double getZ()
    {
        return this.z;
    }

    public Location getLocation()
    {
        return new Location(this.world, this.x, this.y, this.z);
    }

    private void updateLocation(World world, double x, double y, double z)
    {

        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.chunkX = ((int) Math.floor(x) >> 4);
        this.chunkZ = ((int) Math.floor(z) >> 4);
    }

    public List<HologramLine> getLinesUnsafe()
    {
        return this.lines;
    }

    public TextLine appendTextLine(String text)
    {
        TextLine line = new TextLine(this, text);
        this.lines.add(line);
        refreshSingleLines();
        return line;
    }

    public ItemLine appendItemLine(ItemStack itemStack)
    {
        ItemLine line = new ItemLine(this, itemStack);
        this.lines.add(line);
        refreshSingleLines();
        return line;
    }

    public int size()
    {
        return this.lines.size();
    }

    public void refreshSingleLines()
    {
        if (this.world.isChunkLoaded(this.chunkX, this.chunkZ))
        {
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

                if (line.isSpawned())
                {
                    line.teleport(this.x, currentY, this.z);
                }
                else
                {
                    line.spawn(this.world, this.x, currentY, this.z);
                }
            }
        }
    }

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

    public void despawnEntities()
    {
        for (HologramLine piece : this.lines)
            piece.despawn();
    }

    public String toString()
    {
        return "CraftHologram [world=" + this.world + ", x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", lines=" + this.lines + "]";
    }
}