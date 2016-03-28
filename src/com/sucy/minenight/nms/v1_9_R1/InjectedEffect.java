/**
 * MineNight
 * com.sucy.minenight.nms.v1_9_R1.InjectedEffect
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
package com.sucy.minenight.nms.v1_9_R1;

import com.sucy.minenight.world.Worlds;
import com.sucy.minenight.world.enums.TickSetting;
import com.sucy.minenight.world.enums.ValueSetting;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.craftbukkit.v1_9_R1.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * A vanilla potion effect override that is configurable
 * and injected into the server to replace the old ones
 */
public class InjectedEffect extends MobEffectList
{
    private boolean instant;

    /**
     * Creates a non-instant potion effect
     *
     * @param flag effect flag
     * @param i    effect ID
     * @throws Exception
     */
    public InjectedEffect(boolean flag, int i)
        throws Exception
    {
        this(flag, i, false);
    }

    /**
     * Creates an optionally instant potion effect
     *
     * @param flag    effect flag
     * @param i       effect ID
     * @param instant instant status
     * @throws Exception
     */
    public InjectedEffect(boolean flag, int i, boolean instant)
        throws Exception
    {
        super(flag, i);

        this.instant = instant;
    }

    /**
     * Passes initialization values onto the superclass
     *
     * @param value initialization value
     * @return this
     */
    public InjectedEffect aPub(double value)
    {
        a(value);
        return this;
    }

    /**
     * Passes initialization values onto the superclass
     *
     * @param value initialization value
     * @return this
     */
    public InjectedEffect cPub(String value)
    {
        c(value);
        return this;
    }

    /**
     * Passes initialization values onto the superclass
     *
     * @param i initialization value
     * @param j initialization value
     * @return this
     */
    public InjectedEffect bPub(int i, int j)
    {
        b(i, j);
        return this;
    }

