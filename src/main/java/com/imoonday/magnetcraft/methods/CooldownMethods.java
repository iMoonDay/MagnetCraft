package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class CooldownMethods {

    public static void setCooldown(PlayerEntity player, ItemStack stack, int cooldown) {
        int percent = ModConfig.getConfig().value.coolingPercentage;
        if (EnchantmentMethods.hasEnchantment(stack, EnchantmentRegistries.FASTER_COOLDOWN_ENCHANTMENT)) {
            int level = EnchantmentMethods.getEnchantmentLvl(stack, EnchantmentRegistries.FASTER_COOLDOWN_ENCHANTMENT);
            cooldown -= cooldown * level / 10;
        }
        player.getItemCooldownManager().set(stack.getItem(), cooldown * percent / 100);
    }

}
