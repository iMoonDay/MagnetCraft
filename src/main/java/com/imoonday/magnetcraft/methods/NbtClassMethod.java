package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class NbtClassMethod {

    public static void enabledSwitch(World world, PlayerEntity player, Hand hand) {
        boolean isMainhand = hand == Hand.MAIN_HAND;
        boolean client = world.isClient;
        boolean display = AutoConfig.getConfigHolder(ModConfig.class).getConfig().displayActionBar;
        boolean enableSneakToSwitch = ModConfig.getConfig().enableSneakToSwitch;
        double dis = ModConfig.getConfig().value.creatureMagnetAttractDis;
        Text message;
        SoundEvent sound;
        ItemStack stack = isMainhand ? player.getMainHandStack() : player.getOffHandStack();
        if (!enableSneakToSwitch) {
            return;
        }
        boolean enabled = stack.getOrCreateNbt().getBoolean("enabled");
        stack.getOrCreateNbt().putBoolean("enabled", !enabled);
        enabled = !enabled;
        if (enabled) {
            message = isMainhand ? Text.translatable("text.magnetcraft.message.mainhand_on") : Text.translatable("text.magnetcraft.message.offhand_on");
            sound = SoundEvents.BLOCK_BEACON_ACTIVATE;
        } else {
            message = isMainhand ? Text.translatable("text.magnetcraft.message.mainhand_off") : Text.translatable("text.magnetcraft.message.offhand_off");
            sound = SoundEvents.BLOCK_BEACON_DEACTIVATE;
            if (stack.isOf(ItemRegistries.CREATURE_MAGNET_ITEM)) {
                player.getWorld().getOtherEntities(player, new Box(player.getX() + dis, player.getY() + dis, player.getZ() + dis, player.getX() - dis, player.getY() - dis, player.getZ() - dis), e -> (e.getScoreboardTags().contains(player.getEntityName()) && e instanceof LivingEntity && e.distanceTo(player) <= dis)).forEach(e -> e.removeScoreboardTag(player.getEntityName()));
            }
        }
        if (client) {
            player.playSound(sound, 1, 1);
        }
        if (display && !client) {
            player.sendMessage(message, true);
        }
    }

    public static void enabledCheck(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains("enabled")) {
            enabledSet(stack);
        }
    }

    public static void enabledSet(ItemStack stack) {
        stack.getOrCreateNbt().putBoolean("enabled", true);
    }

    public static void usedTickCheck(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains("usedTick")) {
            usedTickSet(stack);
        }
    }

    public static void usedTickSet(ItemStack stack) {
        stack.getOrCreateNbt().putInt("usedTick", 0);
    }

    public static void addDamage(LivingEntity user, Hand hand, int damage) {
        boolean mainhand = hand == Hand.MAIN_HAND;
        boolean offhand = hand == Hand.OFF_HAND;
        boolean mainhandDamageable = user.getMainHandStack().isDamageable();
        boolean offhandDamageable = user.getOffHandStack().isDamageable();
        boolean creative = user.isPlayer() && ((PlayerEntity) user).isCreative();
        int mainhandDamage = user.getMainHandStack().getDamage();
        int offhandDamage = user.getOffHandStack().getDamage();
        int mainhandMaxDamage = user.getMainHandStack().getMaxDamage();
        int offhandMaxDamage = user.getOffHandStack().getMaxDamage();
        int mainhandSetDamage = mainhandDamage + damage;
        int offhandSetDamage = offhandDamage + damage;
        if (!creative) {
            if (mainhand && mainhandDamageable) {
                user.getMainHandStack().setDamage(mainhandSetDamage);
                if (mainhandDamage > mainhandMaxDamage) {
                    user.getMainHandStack().setDamage(mainhandMaxDamage);
                }
            } else if (offhand && offhandDamageable) {
                user.getOffHandStack().setDamage(offhandSetDamage);
                if (offhandDamage > offhandMaxDamage) {
                    user.getOffHandStack().setDamage(offhandMaxDamage);
                }
            }
        }
    }

    public static boolean isEmptyDamage(LivingEntity player, Hand hand) {
        boolean isMainhand = hand == Hand.MAIN_HAND;
        ItemStack stack = isMainhand ? player.getMainHandStack() : player.getOffHandStack();
        boolean isDamageable = stack.isDamageable();
        boolean isEmptyDamage = stack.getDamage() >= stack.getMaxDamage();
        return (isDamageable && isEmptyDamage);
    }

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
        int lvl = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            lvl += EnchantmentHelper.getLevel(enchantment, entity.getEquippedStack(slot));
        }
        return lvl;
    }

    public static int getEnchantmentLvl(ItemStack stack, Enchantment enchantment) {
        return EnchantmentHelper.getLevel(enchantment, stack);
    }

}