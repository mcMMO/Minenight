package com.sucy.minenight.nms.v1_9_R1;

import com.sucy.minenight.Minenight;
import com.sucy.minenight.hologram.display.LineData;
import com.sucy.minenight.hologram.display.line.HologramLine;
import com.sucy.minenight.hologram.display.line.ItemLine;
import com.sucy.minenight.hologram.display.line.TextLine;
import com.sucy.minenight.log.Logger;
import com.sucy.minenight.nms.NMSEntityBase;
import com.sucy.minenight.nms.NMSManager;
import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.util.reflect.Reflection;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

/**
 * NMS Manager implementation for 1.9
 */
public class NMSManager_19
    implements NMSManager
{
    private final HashMap<Integer, NMSEntityBase> entities = new HashMap<Integer, NMSEntityBase>();

    private Method validate;

    private Field chunkProvider;
    private Field chunkLoader;
    private Field modified;

    /**
     * Sets up reflection and entity registration
     */
    public NMSManager_19()
        throws Exception
    {
        registerCustomEntity(NMSStand.class, "ArmorStand", 30);
        registerCustomEntity(NMSItem.class, "Item", 1);

        validate = World.class.getDeclaredMethod("b", Entity.class);
        validate.setAccessible(true);

        chunkProvider = World.class.getDeclaredField("chunkProvider");
        chunkProvider.setAccessible(true);

        chunkLoader = ChunkProviderServer.class.getDeclaredField("chunkLoader");
        chunkLoader.setAccessible(true);

        modified = Chunk.class.getDeclaredField("r");
        modified.setAccessible(true);
    }

    /**
     * Overrides potion effects
     */
    public void overrideVanilla()
    {
        try
        {
            DataSection data = Minenight.getConfigData("stats", true, true);
            VanillaManipulator.updatePotions();
            VanillaManipulator.updateStackSize(data.getSection("items"));
            VanillaManipulator.updateStrengths(data.getSection("blocks"));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the stats file for a given player using their ID
     *
     * @param playerId player UUID
     * @return stats file
     */
    public File getStatsFile(UUID playerId)
    {
        File dataDirectory = ((CraftWorld)Bukkit.getWorlds().get(0)).getHandle().getDataManager().getDirectory();
        return new File(dataDirectory, "stats" + File.separator + playerId.toString() + ".json");
    }

    /**
     * Stops chunks from generating in a given world
     *
     * @param world world to stop chunks from generating in
     */
    public void stopChunks(org.bukkit.World world)
    {
        try
        {
            WorldServer nmsWorld = ((CraftWorld) world).getHandle();
            ChunkProviderServer provider = nmsWorld.getChunkProviderServer();

            NoChunkProvider wrapper = new NoChunkProvider(nmsWorld, (IChunkLoader) chunkLoader.get(provider), provider.chunkGenerator);
            chunkProvider.set(nmsWorld, wrapper);

            Logger.debug("Disabled chunks for " + world.getName());
        }
        catch (Exception ex)
        {
            Logger.bug("Failed to set up chunk gen override - " + ex.getMessage());
        }
    }

    /**
     * Makes a light at a location
     *
     * @param loc   location to make the light at
     * @param level light level
     */
    public void makeLight(Location loc, int level)
    {
        try
        {
            WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
            BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            world.a(EnumSkyBlock.BLOCK, pos, level);
            chunks(world, pos);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Deletes a light at a location
     *
     * @param loc location to delete the light from
     */
    public void deleteLight(Location loc)
    {
        try
        {
            WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
            BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            world.c(EnumSkyBlock.BLOCK, pos);
            chunks(world, pos);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Updates a light at a location
     *
     * @param from  old location
     * @param to    new location
     * @param level light level
     */
    public void updateLight(Location from, Location to, int level)
    {
        try
        {
            WorldServer world = ((CraftWorld) from.getWorld()).getHandle();
            BlockPosition pos = new BlockPosition(from.getBlockX(), from.getBlockY(), from.getBlockZ());
            world.c(EnumSkyBlock.BLOCK, pos);
            pos = new BlockPosition(to.getBlockX(), to.getBlockY(), to.getBlockZ());
            world.a(EnumSkyBlock.BLOCK, pos, level);
            chunks(world, pos);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Updates chunk lighting
     *
     * @param world world containing the chunks
     * @param pos   position of the light to base it around
     * @throws Exception
     */
    private void chunks(WorldServer world, BlockPosition pos)
        throws Exception
    {
        for (int dX = -1; dX <= 1; dX++)
        {
            for (int dZ = -1; dZ <= 1; dZ++)
            {
                Chunk chunk = world.getChunkIfLoaded(pos.getX() + dX, pos.getZ() + dZ);
                if (chunk != null && modified.getBoolean(chunk))
                    chunk.f(false);
            }
        }
    }

    /**
     * Registers one of the used custom entities
     *
     * @param entityClass custom entity's class
     * @param name        name of the entity
     * @param id          entity ID
     *
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
     *
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
     *
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
     *
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
                    connection.sendPacket(new PacketPlayOutSpawnEntityLiving((EntityLiving) ((TextLine) line).getNameable()));
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
