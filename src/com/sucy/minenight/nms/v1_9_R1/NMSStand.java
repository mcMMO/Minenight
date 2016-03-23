package com.sucy.minenight.nms.v1_9_R1;

import com.sucy.minenight.hologram.display.line.HologramLine;
import com.sucy.minenight.nms.NMS;
import com.sucy.minenight.nms.NMSEntityBase;
import com.sucy.minenight.nms.NMSNameable;
import com.sucy.minenight.util.MathFunc;
import com.sucy.minenight.util.reflect.Reflection;
import net.minecraft.server.v1_9_R1.*;

/**
 * A simplified armor stand entity for hologram usage
 */
public class NMSStand extends EntityArmorStand
    implements NMSEntityBase, NMSNameable
{
    private HologramLine parentPiece;

    /**
     * Makes a new stand entity
     *
     * @param world       world the stand is in
     * @param parentPiece hologram line owning the entity
     */
    public NMSStand(World world, HologramLine parentPiece)
    {
        super(world);
        super.setInvisible(true);
        super.setSmall(true);
        super.setArms(false);
        super.setGravity(true);
        super.setBasePlate(true);
        super.setMarker(true);
        this.parentPiece = parentPiece;
        Reflection.setValue(net.minecraft.server.v1_9_R1.EntityArmorStand.class, this, "bz", Integer.MAX_VALUE);
        super.a(new Boundless());
    }

    // Interface implementations

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
        if ((name != null) && (name.length() > 300))
        {
            name = name.substring(0, 300);
        }
        super.setCustomName(name);
        super.setCustomNameVisible((name != null) && (!name.isEmpty()));
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
     * Moves the stand elsewhere and updates the position to players
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     */
    public void setPos(double x, double y, double z)
    {
        super.setPosition(x, y, z);

        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(this);

        for (EntityHuman p : this.world.players)
        {
            if (p instanceof EntityPlayer && MathFunc.dSq(p.locX, p.locZ, this.locX, this.locZ) < 8192)
            {
                EntityPlayer player = (EntityPlayer) p;
                if (player.playerConnection != null)
                    player.playerConnection.sendPacket(teleportPacket);
            }
        }
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

    public void m() { }

    public void die() { }

    public void setCustomName(String customName) { }

    public void setCustomNameVisible(boolean visible) { }

    public void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack) { }
}
