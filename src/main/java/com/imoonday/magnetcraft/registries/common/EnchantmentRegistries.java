package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.common.enchantments.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;

public class EnchantmentRegistries {

    public static final ArrayList<Enchantment> CHECK_ENCHANTMENTS = new ArrayList<>();

    public static final AttractEnchantment ATTRACT_ENCHANTMENT = register("attract", new AttractEnchantment(), false);
    public static final AutomaticCollectionEnchantment AUTOMATIC_COLLECTION_ENCHANTMENT = register("automatic_collection", new AutomaticCollectionEnchantment(), false);
    public static final AutomaticLootingEnchantment AUTOMATIC_LOOTING_ENCHANTMENT = register("automatic_looting", new AutomaticLootingEnchantment(), false);
    public static final DegaussingProtectionEnchantment DEGAUSSING_PROTECTION_ENCHANTMENT = register("degaussing_protection", new DegaussingProtectionEnchantment(), false);
    public static final FasterCooldownEnchantment FASTER_COOLDOWN_ENCHANTMENT = register("faster_cooldown", new FasterCooldownEnchantment(), true);
    public static final MagneticLevitationEnchantment MAGNETIC_LEVITATION_ENCHANTMENT = register("magnetic_levitation", new MagneticLevitationEnchantment(), false);
    public static final AccumulatorEnchantment ACCUMULATOR_ENCHANTMENT = register("accumulator", new AccumulatorEnchantment(), true);

    public static void register() {
        MagnetCraft.LOGGER.info("EnchantmentRegistries.class Loaded");
    }

    static <T extends Enchantment> T register(String id, T enchantment, boolean checkAcceptable) {
        Registry.register(Registries.ENCHANTMENT, id(id), enchantment);
        if (checkAcceptable) {
            CHECK_ENCHANTMENTS.add(enchantment);
        }
        return enchantment;
    }

}
