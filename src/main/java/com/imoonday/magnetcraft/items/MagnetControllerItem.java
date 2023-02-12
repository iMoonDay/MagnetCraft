package com.imoonday.magnetcraft.items;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.events.NbtEvent;
import com.imoonday.magnetcraft.registries.EffectRegistries;
import com.imoonday.magnetcraft.registries.ItemRegistries;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagnetControllerItem extends Item {
    public MagnetControllerItem(Settings settings) {
        super(settings);
    }

    public static void register() {
        ModelPredicateProviderRegistry.register(ItemRegistries.MAGNET_CONTROLLER_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {

            if (livingEntity == null || !itemStack.hasNbt()) return 0.0F;
            return itemStack.getOrCreateNbt().getBoolean("enabled") ? 1.0F : 0.0F;

        });
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.magnetcraft.magnet_controller.tooltip.1")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.magnetcraft.magnet_controller.tooltip.2")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        useTask(world, user, hand,true);
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return ActionResult.PASS;
    }

    public static void useTask(World world, PlayerEntity user, @Nullable Hand hand, boolean selected) {

        boolean creative = user.isCreative();

        boolean mainhand = hand == Hand.MAIN_HAND;
        boolean offhand = hand == Hand.OFF_HAND;

        boolean mainhandDamageable = user.getMainHandStack().isDamageable();
        boolean offhandDamageable = user.getOffHandStack().isDamageable();

        boolean mainhandEmptyDamage = user.getMainHandStack().getDamage() >= user.getMainHandStack().getMaxDamage();
        boolean offhandEmptyDamage = user.getOffHandStack().getDamage() >= user.getOffHandStack().getMaxDamage();

        boolean hasEffect = user.getActiveStatusEffects().containsKey(EffectRegistries.DEGAUSSING_EFFECT);

        boolean server = !user.world.isClient;

        if (user.isSneaking()&&selected) {

            if ((!creative)
                    && (mainhand && mainhandDamageable && mainhandEmptyDamage)
                    || (offhand && offhandDamageable && offhandEmptyDamage)
            ) return;

            NbtEvent.addDamage(user, hand, 1);

            if (hasEffect) {
                user.removeStatusEffect(EffectRegistries.DEGAUSSING_EFFECT);
                user.playSound(SoundEvents.ENTITY_WANDERING_TRADER_DRINK_MILK, 1, 1);
                user.getItemCooldownManager().set(ItemRegistries.MAGNET_CONTROLLER_ITEM, 20);
            } else {
                user.addStatusEffect(new StatusEffectInstance(
                        EffectRegistries.DEGAUSSING_EFFECT, 60 * 20, 0, true, false, true));
                user.playSound(SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION, 1, 1);
                user.getItemCooldownManager().set(ItemRegistries.MAGNET_CONTROLLER_ITEM, 6 * 20);
            }
        } else {

            if (user.getScoreboardTags().contains("MagnetOFF")) {
                user.removeScoreboardTag("MagnetOFF");
                user.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 1, 1);
                if (server && MagnetCraft.TEST_MODE) user.sendMessage(Text.literal("[调试] 所有磁铁:开"));
            } else {
                user.addScoreboardTag("MagnetOFF");
                user.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1, 1);
                if (server && MagnetCraft.TEST_MODE) user.sendMessage(Text.literal("[调试] 所有磁铁:关"));
            }
            user.getItemCooldownManager().set(ItemRegistries.MAGNET_CONTROLLER_ITEM, 20);
            register();
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);

            NbtCompound nbt = new NbtCompound();
            nbt.putBoolean("enabled",!user.getScoreboardTags().contains("MagnetOFF"));
            stack.setNbt(nbt);

    }
}

