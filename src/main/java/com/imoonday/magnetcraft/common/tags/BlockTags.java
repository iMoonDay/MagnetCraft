package com.imoonday.magnetcraft.common.tags;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class BlockTags {
    public static final TagKey<Block> MAGNETITE_ORES = TagKey.of(RegistryKeys.BLOCK, id("magnetite_ores"));
    public static final TagKey<Block> MAGLEV_RAILS = TagKey.of(RegistryKeys.BLOCK,id("maglev_rails"));
}
