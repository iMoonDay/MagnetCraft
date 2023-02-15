package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NbtClassMethod {

    public static void enabledSwitch(World world, PlayerEntity player, Hand hand) {

        boolean mainhand = player.getMainHandStack().getOrCreateNbt().getBoolean("enabled");
        boolean offhand = player.getOffHandStack().getOrCreateNbt().getBoolean("enabled");
        boolean client = world.isClient;
        boolean display = AutoConfig.getConfigHolder(ModConfig.class).getConfig().displayActionBar;
        double dis = ModConfig.getConfig().value.creatureMagnetAttractDis;

        if (hand == Hand.MAIN_HAND) {
            player.getMainHandStack().getOrCreateNbt().putBoolean("enabled", !mainhand);
            mainhand = player.getMainHandStack().getOrCreateNbt().getBoolean("enabled");
            if (mainhand) {
                if (client) {
                    player.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 1, 1);
                }
                if (display) {
                    MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("主手磁铁:开"), false);
                }
            }
            if (!mainhand) {
                if (client) {
                    player.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1, 1);
                }
                if (display) {
                    MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("主手磁铁:关"), false);
                }
                if (player.getMainHandStack().isOf(ItemRegistries.CREATURE_MAGNET_ITEM)) {
                    player.getWorld().getOtherEntities(player, new Box(
                                            player.getPos().getX() + dis,
                                            player.getPos().getY() + dis,
                                            player.getPos().getZ() + dis,
                                            player.getPos().getX() - dis,
                                            player.getPos().getY() - dis,
                                            player.getPos().getZ() - dis),
                                    e -> (e.getScoreboardTags().contains(player.getEntityName())
                                            && e instanceof LivingEntity && e.distanceTo(player) <= dis))
                            .forEach(e -> e.removeScoreboardTag(player.getEntityName()));
                }
            }
        } else {
            player.getOffHandStack().getOrCreateNbt().putBoolean("enabled", !offhand);
            offhand = player.getOffHandStack().getOrCreateNbt().getBoolean("enabled");
            if (offhand) {
                if (client) {
                    player.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 1, 1);
                }
                if (display) {
                    MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("副手磁铁:开"), false);
                }
            }
            if (!offhand) {
                if (client) {
                    player.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1, 1);
                }
                if (display) {
                    MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("副手磁铁:开"), false);
                }
                if (player.getOffHandStack().isOf(ItemRegistries.CREATURE_MAGNET_ITEM)) {
                    player.getWorld().getOtherEntities(player, new Box(
                                            player.getPos().getX() + dis,
                                            player.getPos().getY() + dis,
                                            player.getPos().getZ() + dis,
                                            player.getPos().getX() - dis,
                                            player.getPos().getY() - dis,
                                            player.getPos().getZ() - dis),
                                    e -> (e.getScoreboardTags().contains(player.getEntityName())
                                            && e instanceof LivingEntity && e.distanceTo(player) <= dis))
                            .forEach(e -> e.removeScoreboardTag(player.getEntityName()));
                }
            }
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

        boolean creative = false;
        boolean mainhand = hand == Hand.MAIN_HAND;
        boolean offhand = hand == Hand.OFF_HAND;
        boolean mainhandDamageable = user.getMainHandStack().isDamageable();
        boolean offhandDamageable = user.getOffHandStack().isDamageable();

        int mainhandDamage = user.getMainHandStack().getDamage();
        int offhandDamage = user.getOffHandStack().getDamage();
        int mainhandMaxDamage = user.getMainHandStack().getMaxDamage();
        int offhandMaxDamage = user.getOffHandStack().getMaxDamage();
        int mainhandSetDamage = mainhandDamage + damage;
        int offhandSetDamage = offhandDamage + damage;

        if (user instanceof PlayerEntity) {
            creative = ((PlayerEntity) user).isCreative();
        }

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