package com.sucy.minenight.nms.v1_9_R1;

import com.sucy.minenight.nms.NMSEntityBase;
import com.sucy.minenight.nms.NMSManager;
import com.sucy.minenight.hologram.display.LineData;
import com.sucy.minenight.hologram.display.line.HologramLine;
import com.sucy.minenight.hologram.display.line.ItemLine;
import com.sucy.minenight.hologram.display.line.TextLine;
import com.sucy.minenight.util.reflect.Reflection;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * NMS Manager implementation for 1.9
 */
public class NMSManager_19
    implements NMSManager
{
    private final HashMap<Integer, NMSEntityBase> entities = new HashMap<Integer, NMSEntityBase>();

    private Method validate;

    /**
     * Sets up reflection and entity registration
     */
    public void setup()
    {
        try
        {
            registerCustomEntity(NMSStand.class, "ArmorStand", 30);
            registerCustomEntity(NMSItem.class, "Item", 1);

            this.validate = World.class.getDeclaredMethod("b", Entity.class);
            this.validate.setAccessible(true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Registers one of the used custom entities
     *
     * @param entityClass custom entity's class
     * @param name        name of the entity
     * @param id          entity ID
     * @throws Exception
     */
    private void registerCustomEntity(Class entityClass, String name, int id) throws Exception
    {
        Reflection.putStaticMap(EntityTypes.class, "d", entityClass, name);
        Reflection.putStaticMap(EntityTypes.class, "f", entityClass, id);
    }

    /**
     * Retrieves a custom spawned entity by ID
     *
     * @param id entity ID
     * @return custom spawned entity
     */
    public NMSEntityBase getEntity(int id)
    {
        return entities.get(id);
    }

    /**
     * Removes the stored reference for the entity with the given ID
     */
    public void remove(int id)
    {
        entities.remove(id);
    }

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
    public NMSStand spawnNMSArmorStand(org.bukkit.World world, double x, double y, double z, HologramLine line)
    {
        WorldServer nmsWorld = ((CraftWorld) world).getHandle();
        NMSStand invisibleArmorStand = new NMSStand(nmsWorld, line);
        invisibleArmorStand.setPos(x, y, z);
        addEntityToWorld(nmsWorld, invisibleArmorStand);
        entities.put(invisibleArmorStand.getId(), invisibleArmorStand);
        return invisibleArmorStand;
    }

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
    public NMSItem spawnNMSItem(org.bukkit.World world, double x, double y, double z, ItemLine line, ItemStack item)
    {
        WorldServer nmsWorld = ((CraftWorld) world).getHandle();
        NMSItem entity = new NMSItem(nmsWorld, line);
        entity.setPos(x, y, z);
        entity.setItem(item);
        addEntityToWorld(nmsWorld, entity);
        entities.put(entity.id(), entity);
        return entity;
    }

    /**
     * Adds the entity to the world
     *
     * @param nmsWorld  world to add to
     * @param nmsEntity entity to add
     * @return true if succeeded
     */
    private boolean addEntityToWorld(WorldServer nmsWorld, Entity nmsEntity)
    {
        int chunkX = MathHelper.floor(nmsEntity.locX / 16.0D);
        int chunkZ = MathHelper.floor(nmsEntity.locZ / 16.0D);

        if (!nmsWorld.getChunkProviderServer().isChunkLoaded(chunkX, chunkZ))
        {
            nmsEntity.dead = true;
            return false;
        }

        nmsWorld.getChunkAt(chunkX, chunkZ).a(nmsEntity);
        nmsWorld.entityList.add(nmsEntity);
        try
        {
            this.validate.invoke(nmsWorld, nmsEntity);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Sends a destroy packet to the player for the hologram's entities
     *
     * @param player   player to send to
     * @param hologram hologram to send for
     */
    public void sendDestroyEntitiesPacket(Player player, LineData hologram)
    {
        int count = 0;
        for (HologramLine line : hologram.getLinesUnsafe())
            count += line.getEntityCount();

        int i = 0;
        int[] ids = new int[count];
        for (HologramLine line : hologram.getLinesUnsafe())
            if (line.isSpawned())
                for (int id : line.getIDs())
                    ids[i++] = id;

        if (count > 0)
        {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(ids);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    /**
     * Sends creation packets to the player for each of the hologram's entities
     *
     * @param player   player to send to
     * @param hologram hologram to send for
     */
    public void sendCreateEntitiesPacket(Player player, LineData hologram)
    {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        for (HologramLine line : hologram.getLinesUnsafe())
        {
            if (line.isSpawned())
            {
                if ((line instanceof TextLine))
                    connection.sendPacket(new PacketPlayOutSpawnEntityLiving((EntityLiving)((TextLine) line).getNameable()));
                else if ((line instanceof ItemLine))
                {
                    ItemLine itemLine = (ItemLine) line;

                    connection.sendPacket(new PacketPlayOutSpawnEntity((Entity) itemLine.getItem(), 2, 1));
                    connection.sendPacket(new PacketPlayOutSpawnEntityLiving((EntityLiving) itemLine.getVehicle()));

                    PacketPlayOutMount mountPacket = new PacketPlayOutMount();
                    Reflection.setValue(mountPacket, "a", itemLine.getVehicle().id());
                    Reflection.setValue(mountPacket, "b", new int[] { itemLine.getItem().id() });
                    connection.sendPacket(mountPacket);

                    connection.sendPacket(
                        new PacketPlayOutEntityMetadata(
                            itemLine.getItem().id(),
                            ((Entity) itemLine.getItem()).getDataWatcher(),
                            false
                        )
                    );
                }
            }
        }
    }
}