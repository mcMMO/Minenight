package com.sucy.minenight.nms;

/**
 * Interface for custom text entities
 */
public abstract interface NMSNameable extends NMSEntityBase
{
    /**
     * Sets the display name of the entity
     *
     * @param name new name to use
     */
    public abstract void rename(String name);
}
