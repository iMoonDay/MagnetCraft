package com.imoonday.magnetcraft.common.tags;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class ToolTags {
    public static final TagKey<Item> TOOLS = TagKey.of(RegistryKeys.ITEM, id("tools"));
}
