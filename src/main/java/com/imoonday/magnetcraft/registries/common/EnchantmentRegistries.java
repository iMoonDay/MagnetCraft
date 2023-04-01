package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.common.enchantments.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static com.imoonday.magnetcraft.registries.special.IdentifierRegistries.id;
import static com.imoonday.magnetcraft.registries.special.ItemGroupRegistries.MAGNET_OHTERS;

public class EnchantmentRegistries {

    public static final ArrayList<Enchantment> CHECK_ENCHANTMENTS = new ArrayList<>();

    public static final AttractEnchantment ATTRACT_ENCHANTMENT = register("attract", new AttractEnchantment());
    public static final AutomaticCollectionEnchantment AUTOMATIC_COLLECTION_ENCHANTMENT = register("automatic_collection", new AutomaticCollectionEnchantment());
    public static final AutomaticLootingEnchantment AUTOMATIC_LOOTING_ENCHANTMENT = register("automatic_looting", new AutomaticLootingEnchantment());
    public static final DegaussingProtectionEnchantment DEGAUSSING_PROTECTION_ENCHANTMENT = register("degaussing_protection", new DegaussingProtectionEnchantment());
    public static final FasterCooldownEnchantment FASTER_COOLDOWN_ENCHANTMENT = registerSpecial("faster_cooldown", new FasterCooldownEnchantment());
    public static final MagneticLevitationEnchantment MAGNETIC_LEVITATION_ENCHANTMENT = register("magnetic_levitation", new MagneticLevitationEnchantment());
    public static final AccumulatorEnchantment ACCUMULATOR_ENCHANTMENT = registerSpecial("accumulator", new AccumulatorEnchantment());
    public static final ElectromagneticProtectionEnchantment ELECTROMAGNETIC_PROTECTION_ENCHANTMENT = register("electromagnetic_protection", new ElectromagneticProtectionEnchantment());

    public static void register() {
        MagnetCraft.LOGGER.info("EnchantmentRegistries.class Loaded");
    }

    static <T extends Enchantment> T register(String id, T enchantment) {
        ItemGroupEvents.modifyEntriesEvent(MAGNET_OHTERS).register(content -> IntStream.rangeClosed(enchantment.getMinLevel(), enchantment.getMaxLevel()).forEach(i -> content.add(EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, i)))));
        return Registry.register(Registries.ENCHANTMENT, id(id), enchantment);
    }

    static <T extends Enchantment> T registerSpecial(String id, T enchantment) {
        CHECK_ENCHANTMENTS.add(enchantment);
        return register(id, enchantment);
    }

}
