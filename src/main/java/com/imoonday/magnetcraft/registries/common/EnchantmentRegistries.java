package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.common.enchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class EnchantmentRegistries {

    public static final Enchantment ATTRACT_ENCHANTMENT = register("attract", new AttractEnchantment());
    public static final Enchantment AUTOMATIC_COLLECTION_ENCHANTMENT = register("automatic_collection", new AutomaticCollectionEnchantment());
    public static final Enchantment AUTOMATIC_LOOTING_ENCHANTMENT = register("automatic_looting", new AutomaticLootingEnchantment());
    public static final Enchantment DEGAUSSING_PROTECTION_ENCHANTMENT = register("degaussing_protection", new DegaussingProtectionEnchantment());
    public static final Enchantment FASTER_COOLDOWN_ENCHANTMENT = register("faster_cooldown", new FasterCooldownEnchantment());

    public static void register() {
        MagnetCraft.LOGGER.info("EnchantmentRegistries.class Loaded");
    }

    static <T extends Enchantment> T register(String id, T enchantment) {
        Registry.register(Registries.ENCHANTMENT, id(id), enchantment);
        return enchantment;
    }

}
