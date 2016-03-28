/**
 * MineNight
 * com.sucy.minenight.nms.v1_9_R1.BlockManipulator
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

import com.sucy.minenight.util.config.parse.DataSection;
import com.sucy.minenight.world.Worlds;
import com.sucy.minenight.world.enums.ValueSetting;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.craftbukkit.v1_9_R1.potion.CraftPotionEffectType;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * Handles overriding vanilla Minecraft values
 */
public class VanillaManipulator
{
    private static Field modifiers;

    /**
     * Overrides block strengths
     *
     * @param data data to use for block strengths
     * @throws Exception
     */
    public static void updateStrengths(DataSection data)
        throws Exception
    {
        Field strength = Block.class.getDeclaredField("strength");
        strength.setAccessible(true);
        for (String key : data.keys())
        {
            Block block = Block.REGISTRY.get(new MinecraftKey(key));
            strength.set(block, data.getFloat(key));
        }
    }

    /**
     * Overrides item stack size
     *
     * @param data data to pull from
     * @throws Exception
     */
    public static void updateStackSize(DataSection data)
        throws Exception
    {
        for (String key : data.keys())
        {
            Item.REGISTRY.get(new MinecraftKey(key)).d(data.getInt(key));
        }
    }

    /**
     * Creates and injects overrides for each type of vanilla potion effect
     */
    public static void updatePotions()
        throws Exception
    {
        // Grab needed reflection values
        modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);

        RegistryMaterials<MinecraftKey, MobEffectList> REGISTRY = MobEffectList.REGISTRY;

