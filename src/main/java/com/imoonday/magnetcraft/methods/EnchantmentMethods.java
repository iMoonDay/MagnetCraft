package com.imoonday.magnetcraft.methods;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class EnchantmentMethods {

    public static boolean hasEnchantment(LivingEntity entity, EquipmentSlot equipmentSlot, Enchantment enchantment) {
        return getEnchantmentLvl(entity, equipmentSlot, enchantment) > 0;
    }

    public static boolean hasEnchantment(LivingEntity entity, Enchantment enchantment) {
        return getEnchantmentLvl(entity, enchantment) > 0;
    }

    public static boolean hasEnchantment(ItemStack stack, Enchantment enchantment) {
        return getEnchantmentLvl(stack, enchantment) > 0;
    }

    public static int getEnchantmentLvl(LivingEntity entity, EquipmentSlot equipmentSlot, Enchantment enchantment) {
        return EnchantmentHelper.getLevel(enchantment, entity.getEquippedStack(equipmentSlot));
    }

    public static int getEnchantmentLvl(LivingEntity entity, Enchantment enchantment) {
        return Arrays.stream(EquipmentSlot.values()).mapToInt(slot -> EnchantmentHelper.getLevel(enchantment, entity.getEquippedStack(slot))).sum();
    }

    public static int getEnchantmentLvl(ItemStack stack, Enchantment enchantment) {
        return EnchantmentHelper.getLevel(enchantment, stack);
    }

}
