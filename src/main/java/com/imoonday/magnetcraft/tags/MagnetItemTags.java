package com.imoonday.magnetcraft.tags;

import com.imoonday.magnetcraft.MagnetCraft;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class MagnetItemTags {
    public static final TagKey<Item> MAGNET_ITEMS = TagKey.of(RegistryKeys.ITEM, new Identifier(MagnetCraft.MOD_ID, "magnets"));
}
