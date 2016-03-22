package com.sucy.minenight.nms;

import com.sucy.minenight.nms.v1_9_R1.NMSStand;
import com.sucy.minenight.nms.v1_9_R1.NMSItem;
import com.sucy.minenight.hologram.display.LineData;
import com.sucy.minenight.hologram.display.line.HologramLine;
import com.sucy.minenight.hologram.display.line.ItemLine;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles tapping into the NMS framework for setting up and using custom entities
 */
public abstract interface NMSManager
{
    /**
     * Sets up reflection and entity registration
     */
    public abstract void setup();

    /**
     * Retrieves a custom spawned entity by ID
     *
     * @param id entity ID
     * @return custom spawned entity
     */
    public abstract NMSEntityBase getEntity(int id);

    /**
     * Removes the stored reference for the entity with the given ID
     */
    public abstract void remove(int id);

    /**
     * Spawns a custom armor stand entity
     *
     * @param world world to spawn in
     * @param x     x coordinate
     * @param y     y coordinate
     * @param z     z coordinate
     * @param line  line owning the entity
     * @return created entity
     */
    public abstract NMSStand spawnNMSArmorStand(World world, double x, double y, double z, HologramLine line);

    /**
     * Spawns a custom item entity
     * @param world world to spawn in
     * @param x     x coordinate
     * @param y     y coordinate
     * @param z     z coordinate
     * @param line  line owning the entity
     * @param item  item to base it off of
     * @return created entity
     */
    public abstract NMSItem spawnNMSItem(World world, double x, double y, double z, ItemLine line, ItemStack item);

    /**
     * Sends a destroy packet to the player for the hologram's entities
     *
     * @param player   player to send to
     * @param hologram hologram to send for
     */
    public abstract void sendDestroyEntitiesPacket(Player player, LineData hologram);

    /**
     * Sends creation packets to the player for each of the hologram's entities
     *
     * @param player   player to send to
     * @param hologram hologram to send for
     */
    public abstract void sendCreateEntitiesPacket(Player player, LineData hologram);
}
