package com.sucy.minenight.hologram.display;

import com.sucy.minenight.nms.NMS;
import com.sucy.minenight.util.version.VersionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class Visibility
{
    private final LineData        hologram;
    private       HashSet<String> playersVisibilityMap;
    private       boolean         visibleByDefault;
    private static final int VISIBILITY_DISTANCE_SQUARED = 4096;

    public Visibility(LineData hologram)
    {
        this.hologram = hologram;
        this.visibleByDefault = true;
    }

    public boolean isVisibleByDefault()
    {
        return this.visibleByDefault;
    }

    public void setVisibleByDefault(boolean visibleByDefault)
    {
        if (this.visibleByDefault != visibleByDefault)
        {
            boolean oldVisibleByDefault = this.visibleByDefault;
            this.visibleByDefault = visibleByDefault;

            for (Player player : VersionManager.getOnlinePlayers())
            {
                if ((this.playersVisibilityMap == null) || (!this.playersVisibilityMap.contains(player.getName().toLowerCase())))
                {
                    if (oldVisibleByDefault)
                    {
                        sendDestroyPacketIfNear(player, this.hologram);
                    }
                    else
                        sendCreatePacketIfNear(player, this.hologram);
                }
            }
        }
    }

    public void showTo(Player player)
    {


        boolean wasVisible = isVisibleTo(player);

        if (this.playersVisibilityMap == null)
        {
            this.playersVisibilityMap = new HashSet<String>();
        }

        this.playersVisibilityMap.add(player.getName().toLowerCase());

        if (!wasVisible)
            sendCreatePacketIfNear(player, this.hologram);
    }

    public void hideTo(Player player)
    {


        boolean wasVisible = isVisibleTo(player);

        if (this.playersVisibilityMap == null)
        {
            this.playersVisibilityMap = new HashSet<String>();
        }

        this.playersVisibilityMap.add(player.getName().toLowerCase());

        if (wasVisible)
            sendDestroyPacketIfNear(player, this.hologram);
    }

    public boolean isVisibleTo(Player player)
    {
        if (this.playersVisibilityMap != null)
        {
            return this.playersVisibilityMap.contains(player.getName().toLowerCase());
        }

        return this.visibleByDefault;
    }

    public void resetVisibility(Player player)
    {


        if (this.playersVisibilityMap == null)
        {
            return;
        }

        boolean wasVisible = isVisibleTo(player);

        this.playersVisibilityMap.remove(player.getName().toLowerCase());

        if ((this.visibleByDefault) && (!wasVisible))
        {
            sendCreatePacketIfNear(player, this.hologram);
        }
        else if ((!this.visibleByDefault) && (wasVisible))
            sendDestroyPacketIfNear(player, this.hologram);
    }

    public void resetVisibilityAll()
    {
        if (this.playersVisibilityMap != null)
        {
            for (String playerName : playersVisibilityMap)
            {
                Player onlinePlayer = Bukkit.getPlayerExact(playerName);
                if (onlinePlayer != null)
                {
                    resetVisibility(onlinePlayer);
                }
            }

            this.playersVisibilityMap.clear();
            this.playersVisibilityMap = null;
        }
    }

    private static void sendCreatePacketIfNear(Player player, LineData hologram)
    {
        if (isNear(player, hologram))
        {
            NMS.getManager().sendCreateEntitiesPacket(player, hologram);
        }
    }

    private static void sendDestroyPacketIfNear(Player player, LineData hologram)
    {
        if (isNear(player, hologram))
            NMS.getManager().sendDestroyEntitiesPacket(player, hologram);
    }

    private static boolean isNear(Player player, LineData hologram)
    {
        return (player.isOnline()) && (player.getWorld().equals(hologram.getWorld())) && (player.getLocation().distanceSquared(hologram.getLocation()) < VISIBILITY_DISTANCE_SQUARED);
    }

    public String toString()
    {
        return "CraftVisibilityManager [playersMap=" + this.playersVisibilityMap + ", visibleByDefault=" + this.visibleByDefault + "]";
    }
}
