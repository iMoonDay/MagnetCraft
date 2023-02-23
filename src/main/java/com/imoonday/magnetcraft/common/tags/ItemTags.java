package com.imoonday.magnetcraft.common.tags;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class ItemTags {
    public static final TagKey<Item> MAGNETS = TagKey.of(RegistryKeys.ITEM, id("magnets"));
    public static final TagKey<Item> ATTRACTING_MAGNETS = TagKey.of(RegistryKeys.ITEM, id("attracting_magnets"));

    public static final TagKey<Item> TOOLS = TagKey.of(RegistryKeys.ITEM, id("tools"));

}
