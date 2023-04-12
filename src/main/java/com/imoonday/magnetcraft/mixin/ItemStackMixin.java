package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.api.MagnetCraftItemStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(ItemStack.class)
public class ItemStackMixin implements MagnetCraftItemStack {

    @Override
    public void addDamage(int damage, @Nullable Random random) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!stack.isDamageable()) {
            return;
        }
        int value = getNextDamage(damage, stack);
        stack.damage(value, Optional.ofNullable(random).orElse(Random.create()), null);
    }

    @Override
    public void addDamage(int damage) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!stack.isDamageable()) {
            return;
        }
        int value = getNextDamage(damage, stack);
        stack.setDamage(value);
    }

    private static int getNextDamage(int damage, ItemStack stack) {
        int stackDamage = stack.getDamage();
        int stackMaxDamage = stack.getMaxDamage();
        int finalDamage = stackDamage + damage;
        return MathHelper.clamp(finalDamage, 0, stackMaxDamage);
    }

    @Override
    public boolean isBroken() {
        ItemStack stack = (ItemStack) (Object) this;
        return stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage();
    }

    @Override
    public boolean hasEnchantment(Enchantment enchantment) {
        ItemStack stack = (ItemStack) (Object) this;
        return stack.getEnchantmentLvl(enchantment) > 0;
    }

    @Override
    public int getEnchantmentLvl(Enchantment enchantment) {
        ItemStack stack = (ItemStack) (Object) this;
        return EnchantmentHelper.getLevel(enchantment, stack);
    }
}
