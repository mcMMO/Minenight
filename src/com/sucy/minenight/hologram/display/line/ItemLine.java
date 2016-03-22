package com.sucy.minenight.hologram.display.line;

import com.sucy.minenight.hologram.display.LineData;
import com.sucy.minenight.nms.NMS;
import com.sucy.minenight.nms.NMSEntityBase;
import com.sucy.minenight.nms.NMSIcon;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class ItemLine extends HologramLine
{
    private ItemStack     itemStack;
    private NMSIcon       nmsItem;
    private NMSEntityBase nmsVehicle;

    public ItemLine(LineData hologram, ItemStack itemStack)
    {
        super(hologram, 0.7D);
        setItemStack(itemStack);
    }

    public void setItemStack(ItemStack itemStack)
    {
        this.itemStack = itemStack;

        if (this.nmsItem != null)
            this.nmsItem.setItem(itemStack);
    }

    public void spawn(World world, double x, double y, double z)
    {
        super.spawn(world, x, y, z);

        if ((this.itemStack != null) && (this.itemStack.getType() != Material.AIR))
        {
            this.nmsItem = NMS.getManager().spawnNMSItem(world, x, y, z, this, this.itemStack);

            this.nmsVehicle = NMS.getManager().spawnNMSArmorStand(world, x, y, z, this);

            this.nmsItem.ride(this.nmsVehicle);
        }
    }

    public void despawn()
    {
        super.despawn();

        if (this.nmsVehicle != null)
        {
            this.nmsVehicle.despawn();
            this.nmsVehicle = null;
        }

        if (this.nmsItem != null)
        {
            this.nmsItem.despawn();
            this.nmsItem = null;
        }
    }

    public void teleport(double x, double y, double z)
    {
        if (this.nmsVehicle != null)
        {
            this.nmsVehicle.setPos(x, y, z);
        }

        if (this.nmsItem != null)
            this.nmsItem.setPos(x, y, z);
    }

    public int getEntityCount()
    {
        return isSpawned() ? 2 : 0;
    }

    public int[] getIDs()
    {
        return isSpawned() ? new int[] { this.nmsVehicle.id(), this.nmsItem.id() } : new int[0];
    }

    public NMSEntityBase getItem()
    {
        return this.nmsItem;
    }

    public NMSEntityBase getVehicle()
    {
        return this.nmsVehicle;
    }
}
