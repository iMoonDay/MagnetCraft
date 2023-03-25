package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.api.AbstractFilterableItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagnetControllerItem extends AbstractFilterableItem {

    public static final String ENABLE = "Enable";
    public static final String FILTERABLE = "Filterable";

    public MagnetControllerItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.MAGNET_CONTROLLER_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> itemStack.getNbt() == null || !itemStack.getNbt().contains(ENABLE) ? 0.0F : itemStack.getOrCreateNbt().getBoolean(ENABLE) ? 1.0F : 0.0F);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(ItemRegistries.DEMAGNETIZED_POWDER_ITEM) || super.canRepair(stack, ingredient);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.magnetcraft.magnet_controller.tooltip.1")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.magnetcraft.magnet_controller.tooltip.2")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (stack.getOrCreateNbt().getBoolean(FILTERABLE) && !user.isSneaky()) {
            if (!user.world.isClient) {
                openScreen(user, hand, this);
            }
        } else {
            useController(user, hand, true);
        }
        return TypedActionResult.success(stack);
    }

    public static void useController(PlayerEntity user, @Nullable Hand hand, boolean selected) {
        boolean rightClickReversal = ModConfig.getConfig().rightClickReversal;
        boolean sneaking = user.isSneaking();
        if (sneaking && user.getAbilities().flying) {
            sneaking = false;
        }
        if ((sneaking && !rightClickReversal) || (!sneaking && rightClickReversal) && selected) {
            if (!user.isCreative() && user.isBroken(hand)) {
                return;
            }
            user.addDamage(hand, 1, true);
            if (user.hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT)) {
                user.removeStatusEffect(EffectRegistries.DEGAUSSING_EFFECT);
                user.playSound(SoundEvents.ENTITY_WANDERING_TRADER_DRINK_MILK, 1, 1);
                user.setCooldown(user.getStackInHand(hand), 20);
            } else {
                user.addStatusEffect(new StatusEffectInstance(EffectRegistries.DEGAUSSING_EFFECT, 60 * 20, 0, true, false, true));
                user.playSound(SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION, 1, 1);
                user.setCooldown(user.getStackInHand(hand), 6 * 20);
            }
        } else {
            changeMagnetEnable(user);
            if (hand != null) {
                user.setCooldown(user.getStackInHand(hand), 20);
            } else {
                int percent = ModConfig.getValue().coolingPercentage;
                user.getItemCooldownManager().set(ItemRegistries.MAGNET_CONTROLLER_ITEM, 20 * percent / 100);
            }
            user.getInventory().markDirty();
        }
    }

    public static void changeMagnetEnable(PlayerEntity user) {
        boolean display = ModConfig.getConfig().displayActionBar;
        Text message;
        SoundEvent sound;
        if (user.getEnable()) {
            user.setEnable(false);
            sound = SoundEvents.BLOCK_BEACON_DEACTIVATE;
            message = Text.translatable("text.magnetcraft.message.magnet_off");
        } else {
            user.setEnable(true);
            sound = SoundEvents.BLOCK_BEACON_ACTIVATE;
            message = Text.translatable("text.magnetcraft.message.magnet_on");
        }
        user.playSound(sound, 1, 1);
        if (display && !user.world.isClient) {
            user.sendMessage(message, true);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        if (!world.isClient) {
            stack.getOrCreateNbt().putBoolean(ENABLE, user.getEnable());
        }
        if (user instanceof PlayerEntity player) {
            player.getInventory().markDirty();
        }
    }

    @Override
    public int getEnchantability() {
        return 14;
    }

}

