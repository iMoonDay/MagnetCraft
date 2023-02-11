package com.imoonday.magnetcraft.registries;

import com.imoonday.magnetcraft.effects.AttractEffect;
import com.imoonday.magnetcraft.effects.DegaussingEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.MagnetCraft.MOD_ID;

public class EffectRegistries {

    public static final AttractEffect ATTRACT_EFFECT = new AttractEffect();
    public static final DegaussingEffect DEGAUSSING_EFFECT = new DegaussingEffect();

    public static void register(){
        Registry.register(Registries.STATUS_EFFECT, new Identifier(
                MOD_ID, "attract"), ATTRACT_EFFECT);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(
                MOD_ID, "degaussing"), DEGAUSSING_EFFECT);

    }
}
