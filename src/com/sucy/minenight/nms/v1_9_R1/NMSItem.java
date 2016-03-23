package com.sucy.minenight.nms.v1_9_R1;

import com.sucy.minenight.hologram.display.line.HologramLine;
import com.sucy.minenight.hologram.display.line.ItemLine;
import com.sucy.minenight.nms.NMS;
import com.sucy.minenight.nms.NMSEntityBase;
import com.sucy.minenight.nms.NMSIcon;
import com.sucy.minenight.util.MathFunc;
import com.sucy.minenight.util.reflect.Reflection;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * A simplified item icon entity for use with holograms
 */
public class NMSItem extends EntityItem
    implements NMSIcon
{
    private ItemLine parentPiece;
    private int      resendMountPacketTicks;

    /**
     * Makes an item entity in the world
     * for the line of a hologram
     *
     * @param world world the item is in
     * @param piece line of a hologram owning this entity
     */
    public NMSItem(World world, ItemLine piece)
    {
        super(world);
        this.pickupDelay = 2147483647;
        this.parentPiece = piece;
    }

    // Interface implementations

    /**
     * Sets the ItemStack for the EntityItem
     * from a Bukkit item reference
     *
     * @param stack Bukkit item
     */
    public void setItem(org.bukkit.inventory.ItemStack stack)
    {
        ItemMeta meta = stack.getItemMeta();
        ArrayList<String> lore = new ArrayList<String>();
        lore.add("" + Math.random());
        meta.setLore(lore);
        stack.setItemMeta(meta);

        ItemStack newItem = CraftItemStack.asNMSCopy(stack);
        if (newItem == null)
            newItem = new ItemStack(Blocks.PUMPKIN);

        newItem.count = 0;
        setItemStack(newItem);
    }

    /**
     * @return true if removed, false otherwise
     */
    public boolean isDespawned()
    {
        return this.dead;
    }

    /**
     * @return entity ID
     */
    public int id()
    {
        return super.getId();
    }

    /**
     * @return hologram line owning this entity
     */
    public HologramLine getLine()
    {
        return this.parentPiece;
    }

    /**
     * Sets the display name of the entity
     *
     * @param name new name to use
     */
    public void rename(String name)
    {
        if (name == null)
        {
            setCustomNameVisible(false);
            return;
        }
        if (name.length() > 300)
        {
            name = name.substring(0, 300);
        }
        super.setCustomName(name);
        super.setCustomNameVisible(!name.isEmpty());
    }

    /**
     * Removes the entity, making sure the NMS Manager knows about it
     */
    public void despawn()
    {
        NMS.getManager().remove(getId());
        this.dead = true;
    }

    /**
     * Sets the position of the item but updates aren't
     * necessary as it's always riding a stand
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     */
    public void setPos(double x, double y, double z)
    {
        super.setPosition(x, y, z);
    }

    /**
     * Prevents the entity despawning when not desired
     *
     * @return entity ID or -1 if for a packet
     */
    public int getId()
    {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if ((elements.length > 2) && (elements[2] != null) && (elements[2].getFileName().equals("EntityTrackerEntry.java")) && (elements[2].getLineNumber() > 142) && (elements[2].getLineNumber() < 152))
        {
            return -1;
        }

        return super.getId();
    }

    /**
     * Makes the item ride the given vehicle
     * in order to keep it in place
     *
     * @param vehicleBase vehicle to ride in
     */
    public void ride(NMSEntityBase vehicleBase)
    {
        if (vehicleBase == null || !(vehicleBase instanceof Entity))
        {
            return;
        }

        net.minecraft.server.v1_9_R1.Entity entity = (net.minecraft.server.v1_9_R1.Entity) vehicleBase;
        if (super.by() != null)
        {
            net.minecraft.server.v1_9_R1.Entity oldVehicle = super.by();
            Reflection.setValue(net.minecraft.server.v1_9_R1.Entity.class, this, "as", null);
            oldVehicle.passengers.remove(this);
        }

        Reflection.setValue(net.minecraft.server.v1_9_R1.Entity.class, this, "as", entity);
        entity.passengers.clear();
        entity.passengers.add(this);
    }

    // Do some quick fixes

    /**
     * Makes sure the item doesn't expire and occasionally
     * refreshes the riding state of it in case the client
     * thinks it is no longer attached for some reason.
     */
    public void m()
    {
        this.ticksLived = 0;

        if (by() != null && this.resendMountPacketTicks++ > 100)
        {
            this.resendMountPacketTicks = 0;
            PacketPlayOutMount mountPacket = new PacketPlayOutMount(by());

            for (EntityHuman p : this.world.players)
            {
                if ((p instanceof EntityPlayer) && MathFunc.dSq(p.locX, p.locZ, this.locX, this.locZ) < 1024)
                {
                    EntityPlayer player = (EntityPlayer) p;
                    if (player.playerConnection != null)
                        player.playerConnection.sendPacket(mountPacket);
                }
            }
        }
    }

    /**
     * Prevent entities from picking the item up
     *
     * @return the item stack or null if invalid case
     */
    public ItemStack getItemStack()
    {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        if ((stacktrace.length > 2) && (stacktrace[2].getClassName().contains("EntityInsentient")))
        {
            return null;
        }

        return super.getItemStack();
    }

    // Remove functionality so it takes up less resources

    public EnumInteractionResult a(EntityHuman human, Vec3D vec3d, ItemStack itemstack, EnumHand enumhand) { return EnumInteractionResult.PASS; }

    public boolean c(NBTTagCompound nbttagcompound) { return false; }

    public boolean c(int i, ItemStack item) { return false; }

    public boolean d(NBTTagCompound nbttagcompound) { return false; }

    public boolean isInvulnerable(DamageSource source) { return true; }

    public boolean isCollidable() { return false; }

    public void a(AxisAlignedBB boundingBox) { }

    public void a(SoundEffect soundeffect, float f, float f1) { }

    public void a(NBTTagCompound nbttagcompound) { }

    public void b(NBTTagCompound nbttagcompound) { }

    public void e(NBTTagCompound nbttagcompound) { }

    public void f(NBTTagCompound nbttagcompound) { }

    public void d(EntityHuman human) { }

    public void die() { }

    public void inactiveTick() { }

    public void setCustomName(String customName) { }

    public void setCustomNameVisible(boolean visible) { }

    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) { }
}
