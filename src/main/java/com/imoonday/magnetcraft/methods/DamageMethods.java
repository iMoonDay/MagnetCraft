package com.imoonday.magnetcraft.methods;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;

public class DamageMethods {
    public static void addDamage(LivingEntity user, Hand hand, int damage, boolean unbreaking) {
        ItemStack stack = user.getStackInHand(hand);
        if ((user instanceof PlayerEntity player && player.getAbilities().creativeMode && damage > 0) || !stack.isDamageable()) {
            return;
        }
        if (unbreaking) {
            stack.damage(damage, user.getRandom(), user instanceof ServerPlayerEntity serverPlayer ? serverPlayer : null);
        } else {
            int stackDamage = stack.getDamage();
            int stackMaxDamage = stack.getMaxDamage();
            int finalDamage = stackDamage + damage;
            stack.setDamage(finalDamage > stackMaxDamage ? stackMaxDamage : Math.max(finalDamage, 0));
        }
    }

    public static void addDamage(ItemStack stack,Random random, int damage, boolean unbreaking) {
        if (!stack.isDamageable()) {
            return;
        }
        if (unbreaking) {
            stack.damage(damage, random,null);
        } else {
            int stackDamage = stack.getDamage();
            int stackMaxDamage = stack.getMaxDamage();
            int finalDamage = stackDamage + damage;
            stack.setDamage(finalDamage > stackMaxDamage ? stackMaxDamage : Math.max(finalDamage, 0));
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
