package com.imoonday.magnetcraft.registries;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.MagnetCraft.MOD_ID;
import static com.imoonday.magnetcraft.registries.EffectRegistries.ATTRACT_EFFECT;
import static com.imoonday.magnetcraft.registries.EffectRegistries.DEGAUSSING_EFFECT;

public class PotionRegistries {

    public static final Potion ATTRACT_POTION = new Potion(new StatusEffectInstance(ATTRACT_EFFECT, 5 * 60 * 20));
    public static final Potion DEGAUSSING_POTION = new Potion(new StatusEffectInstance(DEGAUSSING_EFFECT, 5 * 60 * 20));

    public static void register(){

        Registry.register(Registries.POTION, new Identifier(
                MOD_ID, "attract"), ATTRACT_POTION);
        Registry.register(Registries.POTION, new Identifier(
                MOD_ID, "degaussing"), DEGAUSSING_POTION);

    }
}
