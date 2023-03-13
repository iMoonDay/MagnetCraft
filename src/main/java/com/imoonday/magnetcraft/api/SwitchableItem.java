package com.imoonday.magnetcraft.api;

import com.imoonday.magnetcraft.common.items.magnets.CreatureMagnetItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class SwitchableItem extends Item {
    public SwitchableItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        if (getSwitchable()) {
            enabledSet(stack);
        }
        return stack;
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
        if (getSwitchable()) {
            enabledSet(stack);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);
        if (getSwitchable()) {
            enabledCheck(stack);
        }
    }

    public boolean getSwitchable() {
        return true;
    }

    public static void enabledSwitch(World world, PlayerEntity player, Hand hand) {
        boolean display = AutoConfig.getConfigHolder(ModConfig.class).getConfig().displayActionBar;
        boolean enableSneakToSwitch = ModConfig.getConfig().enableSneakToSwitch;
        double dis = ModConfig.getConfig().value.creatureMagnetAttractDis;
        boolean isMainhand = hand == Hand.MAIN_HAND;
        boolean client = world.isClient;
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
                player.world.getOtherEntities(player, player.getBoundingBox().expand(dis), entity -> (!entity.getAttractOwner().equals(CreatureMagnetItem.EMPTY_UUID) && entity instanceof LivingEntity && entity.getPos().isInRange(player.getPos(), dis))).forEach(entity -> entity.setAttractOwner(CreatureMagnetItem.EMPTY_UUID));
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
