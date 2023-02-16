package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NbtClassMethod {

    public static void enabledSwitch(World world, PlayerEntity player, Hand hand) {
        boolean isMainhand = hand == Hand.MAIN_HAND;
        boolean client = world.isClient;
        boolean display = AutoConfig.getConfigHolder(ModConfig.class).getConfig().displayActionBar;
        boolean enableSneakToSwitch = ModConfig.getConfig().enableSneakToSwitch;
        double dis = ModConfig.getConfig().value.creatureMagnetAttractDis;
        String message;
        SoundEvent sound;
        ItemStack stack;
        if (!enableSneakToSwitch) {
            return;
        }
        if (isMainhand) {
            stack = player.getMainHandStack();
        } else {
            stack = player.getOffHandStack();
        }
        boolean enabled = stack.getOrCreateNbt().getBoolean("enabled");
        stack.getOrCreateNbt().putBoolean("enabled", !enabled);
        enabled = !enabled;
        if (enabled) {
            if (isMainhand) {
                message = "主手磁铁:开";
            } else {
                message = "副手磁铁:开";
            }
            sound = SoundEvents.BLOCK_BEACON_ACTIVATE;
        } else {
            if (isMainhand) {
                message = "主手磁铁:关";
            } else {
                message = "副手磁铁:关";
            }
            sound = SoundEvents.BLOCK_BEACON_DEACTIVATE;
            if (stack.isOf(ItemRegistries.CREATURE_MAGNET_ITEM)) {
                player.getWorld().getOtherEntities(player, new Box(player.getPos().getX() + dis, player.getPos().getY() + dis, player.getPos().getZ() + dis, player.getPos().getX() - dis, player.getPos().getY() - dis, player.getPos().getZ() - dis), e -> (e.getScoreboardTags().contains(player.getEntityName()) && e instanceof LivingEntity && e.distanceTo(player) <= dis)).forEach(e -> e.removeScoreboardTag(player.getEntityName()));
            }
        }
        if (client) {
            player.playSound(sound, 1, 1);
        }
        if (display && !client) {
            ((ServerPlayerEntity) player).networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal(message)));
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
        boolean isMainhand = hand == Hand.MAIN_HAND;
        ItemStack stack;
        if (isMainhand) {
            stack = player.getMainHandStack();
        } else {
            stack = player.getOffHandStack();
        }
        boolean isDamageable = stack.isDamageable();
        boolean isEmptyDamage = stack.getDamage() >= stack.getMaxDamage();
        return (isDamageable && isEmptyDamage);
    }

    public static boolean hasEnchantment(LivingEntity entity, @Nullable EquipmentSlot equipmentSlot, String id) {
        return getEnchantmentLvl(entity, equipmentSlot, id) > 0;
    }

    public static int getEnchantmentLvl(LivingEntity entity, @Nullable EquipmentSlot equipmentSlot, String id) {
        short lvl = 0;
        if (equipmentSlot == null) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                Optional<Short> lvlCount = entity.getEquippedStack(slot).getEnchantments().parallelStream().map(i -> (NbtCompound) i).filter(i -> i.getString("id").equals(id)).map(i -> i.getShort("lvl")).findFirst();
                if (lvlCount.isPresent()) lvl += lvlCount.get();
            }
        } else {
            Optional<Short> lvlCount = entity.getEquippedStack(equipmentSlot).getEnchantments().parallelStream().map(i -> (NbtCompound) i).filter(i -> i.getString("id").equals(id)).map(i -> i.getShort("lvl")).findFirst();
            if (lvlCount.isPresent()) lvl = lvlCount.get();
        }
        return lvl;
    }
}