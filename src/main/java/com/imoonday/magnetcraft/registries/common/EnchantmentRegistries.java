package com.imoonday.magnetcraft.registries.common;

import com.imoonday.magnetcraft.common.enchantments.AttractEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.imoonday.magnetcraft.MagnetCraft.MOD_ID;

public class EnchantmentRegistries{

        public static final Enchantment ATTRACT_ENCHANTMENT = new AttractEnchantment();

    public static void register() {
        Registry.register(Registries.ENCHANTMENT, new Identifier(MOD_ID, "attract"), ATTRACT_ENCHANTMENT);
    }
}
