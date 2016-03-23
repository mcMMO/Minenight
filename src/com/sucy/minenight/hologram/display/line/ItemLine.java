package com.sucy.minenight.hologram.display.line;

import com.sucy.minenight.hologram.display.LineData;
import com.sucy.minenight.nms.NMS;
import com.sucy.minenight.nms.NMSEntityBase;
import com.sucy.minenight.nms.NMSIcon;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 * A line for a hologram that displays an item icon
 */
public class ItemLine extends HologramLine
{
    private ItemStack     itemStack;
    private NMSIcon       icon;
    private NMSEntityBase vehicle;

    /**
     * @param hologram  owning hologram reference
     * @param itemStack item to display
     */
    public ItemLine(LineData hologram, ItemStack itemStack)
    {
        super(hologram, 0.7D);
        setItemStack(itemStack);
    }

    /**
     * @param itemStack new item to display
     */
    public void setItemStack(ItemStack itemStack)
    {
        this.itemStack = itemStack;

        if (this.icon != null)
            this.icon.setItem(itemStack);
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

        if ((this.itemStack != null) && (this.itemStack.getType() != Material.AIR))
        {
            this.icon = NMS.getManager().spawnNMSItem(world, x, y + 0.6, z, this, this.itemStack);
            this.vehicle = NMS.getManager().spawnNMSArmorStand(world, x, y + 0.6, z, this);
            this.icon.ride(this.vehicle);
        }
    }

    /**
     * Despawns the hologram from the world
     */
    public void despawn()
    {
        super.despawn();

        if (this.vehicle != null)
        {
            this.vehicle.despawn();
            this.vehicle = null;
        }

        if (this.icon != null)
        {
            this.icon.despawn();
            this.icon = null;
        }
    }

    /**
     * @return number of entities used for the line
     */
    public int getEntityCount()
    {
        return isSpawned() ? 2 : 0;
    }

    /**
     * @return IDs of each entity used by the line
     */
    public int[] getIDs()
    {
        return isSpawned() ? new int[] { this.vehicle.id(), this.icon.id() } : new int[0];
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
        if (this.vehicle != null)
        {
            this.vehicle.setPos(x, y, z);
        }

        if (this.icon != null)
            this.icon.setPos(x, y, z);
    }

    /**
     * @return NMS entity used for the icon itself
     */
    public NMSEntityBase getItem()
    {
        return this.icon;
    }

    /**
     * @return NMS entity used to keep the icon in place
     */
    public NMSEntityBase getVehicle()
    {
        return this.vehicle;
    }
}
