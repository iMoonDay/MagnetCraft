package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.common.effects.AttractEffect;
import com.imoonday.magnetcraft.common.effects.DegaussingEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class EffectRegistries {

    public static final AttractEffect ATTRACT_EFFECT = new AttractEffect();
    public static final DegaussingEffect DEGAUSSING_EFFECT = new DegaussingEffect();

    public static void register() {
        Registry.register(Registries.STATUS_EFFECT, id("attract"), ATTRACT_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, id("degaussing"), DEGAUSSING_EFFECT);
    }
}
