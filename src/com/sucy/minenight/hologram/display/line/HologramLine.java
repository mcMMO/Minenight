package com.sucy.minenight.hologram.display.line;

import com.sucy.minenight.hologram.display.LineData;
import org.bukkit.World;

/**
 * Base class for individual lines in holograms
 */
public abstract class HologramLine
{
    private final LineData parent;
    private final double   height;
    private       boolean  isSpawned;

    /**
     * @param hologram hologram line data reference
     * @param height   size of the line
     */
    protected HologramLine(LineData hologram, double height)
    {
        this.parent = hologram;
        this.height = height;
    }

    /**
     * @return owning hologram's line data
     */
    public final LineData getParent()
    {
        return parent;
    }

    /**
     * @return height of the hologram
     */
    public final double getHeight()
    {
        return this.height;
    }

    /**
     * Spawns the hologram at the coordinates
     *
     * @param world world to spawn in
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     */
    public void spawn(World world, double x, double y, double z)
    {
        despawn();
        this.isSpawned = true;
    }

    /**
     * Despawns the hologram from the world
     */
    public void despawn()
    {
        this.isSpawned = false;
    }

    /**
     * @return whether or not the hologram line has been spawned
     */
    public final boolean isSpawned()
    {
        return this.isSpawned;
    }

    /**
     * @return number of entities used for the line
     */
    public abstract int getEntityCount();

    /**
     * @return IDs of each entity used by the line
     */
    public abstract int[] getIDs();

    /**
     * Moves the hologram to another location
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     */
    public abstract void teleport(double x, double y, double z);
}
