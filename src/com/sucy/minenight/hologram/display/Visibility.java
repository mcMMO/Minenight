package com.sucy.minenight.hologram.display;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

/**
 * Controls who can see a hologram
 */
public class Visibility
{
    private HashSet<UUID> players;
    private boolean       visibleToAll;
    private boolean       blacklist;

    /**
     * Defaults visibility to all players
     */
    public Visibility()
    {
        this.visibleToAll = true;
    }

    /**
     * Adds the player to a whitelist for those who can
     * see the hologram. Once one player is added, only
     * those on the whitelist can see it at all.
     *
     * @param player player to show to
     */
    public void showTo(Player player)
    {
        blacklist = visibleToAll = false;

        if (this.players == null)
            players = new HashSet<UUID>();

        this.players.add(player.getUniqueId());
    }

    /**
     * Adds the player to a blacklist for those who can
     * see the hologram. Once one player is added, only
     * those not on the blacklist can see it at all.
     *
     * @param player player to hide from
     */
    public void exclude(Player player)
    {
        blacklist = true;
        visibleToAll = false;

        if (this.players == null)
            players = new HashSet<UUID>();

        this.players.add(player.getUniqueId());
    }

    /**
     * Checks whether or not the player should
     * be able to see the hologram
     *
     * @param player player to check
     *
     * @return true if allowed, false otherwise
     */
    public boolean isVisibleTo(Player player)
    {
        return visibleToAll || (players.contains(player.getUniqueId()) != blacklist);
    }
}
