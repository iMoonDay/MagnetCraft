package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.MagnetCraft;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.magnetcraft.registries.common.EffectRegistries.*;
import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

@SuppressWarnings("unused")
public class PotionRegistries {

    public static final Potion ATTRACT_POTION = register("attract", new Potion(new StatusEffectInstance(ATTRACT_EFFECT, 5 * 60 * 20)));
    public static final Potion DEGAUSSING_POTION = register("degaussing", new Potion(new StatusEffectInstance(DEGAUSSING_EFFECT, 5 * 60 * 20)));
    public static final Potion UNATTRACT_POTION = register("unattract", new Potion(new StatusEffectInstance(UNATTRACT_EFFECT, 5 * 60 * 20)));

    public static void register() {
        MagnetCraft.LOGGER.info("PotionRegistries.class Loaded");
    }

    static <T extends Potion> T register(String id, T potion) {
        Registry.register(Registries.POTION, id(id), potion);
        return potion;
    }

}
