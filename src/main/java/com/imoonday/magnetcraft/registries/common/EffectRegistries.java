package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.common.effects.AttractEffect;
import com.imoonday.magnetcraft.common.effects.DegaussingEffect;
import com.imoonday.magnetcraft.common.effects.UnattractEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class EffectRegistries {

    public static final AttractEffect ATTRACT_EFFECT = register("attract", new AttractEffect());
    public static final DegaussingEffect DEGAUSSING_EFFECT = register("degaussing", new DegaussingEffect());
    public static final UnattractEffect UNATTRACT_EFFECT = register("unattract", new UnattractEffect());

    public static void register() {
//        Registry.register(Registries.STATUS_EFFECT, id("attract"), ATTRACT_EFFECT);
//        Registry.register(Registries.STATUS_EFFECT, id("degaussing"), DEGAUSSING_EFFECT);
//        Registry.register(Registries.STATUS_EFFECT, id("unattract"), UNATTRACT_EFFECT);
    }

    private static <T extends StatusEffect> T register(String id, T effect) {
        Registry.register(Registries.STATUS_EFFECT, id(id), effect);
        return effect;
    }

}
