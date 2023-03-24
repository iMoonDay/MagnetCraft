package com.imoonday.magnetcraft.common.tags;

import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

/**
 * @author iMoonDay
 */
public class FluidTags {

    public static final TagKey<Fluid> MAGNETIC_FLUID = TagKey.of(RegistryKeys.FLUID, id("magnetic_fluid"));

}
