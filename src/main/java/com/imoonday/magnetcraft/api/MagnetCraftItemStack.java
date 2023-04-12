package com.imoonday.magnetcraft.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface MagnetCraftItemStack {

    default void addDamage(int damage, @Nullable Random random) {
    }

    default void addDamage(int damage) {
    }

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
