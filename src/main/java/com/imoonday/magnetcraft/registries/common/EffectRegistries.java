package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.common.effects.AttractEffect;
import com.imoonday.magnetcraft.common.effects.DegaussingEffect;
import com.imoonday.magnetcraft.common.effects.UnattractEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

/**
 * @author iMoonDay
 */
public class EffectRegistries {

    public static final AttractEffect ATTRACT_EFFECT = register("attract", new AttractEffect());
    public static final DegaussingEffect DEGAUSSING_EFFECT = register("degaussing", new DegaussingEffect());
    public static final UnattractEffect UNATTRACT_EFFECT = register("unattract", new UnattractEffect());

    public static void register() {
        MagnetCraft.LOGGER.info("EffectRegistries.class Loaded");
    }

    static <T extends StatusEffect> T register(String id, T effect) {
        Registry.register(Registries.STATUS_EFFECT, id(id), effect);
        return effect;
    }

}
