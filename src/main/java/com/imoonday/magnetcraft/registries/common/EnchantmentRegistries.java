package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.common.enchantments.AttractEnchantment;
import com.imoonday.magnetcraft.common.enchantments.AutomaticCollectionEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class EnchantmentRegistries{

        public static final Enchantment ATTRACT_ENCHANTMENT = new AttractEnchantment();
        public static final Enchantment AUTOMATIC_COLLECTION_ENCHANTMENT = new AutomaticCollectionEnchantment();

    public static void register() {
        Registry.register(Registries.ENCHANTMENT, id("attract"), ATTRACT_ENCHANTMENT);
        Registry.register(Registries.ENCHANTMENT, id("automatic_collection"), AUTOMATIC_COLLECTION_ENCHANTMENT);
    }
}
