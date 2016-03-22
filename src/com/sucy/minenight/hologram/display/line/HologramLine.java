package com.sucy.minenight.hologram.display.line;

import com.sucy.minenight.hologram.display.LineData;
import org.bukkit.World;

public abstract class HologramLine
{
    private final LineData parent;
    private final double   height;
    private       boolean  isSpawned;

    protected HologramLine(LineData hologram, double height)
    {
        this.parent = hologram;
        this.height = height;
    }

    public final LineData getParent()
    {
        return parent;
    }

    public final double getHeight()
    {
        return this.height;
    }

    public void spawn(World world, double x, double y, double z)
    {
        despawn();
        this.isSpawned = true;
    }

    public void despawn()
    {
        this.isSpawned = false;
    }

    public final boolean isSpawned()
    {
        return this.isSpawned;
    }

    public abstract int getEntityCount();

    public abstract int[] getIDs();

    public abstract void teleport(double paramDouble1, double paramDouble2, double paramDouble3);
}
