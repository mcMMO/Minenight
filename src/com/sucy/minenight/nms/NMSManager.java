package com.sucy.minenight.nms;

import com.sucy.minenight.hologram.display.LineData;
import com.sucy.minenight.hologram.display.line.HologramLine;
import com.sucy.minenight.hologram.display.line.ItemLine;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles tapping into the NMS framework for setting up and using custom entities
 */
public interface NMSManager
{
    /**
     * Stops chunks from generating in a given world
     *
     * @param world world to stop chunks from generating in
     */
    public void stopChunks(World world);

    /**
     * Retrieves a custom spawned entity by ID
     *
     * @param id entity ID
     *
     * @return custom spawned entity
     */
    public NMSEntityBase getEntity(int id);

    /**
     * Removes the stored reference for the entity with the given ID
     */
    public void remove(int id);

    /**
     * Spawns a custom armor stand entity
     *
     * @param world world to spawn in
     * @param x     x coordinate
     * @param y     y coordinate
     * @param z     z coordinate
     * @param line  line owning the entity
     *
     * @return created entity
     */
    public NMSNameable spawnNMSArmorStand(World world, double x, double y, double z, HologramLine line);

    /**
     * Spawns a custom item entity
     *
     * @param world world to spawn in
     * @param x     x coordinate
     * @param y     y coordinate
     * @param z     z coordinate
     * @param line  line owning the entity
     * @param item  item to base it off of
     *
     * @return created entity
     */
    public NMSIcon spawnNMSItem(World world, double x, double y, double z, ItemLine line, ItemStack item);

    /**
     * Sends a destroy packet to the player for the hologram's entities
     *
     * @param player   player to send to
     * @param hologram hologram to send for
     */
    public void sendDestroyEntitiesPacket(Player player, LineData hologram);

    /**
     * Sends creation packets to the player for each of the hologram's entities
     *
     * @param player   player to send to
     * @param hologram hologram to send for
     */
    public void sendCreateEntitiesPacket(Player player, LineData hologram);
}
