package com.imoonday.magnetcraft.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface MagnetCraftItemStack {

    default void addDamage(@Nullable Random random, int damage, boolean unbreaking) {}

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
