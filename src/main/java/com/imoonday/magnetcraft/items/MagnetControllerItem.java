package com.imoonday.magnetcraft.items;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.events.NbtEvent;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class MagnetControllerItem extends Item {
    public MagnetControllerItem(Settings settings) {
        super(settings);
    }

    public static void register() {
        ModelPredicateProviderRegistry.register(MagnetCraft.MAGNET_CONTROLLER_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (livingEntity == null) return 1.0F;
            return livingEntity.getScoreboardTags().contains("MagnetOFF") ? 0.0F : 1.0F;
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

        boolean creative = user.isCreative();

        boolean mainhand = hand == Hand.MAIN_HAND;
        boolean offhand = hand == Hand.OFF_HAND;

        boolean mainhandDamageable = user.getMainHandStack().isDamageable();
        boolean offhandDamageable = user.getOffHandStack().isDamageable();

        boolean mainhandEmptyDamage = user.getMainHandStack().getDamage() >= user.getMainHandStack().getMaxDamage();
        boolean offhandEmptyDamage = user.getOffHandStack().getDamage() >= user.getOffHandStack().getMaxDamage();

        boolean hasEffect = user.getActiveStatusEffects().containsKey(MagnetCraft.DEGAUSSING_EFFECT);

        boolean server = !world.isClient;

        if (user.isSneaking()) {

            if ((!creative)
                    && (mainhand && mainhandDamageable && mainhandEmptyDamage)
                    || (offhand && offhandDamageable && offhandEmptyDamage)
            ) return super.use(world, user, hand);

            NbtEvent.addDamage(user, hand, 1);

            if (hasEffect) {
                user.removeStatusEffect(MagnetCraft.DEGAUSSING_EFFECT);
                if (!server) user.playSound(SoundEvents.ENTITY_WANDERING_TRADER_DRINK_MILK, 1, 1);
                user.getItemCooldownManager().set(this, 20);
            } else {
                user.addStatusEffect(new StatusEffectInstance(
                        MagnetCraft.DEGAUSSING_EFFECT, 60 * 20, 0, true, false, true));
                if (!server) user.playSound(SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION, 1, 1);
                user.getItemCooldownManager().set(this, 6 * 20);
            }
        } else {

            if (user.getScoreboardTags().contains("MagnetOFF")) {
                user.removeScoreboardTag("MagnetOFF");
                if (!server) user.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 1, 1);
                if (server && MagnetCraft.TEST_MODE) user.sendMessage(Text.literal("[调试] 所有磁铁:开"));
            } else {
                user.addScoreboardTag("MagnetOFF");
                if (!server) user.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1, 1);
                if (server && MagnetCraft.TEST_MODE) user.sendMessage(Text.literal("[调试] 所有磁铁:关"));
            }
            user.getItemCooldownManager().set(this, 20);
        }
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return ActionResult.PASS;
    }
}