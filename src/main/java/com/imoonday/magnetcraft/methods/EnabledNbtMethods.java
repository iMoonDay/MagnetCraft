package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class EnabledNbtMethods {

    public static void enabledSwitch(World world, PlayerEntity player, Hand hand) {
        boolean isMainhand = hand == Hand.MAIN_HAND;
        boolean client = world.isClient;
        boolean display = AutoConfig.getConfigHolder(ModConfig.class).getConfig().displayActionBar;
        boolean enableSneakToSwitch = ModConfig.getConfig().enableSneakToSwitch;
        double dis = ModConfig.getConfig().value.creatureMagnetAttractDis;
        ItemStack stack = player.getStackInHand(hand);
        Text message;
        SoundEvent sound;
        if (!enableSneakToSwitch) {
            return;
        }
        boolean enabled = stack.getOrCreateNbt().getBoolean("Enable");
        stack.getOrCreateNbt().putBoolean("Enable", !enabled);
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
        if (stack.getNbt() == null || !stack.getNbt().contains("Enable")) {
            enabledSet(stack);
        }
    }

    public static void enabledSet(ItemStack stack) {
        stack.getOrCreateNbt().putBoolean("Enable", true);
    }

}