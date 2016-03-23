/**
 * MineNight
 * com.sucy.minenight.hologram.listener.PlayerListener
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.minenight.hologram.listener;

import com.sucy.minenight.Minenight;
import com.sucy.minenight.hologram.Holograms;
import com.sucy.minenight.hologram.data.InstanceSettings;
import com.sucy.minenight.hologram.data.ScoreboardSettings;
import com.sucy.minenight.hologram.display.Hologram;
import com.sucy.minenight.nms.PacketInjector;
import com.sucy.minenight.util.ListenerUtil;
import com.sucy.minenight.util.text.GlobalFilter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.UUID;

/**
 * Handles events related to players and holograms
 */
public class HologramListener implements Listener
{
    private static HashMap<UUID, LivingEntity> killers = new HashMap<UUID, LivingEntity>();

    private Holograms  holograms;
    private Scoreboard scoreboard;

    private PacketInjector injector = new PacketInjector();

    /**
     * @param holograms reference to manager
     */
    public HologramListener(Holograms holograms)
    {
        this.holograms = holograms;

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        ScoreboardSettings settings = holograms.getScoreboardSettings();

        Team team = scoreboard.registerNewTeam("below");
        team.setAllowFriendlyFire(true);
        team.setCanSeeFriendlyInvisibles(false);
        team.setPrefix(settings.below);
        team = scoreboard.registerNewTeam("above");
        team.setAllowFriendlyFire(true);
        team.setCanSeeFriendlyInvisibles(false);
        team.setPrefix(settings.above);

        Objective obj = scoreboard.registerNewObjective("below", "dummy");
        obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
        obj.setDisplayName(settings.subText);
    }

    /**
     * Add a packet injector to the player when they join
     * in order to handle hologram visibility. Also sets
     * up scoreboards for identity setup.
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event)
    {
        injector.addPlayer(event.getPlayer());

        event.getPlayer().setScoreboard(scoreboard);
        updateScoreboard(event.getPlayer());

        GlobalFilter.define(event.getPlayer(), "player", event.getPlayer().getName());
    }

    /**
     * Cleans up when a player disconnects
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event)
    {
        injector.removePlayer(event.getPlayer());

        GlobalFilter.clear(event.getPlayer());
    }

    /**
     * Instance holograms for player deaths
     *
     * @param event event details
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        LivingEntity killer = killers.remove(player.getUniqueId());
        GlobalFilter.define(event.getEntity(), "skull:player", "{skull:" + player.getName() + "}");
        Location loc = player.getLocation().add(0, 3, 0);
        if (killer instanceof Player)
        {
            InstanceSettings settings = holograms.getInstanceSettings("death.player");
            GlobalFilter.define(player, "skull:killer", "{skull:" + killer.getName() + "}");
            GlobalFilter.define(player, "killer", killer.getName());
            Hologram deathHolo = new Hologram(loc, settings.getFormat(player), settings.ticks);
            holograms.addInstance(deathHolo);
        }
        else if (killer != null)
        {
            InstanceSettings settings = holograms.getInstanceSettings("death.entity");
            GlobalFilter.define(player, "skull:player", "{skull:" + player.getName() + "}");
            GlobalFilter.define(player, "entity", killer.getName());
            Hologram deathHolo = new Hologram(loc, settings.getFormat(player), settings.ticks);
            holograms.addInstance(deathHolo);
        }
    }

    /**
     * Apply damaged effects
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamaged(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player player = (Player) event.getEntity();
            new UpdateTask(player);
            killers.put(player.getUniqueId(), ListenerUtil.getDamager(event));
        }

        if (event.getEntity() instanceof LivingEntity)
            instanceValue("hurt", (LivingEntity) event.getEntity(), event.getDamage());
    }

    /**
     * Update scoreboard health after
     *
     * @param event event details
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRegen(EntityRegainHealthEvent event)
    {
        if (event.getEntity() instanceof Player)
        {
            Player player = (Player) event.getEntity();
            new UpdateTask(player);
        }

        if (event.getEntity() instanceof LivingEntity)
            instanceValue("heal", (LivingEntity) event.getEntity(), event.getAmount());
    }

    /**
     * Updates scoreboard details for the given player
     *
     * @param player player to update for
     */
    private void updateScoreboard(Player player)
    {
        ScoreboardSettings settings = holograms.getScoreboardSettings();
        double ratio = player.getHealth() / player.getMaxHealth();
        scoreboard.getObjective(DisplaySlot.BELOW_NAME)
            .getScore(player.getName())
            .setScore((int) (settings.proportion * ratio));
        scoreboard.getTeam(ratio > settings.percent ? "above" : "below")
            .addEntry(player.getName());
    }

    /**
     * Instances a hologram for a valued event
     *
     * @param base   base key for the hologram type
     * @param entity entity involved
     * @param amount number associated with the event
     */
    private void instanceValue(String base, LivingEntity entity, double amount)
    {
        if (amount < 1)
            return;

        GlobalFilter.define("value", "" + (int) amount);
        Location loc = entity.getLocation().add(0, 1.5, 0);

        if (entity instanceof Player)
        {
            Player player = (Player) entity;

            InstanceSettings settings = holograms.getInstanceSettings(base + ".personal");
            Hologram healHolo = new Hologram(loc, settings.getFormat(), settings.ticks);
            healHolo.getVisibility().showTo(player);
            holograms.addInstance(healHolo);

            settings = holograms.getInstanceSettings(base + ".players");
            healHolo = new Hologram(loc, settings.getFormat(), settings.ticks);
            healHolo.getVisibility().exclude(player);
            holograms.addInstance(healHolo);
        }
        else
        {
            InstanceSettings settings = holograms.getInstanceSettings(base + ".entity");
            Hologram healHolo = new Hologram(loc, settings.getFormat(), settings.ticks);
            holograms.addInstance(healHolo);
        }
    }

    /**
     * Applies a scoreboard update after a delay
     */
    private class UpdateTask extends BukkitRunnable
    {
        private Player player;

        private UpdateTask(Player player)
        {
            this.player = player;
            runTaskLater(Minenight.getPlugin(), 1);
        }

        @Override
        public void run()
        {
            updateScoreboard(player);
        }
    }
}
