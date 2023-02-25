package com.imoonday.magnetcraft.methods;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class DamageMethods {
    public static void addDamage(LivingEntity user, Hand hand, int damage) {
        ItemStack stack = user.getStackInHand(hand);
        boolean stackDamageable = stack.isDamageable();
        boolean creative = user.isPlayer() && ((PlayerEntity) user).isCreative();
        int stackDamage = stack.getDamage();
        int stackMaxDamage = stack.getMaxDamage();
        int setDamage = stackDamage + damage;
        if (!creative && stackDamageable) {
            stack.setDamage(setDamage);
            stackDamage = stack.getDamage();
            if (stackDamage > stackMaxDamage) {
                stack.setDamage(stackMaxDamage);
            }
        }
    }

    public static boolean isEmptyDamage(LivingEntity player, Hand hand) {
        if (hand == null) {
            return false;
        }
        ItemStack stack = player.getStackInHand(hand);
        boolean isDamageable = stack.isDamageable();
        boolean isEmptyDamage = stack.getDamage() >= stack.getMaxDamage();
        return (isDamageable && isEmptyDamage);
    }

    public static boolean isEmptyDamage(ItemStack stack) {
        boolean isDamageable = stack.isDamageable();
        boolean isEmptyDamage = stack.getDamage() >= stack.getMaxDamage();
        return (isDamageable && isEmptyDamage);
    }

}
