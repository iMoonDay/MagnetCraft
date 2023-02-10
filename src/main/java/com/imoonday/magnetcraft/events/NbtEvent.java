package com.imoonday.magnetcraft.events;

import com.imoonday.magnetcraft.MagnetCraft;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NbtEvent {
    public static void enabledSwitch(World world, PlayerEntity player, Hand hand) {

        boolean mainhand = player.getMainHandStack().getOrCreateNbt().getBoolean("enabled");
        boolean offhand = player.getOffHandStack().getOrCreateNbt().getBoolean("enabled");
        boolean client = world.isClient;

        NbtCompound nbt = new NbtCompound();

        if (hand == Hand.MAIN_HAND) {
            nbt.putBoolean("enabled", !mainhand);
            nbt.putInt("Damage", player.getMainHandStack().getDamage());
            player.getMainHandStack().setNbt(nbt);
            mainhand = player.getMainHandStack().getOrCreateNbt().getBoolean("enabled");
            if (mainhand) {
                if (client) player.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 1, 1);
                if (!client && MagnetCraft.TEST_MODE) player.sendMessage(Text.literal("[调试] 主手磁铁:开"));
            }
            if (!mainhand) {
                if (client) player.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1, 1);
                if (!client && MagnetCraft.TEST_MODE) player.sendMessage(Text.literal("[调试] 主手磁铁:关"));
            }
//            if (!client) player.sendMessage(Text.literal(String.valueOf(player.getMainHandStack().getOrCreateNbt().getBoolean("enabled"))));
        } else {
            nbt.putBoolean("enabled", !offhand);
            nbt.putInt("Damage", player.getOffHandStack().getDamage());
            player.getOffHandStack().setNbt(nbt);
            offhand = player.getOffHandStack().getOrCreateNbt().getBoolean("enabled");
            if (offhand) {
                if (client) player.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 1, 1);
                if (!client && MagnetCraft.TEST_MODE) player.sendMessage(Text.literal("[调试] 副手磁铁:开"));
            }
            if (!offhand) {
                if (client) player.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1, 1);
                if (!client && MagnetCraft.TEST_MODE) player.sendMessage(Text.literal("[调试] 副手磁铁:关"));
            }
        }
    }

    public static void enabledCheck(World world, PlayerEntity player, int slot) {

        boolean client = world.isClient;
        boolean hasEnabled = player.getInventory().getStack(slot).getOrCreateNbt().contains("enabled");

        if (!hasEnabled) {
            NbtCompound nbt = new NbtCompound();
            nbt.putBoolean("enabled", true);
            player.getInventory().getStack(slot).setNbt(nbt);
            if (!client && MagnetCraft.TEST_MODE)
                player.sendMessage(Text.literal("[调试] 背包磁铁:初始化"));
        }
    }

    public static void enabledSet(ItemStack stack) {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("enabled", true);
        stack.setNbt(nbt);
    }

    public static void addDamage(PlayerEntity user, Hand hand, int damage) {

        boolean creative = user.isCreative();
        boolean mainhand = hand == Hand.MAIN_HAND;
        boolean offhand = hand == Hand.OFF_HAND;
        boolean mainhandDamageable = user.getMainHandStack().isDamageable();
        boolean offhandDamageable = user.getOffHandStack().isDamageable();

        int mainhandDamage = user.getMainHandStack().getDamage();
        int offhandDamage = user.getOffHandStack().getDamage();
        int mainhandMaxDamage = user.getMainHandStack().getMaxDamage();
        int offhandMaxDamage = user.getOffHandStack().getMaxDamage();
        int mainhandSetDamage = mainhandDamage + damage;
        int offhandSetDamage = mainhandMaxDamage + damage;

        if (!creative && mainhand && mainhandDamageable) {
            user.getMainHandStack().setDamage(mainhandSetDamage);
            if (mainhandDamage > mainhandMaxDamage) {
                user.getMainHandStack().setDamage(mainhandMaxDamage);
            }
        }
        if (!creative && offhand && offhandDamageable) {
            user.getOffHandStack().setDamage(offhandSetDamage);
            if (offhandDamage > offhandMaxDamage) {
                user.getOffHandStack().setDamage(offhandMaxDamage);
            }
        }

    }

    public static boolean checkEmptyDamage(LivingEntity player, Hand hand) {

        boolean mainhand = hand == Hand.MAIN_HAND;
        boolean offhand = hand == Hand.OFF_HAND;

        boolean mainhandDamageable = player.getMainHandStack().isDamageable();
        boolean offhandDamageable = player.getOffHandStack().isDamageable();

        boolean mainhandEmptyDamage = player.getMainHandStack().getDamage() >= player.getMainHandStack().getMaxDamage();
        boolean offhandEmptyDamage = player.getOffHandStack().getDamage() >= player.getOffHandStack().getMaxDamage();

        return (mainhand && mainhandDamageable && mainhandEmptyDamage)
                || (offhand && offhandDamageable && offhandEmptyDamage);
    }

    public static boolean hasEnchantment(LivingEntity entity, @Nullable EquipmentSlot equipmentSlot, String id) {
        return getEnchantmentLvl(entity, equipmentSlot, id) > 0;
    }

    public static int getEnchantmentLvl(LivingEntity entity, @Nullable EquipmentSlot equipmentSlot, String id) {

        short lvl = 0;

        if (equipmentSlot == null) {

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                Optional<Short> lvlCount = entity
                        .getEquippedStack(slot)
                        .getEnchantments()
                        .parallelStream().map(i -> (NbtCompound) i)
                        .filter(i -> i.getString("id")
                                .equals(id))
                        .map(i -> i.getShort("lvl"))
                        .findFirst();
                if (lvlCount.isPresent()) lvl += lvlCount.get();
            }

        } else {

            Optional<Short> lvlCount = entity
                    .getEquippedStack(equipmentSlot)
                    .getEnchantments()
                    .parallelStream().map(i -> (NbtCompound) i)
                    .filter(i -> i.getString("id")
                            .equals(id))
                    .map(i -> i.getShort("lvl"))
                    .findFirst();
            if (lvlCount.isPresent()) lvl = lvlCount.get();
        }

        return lvl;
    }
}