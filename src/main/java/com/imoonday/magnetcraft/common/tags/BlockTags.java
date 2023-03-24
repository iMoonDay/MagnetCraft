package com.imoonday.magnetcraft.common.tags;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

/**
 * @author iMoonDay
 */
public class BlockTags {
    public static final TagKey<Block> MAGNETITE_ORES = TagKey.of(RegistryKeys.BLOCK, id("magnetite_ores"));
}
