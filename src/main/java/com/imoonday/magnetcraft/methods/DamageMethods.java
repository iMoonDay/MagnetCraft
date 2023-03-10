package com.imoonday.magnetcraft.methods;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class DamageMethods {
    public static void addDamage(LivingEntity user, Hand hand, int damage, boolean unbreaking) {
        ItemStack stack = user.getStackInHand(hand);
        if (unbreaking) {
            stack.damage(damage, user.getRandom(), user instanceof ServerPlayerEntity serverPlayer ? serverPlayer : null);
        } else {
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
    }

    public static boolean isEmptyDamage(LivingEntity player, Hand hand) {
        if (hand == null) {
            return false;
        }
        ItemStack stack = player.getStackInHand(hand);
        return stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage();
    }

    public static boolean isEmptyDamage(ItemStack stack) {
        return stack.isDamageable() && stack.getDamage() >= stack.getMaxDamage();
    }

}
