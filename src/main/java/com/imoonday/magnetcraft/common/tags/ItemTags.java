package com.imoonday.magnetcraft.common.tags;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

/**
 * @author iMoonDay
 */
public class ItemTags {
    public static final TagKey<Item> ATTRACTIVE_MAGNETS = TagKey.of(RegistryKeys.ITEM, id("attractive_magnets"));
    public static final TagKey<Item> FILTERABLE_MAGNETS = TagKey.of(RegistryKeys.ITEM, id("filterable_magnets"));
    public static final TagKey<Item> MAGNETS = TagKey.of(RegistryKeys.ITEM, id("magnets"));
    public static final TagKey<Item> CORES = TagKey.of(RegistryKeys.ITEM, id("cores"));
}
