package com.imoonday.magnetcraft.registries.common;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.magnetcraft.registries.common.EffectRegistries.*;
import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class PotionRegistries {

    public static final Potion ATTRACT_POTION = new Potion(new StatusEffectInstance(ATTRACT_EFFECT, 5 * 60 * 20));
    public static final Potion DEGAUSSING_POTION = new Potion(new StatusEffectInstance(DEGAUSSING_EFFECT, 5 * 60 * 20));
    public static final Potion UNATTRACT_POTION = new Potion(new StatusEffectInstance(UNATTRACT_EFFECT, 5 * 60 * 20));

    public static void register() {

        Registry.register(Registries.POTION, id("attract"), ATTRACT_POTION);
        Registry.register(Registries.POTION, id("degaussing"), DEGAUSSING_POTION);
        Registry.register(Registries.POTION, id("unattract"), UNATTRACT_POTION);

    }
}
