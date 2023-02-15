package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.NbtClassMethod;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagnetControllerItem extends Item {

    public MagnetControllerItem(Settings settings) {
        super(settings);
    }

    boolean ClientOFF = false;
    boolean ServerOFF = false;

    public static void register() {
        ModelPredicateProviderRegistry.register(ItemRegistries.MAGNET_CONTROLLER_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {

            if (itemStack.getNbt() == null || !itemStack.getNbt().contains("enabled")) return 0.0F;
            return itemStack.getOrCreateNbt().getBoolean("enabled") ? 1.0F : 0.0F;

        });
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        stack.getOrCreateNbt().putBoolean("enabled",true);
        return stack;
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
        useTask(user, hand, true);
        return super.use(world, user, hand);
    }

    public static void useTask(PlayerEntity user, @Nullable Hand hand, boolean selected) {

        boolean display = ModConfig.getConfig().displayActionBar;

        boolean creative = user.isCreative();

        boolean mainhand = hand == Hand.MAIN_HAND;
        boolean offhand = hand == Hand.OFF_HAND;

        boolean mainhandDamageable = user.getMainHandStack().isDamageable();
        boolean offhandDamageable = user.getOffHandStack().isDamageable();

        boolean mainhandEmptyDamage = user.getMainHandStack().getDamage() >= user.getMainHandStack().getMaxDamage();
        boolean offhandEmptyDamage = user.getOffHandStack().getDamage() >= user.getOffHandStack().getMaxDamage();

        boolean hasEffect = user.getActiveStatusEffects().containsKey(EffectRegistries.DEGAUSSING_EFFECT);

        if (user.isSneaking() && selected) {

            if ((!creative)
                    && (mainhand && mainhandDamageable && mainhandEmptyDamage)
                    || (offhand && offhandDamageable && offhandEmptyDamage)
            ) return;

            NbtClassMethod.addDamage(user, hand, 1);

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
                if (display)
                    MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("所有磁铁:开"), false);
            } else {
                user.addScoreboardTag("MagnetOFF");
                user.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1, 1);
                if (display)
                    MinecraftClient.getInstance().inGameHud.setOverlayMessage(Text.literal("所有磁铁:关"), false);
            }
            user.getItemCooldownManager().set(ItemRegistries.MAGNET_CONTROLLER_ITEM, 20);
            register();
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);

        boolean client = user.getWorld().isClient;

        if (client) {
            ClientOFF = user.getScoreboardTags().contains("MagnetOFF");
        }
        if (!client) {
            ServerOFF = user.getScoreboardTags().contains("MagnetOFF");
        }
        if (ClientOFF != ServerOFF) {
            if (client) {
                if (ServerOFF) {
                    user.addScoreboardTag("MagnetOFF");
                } else {
                    user.removeScoreboardTag("MagnetOFF");
                }
            }
        }
        boolean enabled = !user.getScoreboardTags().contains("MagnetOFF");
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("enabled", enabled);
        stack.setNbt(nbt);

    }
}

