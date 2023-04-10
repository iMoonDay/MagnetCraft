package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.api.MagnetCraftItemStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemStackMixin implements MagnetCraftItemStack {
    @Override
    public void addDamage(@Nullable Random random, int damage, boolean unbreaking) {
        ItemStack stack = (ItemStack) (Object) this;
        if (!stack.isDamageable()) {
            return;
        }
        int stackDamage = stack.getDamage();
        int stackMaxDamage = stack.getMaxDamage();
        int finalDamage = stackDamage + damage;
        if (unbreaking) {
            if (random == null) {
                random = Random.create();
            }
            stack.damage(finalDamage > stackMaxDamage ? 0 : Math.max(damage, 0), random, null);
        } else {
            stack.setDamage(finalDamage > stackMaxDamage ? stackMaxDamage : Math.max(finalDamage, 0));
        }
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
