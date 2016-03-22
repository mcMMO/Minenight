package com.sucy.minenight.hologram.display.line;

import com.sucy.minenight.hologram.display.LineData;
import com.sucy.minenight.nms.NMS;
import com.sucy.minenight.nms.NMSNameable;
import org.bukkit.World;

public class TextLine extends HologramLine
{
    private static final double offset = -0.29;

    private String        text;
    private NMSNameable   nmsNameble;

    public TextLine(LineData hologram, String text)
    {
        super(hologram, 0.23D);
        setText(text);
    }

    public String getText()
    {
        return this.text;
    }

    public void setText(String text)
    {
        this.text = text;
        if (this.nmsNameble != null)
            this.nmsNameble.rename(text);
    }

    public void spawn(World world, double x, double y, double z)
    {
        super.spawn(world, x, y, z);

        this.nmsNameble = NMS.getManager().spawnNMSArmorStand(world, x, y - offset, z, this);
        this.nmsNameble.rename(this.text);
    }

    public void despawn()
    {
        super.despawn();

        if (this.nmsNameble != null)
        {
            this.nmsNameble.despawn();
            this.nmsNameble = null;
        }
    }

    public void teleport(double x, double y, double z)
    {
        if (this.nmsNameble != null)
            this.nmsNameble.setPos(x, y - offset, z);
    }

    public int getEntityCount()
    {
        return isSpawned() ? 1 : 0;
    }

    public int[] getIDs()
    {
        return isSpawned() ? new int[] { this.nmsNameble.id() } : new int[0];
    }

    public NMSNameable getNameable()
    {
        return this.nmsNameble;
    }
}
