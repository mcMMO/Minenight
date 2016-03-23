package com.sucy.minenight.nms;

import com.sucy.minenight.hologram.display.line.HologramLine;

/**
 * Base interface for all custom entities
 */
public abstract interface NMSEntityBase
{
    /**
     * @return hologram line owning this entity
     */
    public abstract HologramLine getLine();

    /**
     * Sets the position of the item but updates aren't
     * necessary as it's always riding a stand
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     */
    public abstract void setPos(double x, double y, double z);

    /**
     * @return true if removed, false otherwise
     */
    public abstract boolean isDespawned();

    /**
     * Removes the entity, making sure the NMS Manager knows about it
     */
    public abstract void despawn();

    /**
     * @return entity ID
     */
    public abstract int id();
}