        // Inject the effects into the registry and MobEffects references
        inject(1, new MinecraftKey("speed"), "FASTER_MOVEMENT", new InjectedEffect(false, 8171462).cPub("effect.moveSpeed").bPub(0, 0).a(GenericAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", v(ValueSetting.POTION_MOVE_SPEED), 2).j());
        inject(2, new MinecraftKey("slowness"), "SLOWER_MOVEMENT", new InjectedEffect(true, 5926017).cPub("effect.moveSlowdown").bPub(1, 0).a(GenericAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", v(ValueSetting.POTION_MOVE_SLOW_DOWN), 2));
        inject(3, new MinecraftKey("haste"), "FASTER_DIG", new InjectedEffect(false, 14270531).cPub("effect.digSpeed").bPub(2, 0).aPub(1.5D).j().a(GenericAttributes.f, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", v(ValueSetting.POTION_FAST_DIGGING), 2));
        inject(4, new MinecraftKey("mining_fatigue"), "SLOWER_DIG", new InjectedEffect(true, 4866583).cPub("effect.digSlowDown").bPub(3, 0).a(GenericAttributes.f, "55FCED67-E92A-486E-9800-B47F202C4386", v(ValueSetting.POTION_SLOW_DIGGING), 2));
        inject(5, new MinecraftKey("strength"), "INCREASE_DAMAGE", new InjectedEffect(false, 9643043).cPub("effect.damageBoost").bPub(4, 0).a(GenericAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", v(ValueSetting.POTION_DAMAGE_BOOST), 0).j());
        inject(6, new MinecraftKey("instant_health"), "HEAL", new InjectedEffect(false, 16262179, true).cPub("effect.heal").j());
        inject(7, new MinecraftKey("instant_damage"), "HARM", new InjectedEffect(true, 4393481, true).cPub("effect.harm").j());
        inject(8, new MinecraftKey("jump_boost"), "JUMP", new InjectedEffect(false, 2293580).cPub("effect.jump").bPub(2, 1).j());
        inject(9, new MinecraftKey("nausea"), "CONFUSION", new InjectedEffect(true, 5578058).cPub("effect.confusion").bPub(3, 1).aPub(0.25D));
        inject(10, new MinecraftKey("regeneration"), "REGENERATION", new InjectedEffect(false, 13458603).cPub("effect.regeneration").bPub(7, 0).aPub(0.25D).j());
        inject(11, new MinecraftKey("resistance"), "RESISTANCE", new InjectedEffect(false, 10044730).cPub("effect.resistance").bPub(6, 1).j());
        inject(12, new MinecraftKey("fire_resistance"), "FIRE_RESISTANCE", new InjectedEffect(false, 14981690).cPub("effect.fireResistance").bPub(7, 1).j());
        inject(13, new MinecraftKey("water_breathing"), "WATER_BREATHING", new InjectedEffect(false, 3035801).cPub("effect.waterBreathing").bPub(0, 2).j());
        inject(14, new MinecraftKey("invisibility"), "INVISIBILITY", new InjectedEffect(false, 8356754).cPub("effect.invisibility").bPub(0, 1).j());
        inject(15, new MinecraftKey("blindness"), "BLINDNESS", new InjectedEffect(true, 2039587).cPub("effect.blindness").bPub(5, 1).aPub(0.25D));
        inject(16, new MinecraftKey("night_vision"), "NIGHT_VISION", new InjectedEffect(false, 2039713).cPub("effect.nightVision").bPub(4, 1).j());
        inject(17, new MinecraftKey("hunger"), "HUNGER", new InjectedEffect(true, 5797459).cPub("effect.hunger").bPub(1, 1));
        inject(18, new MinecraftKey("weakness"), "WEAKNESS", new InjectedEffect(true, 4738376).cPub("effect.weakness").bPub(5, 0).a(GenericAttributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", v(ValueSetting.POTION_WEAKNESS), 0));
        inject(19, new MinecraftKey("poison"), "POISON", new InjectedEffect(true, 5149489).cPub("effect.poison").bPub(6, 0).aPub(0.25D));
        inject(20, new MinecraftKey("wither"), "WITHER", new InjectedEffect(true, 3484199).cPub("effect.wither").bPub(1, 2).aPub(0.25D));
        inject(21, new MinecraftKey("health_boost"), "HEALTH_BOOST", new InjectedEffect(false, 16284963).cPub("effect.healthBoost").bPub(7, 2).a(GenericAttributes.maxHealth, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", v(ValueSetting.POTION_HEALTH_BOOST), 0).j());
        inject(22, new MinecraftKey("absorption"), "ABSORBTION", new InjectedEffect(false, 2445989).cPub("effect.absorption").bPub(2, 2).j());
        inject(23, new MinecraftKey("saturation"), "SATURATION", new InjectedEffect(false, 16262179, true).cPub("effect.saturation").j());
        inject(24, new MinecraftKey("glowing"), "GLOWING", new InjectedEffect(false, 9740385).cPub("effect.glowing").bPub(4, 2));
        inject(25, new MinecraftKey("levitation"), "LEVITATION", new InjectedEffect(true, 13565951).cPub("effect.levitation").bPub(3, 2));
        inject(26, new MinecraftKey("luck"), "z", new InjectedEffect(false, 3381504).cPub("effect.luck").bPub(5, 2).j().a(GenericAttributes.h, "03C3C89D-7037-4B42-869F-B146BCB64D2E", v(ValueSetting.POTION_LUCK), 0));
        inject(27, new MinecraftKey("unluck"), "A", new InjectedEffect(true, 12624973).cPub("effect.unluck").bPub(6, 2).a(GenericAttributes.h, "CC5AF142-2BD2-4215-B636-2605AED11727", v(ValueSetting.POTION_UNLUCK), 0));

        // Tell the potion list to accept the new values
        set(PotionEffectType.class, "byId", new PotionEffectType[28]);
        set(PotionEffectType.class, "byName", new HashMap<String, PotionEffectType>());

        // Inject the effects into the potion lists
        PotionRegistry.b();
        set(PotionEffectType.class, "acceptingNew", true);
        for (MobEffectList effect : REGISTRY)
            PotionEffectType.registerPotionEffectType(new CraftPotionEffectType(effect));
        PotionEffectType.stopAcceptingRegistrations();
    }

    /**
     * Grabs a strength setting from the world settings
     *
     * @param setting seting setting to grab
     * @return strength setting value
     */
    private static float v(ValueSetting setting)
    {
        return Worlds.getSettings().getValue(setting);
    }

    /**
     * Injects a potion effect into the server functions
     *
     * @param id     integer ID of the effect
     * @param key    key used for the effect
     * @param name   name of the MobEffects field for the effect
     * @param effect effect to inject
     * @throws Exception
     */
    private static void inject(int id, MinecraftKey key, String name, MobEffectList effect)
        throws Exception
    {
        MobEffectList.REGISTRY.a(id, key, effect);
        set(MobEffects.class, name, MobEffectList.REGISTRY.get(key));
    }

    /**
     * Sets a static field for a class including final fields
     *
     * @param c     class to set for
     * @param f     field name
     * @param value value to set to
     * @throws Exception
     */
    private static void set(Class<?> c, String f, Object value)
        throws Exception
    {
        Field field = c.getDeclaredField(f);
        field.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, value);
    }
}
