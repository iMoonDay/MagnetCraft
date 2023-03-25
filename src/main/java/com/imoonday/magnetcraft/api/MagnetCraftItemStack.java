package com.imoonday.magnetcraft.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.math.random.Random;

@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface MagnetCraftItemStack {

    default void addDamage(Random random, int damage, boolean unbreaking) {}

    default boolean isBroken() {
        return false;
    }

    default boolean hasEnchantment(Enchantment enchantment) {
        return false;
    }

    default int getEnchantmentLvl(Enchantment enchantment) {
        return 0;
    }

}