    /**
     * Ticks the potion effect, using config data to decide how strong it is
     *
     * @param entity entity to affect
     * @param i      potion tier
     */
    @Override
    public void tick(EntityLiving entity, int i)
    {
        if (this == MobEffects.REGENERATION)
        {
            if (entity.getHealth() < entity.getMaxHealth())
                entity.heal(v(ValueSetting.POTION_REGENERATION, i), EntityRegainHealthEvent.RegainReason.MAGIC_REGEN);
        }
        else if (this == MobEffects.POISON)
        {
            float value = StrictMath.min(v(ValueSetting.POTION_POISON, i), entity.getHealth() - 0.1f);
            entity.damageEntity(CraftEventFactory.POISON, value);
        }
        else if (this == MobEffects.WITHER)
            entity.damageEntity(DamageSource.WITHER, v(ValueSetting.POTION_WITHER, i));
        else if ((this == MobEffects.HUNGER) && ((entity instanceof EntityHuman)))
            ((EntityHuman) entity).applyExhaustion(v(ValueSetting.POTION_EXHAUSTION, i));
        else if ((this == MobEffects.SATURATION) && ((entity instanceof EntityHuman)))
        {
            if (!entity.world.isClientSide)
            {
                EntityHuman entityhuman = (EntityHuman) entity;
                int oldFoodLevel = entityhuman.getFoodData().foodLevel;

                FoodLevelChangeEvent event = CraftEventFactory.callFoodLevelChangeEvent(entityhuman, (int)v(ValueSetting.POTION_SATURATION, i) + oldFoodLevel);

                if (!event.isCancelled())
                    entityhuman.getFoodData().eat(event.getFoodLevel() - oldFoodLevel, 1.0F);

                ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutUpdateHealth(((EntityPlayer) entityhuman).getBukkitEntity().getScaledHealth(), entityhuman.getFoodData().foodLevel, entityhuman.getFoodData().saturationLevel));
            }
        }
        else if ((this != MobEffects.HEAL || entity.bP()) && (this != MobEffects.HARM || !entity.bP()))
        {
            if (((this == MobEffects.HARM) && (!entity.bP())) || ((this == MobEffects.HEAL) && (entity.bP())))
                entity.damageEntity(DamageSource.MAGIC, v(ValueSetting.POTION_HARM, i));
        }
        else
            entity.heal(v(ValueSetting.POTION_HEAL, i), EntityRegainHealthEvent.RegainReason.MAGIC);
    }

    /**
     * Applies instant potion effects
     *
     * @param thrown  potion that was thrown to inflict the effect
     * @param thrower entity that threw the potion
     * @param target  entity being affected by the potion
     * @param i       tier of the potion
     * @param d0      effect amplifier (lingering potions have half strength on tick)
     */
    public void applyInstantEffect(Entity thrown, Entity thrower, EntityLiving target, int i, double d0)
    {
        if (((this != MobEffects.HEAL) || (target.bP())) && ((this != MobEffects.HARM) || (!target.bP()))) {
            if (((this == MobEffects.HARM) && (!target.bP())) || ((this == MobEffects.HEAL) && (target.bP()))) {
                int j = (int)(d0 * v(ValueSetting.POTION_HARM, i) + 0.5D);
                if (thrown == null)
                    target.damageEntity(DamageSource.MAGIC, j);
                else
                    target.damageEntity(DamageSource.b(thrown, thrower), j);
            }
        }
        else {
            int j = (int)(d0 * v(ValueSetting.POTION_HEAL, i) + 0.5D);
            target.heal(j, EntityRegainHealthEvent.RegainReason.MAGIC);
        }
    }

    /**
     * Checks whether or not the ticking effect should prock
     *
     * @param i current effect duration
     * @param j the potion tier
     * @return true if should prock
     */
    public boolean a(int i, int j)
    {
        return
            (this == MobEffects.REGENERATION && i % t(TickSetting.POTION_REGENERATION) == 0)
            || (this == MobEffects.POISON && i % t(TickSetting.POTION_POISON) == 0)
            || (this == MobEffects.WITHER && i % t(TickSetting.POTION_WITHER) == 0)
            || (this == MobEffects.HUNGER && i % t(TickSetting.POTION_HUNGER) == 0);
    }

    /**
     * Grabs a tick setting from the world settings
     *
     * @param setting setting type to grab
     * @return tick setting value
     */
    private int t(TickSetting setting)
    {
        return Worlds.getSettings().getTicks(setting);
    }

    /**
     * Grabs a strength setting from the world settings
     *
     * @param setting setting type to grab
     * @param i       potion tier
     * @return strength setting value
     */
    private float v(ValueSetting setting, int i)
    {
        return Worlds.getSettings().getValue(setting) * (i + 1);
    }

    /**
     * @return true if instant
     */
    @Override
    public boolean isInstant()
    {
        return instant;
    }

    /**
     * Removes the effect from an entity
     *
     * @param entity entity removing from
     * @param map    properties of the effect
     * @param i      potion tier
     */
    @Override
    public void b(EntityLiving entity, AttributeMapBase map, int i)
    {
        if (this == MobEffects.ABSORBTION)
        {
            entity.setAbsorptionHearts(entity.getAbsorptionHearts() + v(ValueSetting.POTION_ABSORPTION, i));
        }

        super.b(entity, map, i);
    }

    /**
     * Adds the effect to an entity
     *
     * @param entity entity to add to
     * @param map    properties of the effect
     * @param i      potion tier
     */
    @Override
    public void a(EntityLiving entity, AttributeMapBase map, int i)
    {
        if (this == MobEffects.ABSORBTION)
        {
            entity.setAbsorptionHearts(entity.getAbsorptionHearts() - v(ValueSetting.POTION_ABSORPTION, i));
        }

        super.a(entity, map, i);

        if (this == MobEffects.HEALTH_BOOST)
        {
            if (entity.getHealth() > entity.getMaxHealth())
                entity.setHealth(entity.getMaxHealth());
        }
    }
}
