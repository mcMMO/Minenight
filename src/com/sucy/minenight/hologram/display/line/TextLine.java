package com.sucy.minenight.hologram.display.line;

import com.sucy.minenight.hologram.display.LineData;
import com.sucy.minenight.nms.NMS;
import com.sucy.minenight.nms.NMSNameable;
import org.bukkit.World;

/**
 * A hologram line that displays a bit of text
 */
public class TextLine extends HologramLine
{
    private static final double offset = -0.29;

    private String      text;
    private NMSNameable nmsNameble;

    /**
     * @param hologram owning hologram reference
     * @param text     text to display
     */
    public TextLine(LineData hologram, String text)
    {
        super(hologram, 0.23D);
        setText(text);
    }

    /**
     * @return displayed text
     */
    public String getText()
    {
        return this.text;
    }

    /**
     * @param text new text to display
     */
    public void setText(String text)
    {
        this.text = text;
        if (this.nmsNameble != null)
            this.nmsNameble.rename(text);
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
        super.spawn(world, x, y, z);

        this.nmsNameble = NMS.getManager().spawnNMSArmorStand(world, x, y - offset, z, this);
        this.nmsNameble.rename(this.text);
    }

    /**
     * Despawns the hologram from the world
     */
    public void despawn()
    {
        super.despawn();

        if (this.nmsNameble != null)
        {
            this.nmsNameble.despawn();
            this.nmsNameble = null;
        }
    }

    /**
     * @return number of entities used for the line
     */
    public int getEntityCount()
    {
        return isSpawned() ? 1 : 0;
    }

    /**
     * @return IDs of each entity used by the line
     */
    public int[] getIDs()
    {
        return isSpawned() ? new int[] { this.nmsNameble.id() } : new int[0];
    }

    /**
     * @return NMS entity used to display the text
     */
    public NMSNameable getNameable()
    {
        return this.nmsNameble;
    }

    /**
     * Moves the hologram to another location
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     */
    public void teleport(double x, double y, double z)
    {
        if (this.nmsNameble != null)
            this.nmsNameble.setPos(x, y - offset, z);
    }
}
