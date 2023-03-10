package com.imoonday.magnetcraft.common.items;

import com.imoonday.magnetcraft.api.FilterableItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.DamageMethods;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

public class MagnetControllerItem extends FilterableItem {

    public MagnetControllerItem(Settings settings) {
        super(settings);
    }

    boolean ClientOFF;
    boolean ServerOFF;

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.MAGNET_CONTROLLER_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (itemStack.getNbt() == null || !itemStack.getNbt().contains("Enable")) return 0.0F;
            return itemStack.getOrCreateNbt().getBoolean("Enable") ? 1.0F : 0.0F;
        });
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.FLINT) || super.canRepair(stack, ingredient);
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
        if (user.getStackInHand(hand).getOrCreateNbt().getBoolean("Filterable")) {
            if (!user.isSneaky()) {
                if (!user.world.isClient) {
                    openScreen(user, hand, this);
                }
            } else {
                useTask(user, hand, true);
            }
        } else {
            useTask(user, hand, true);
        }
        return super.use(world, user, hand);
    }

    public static void useTask(PlayerEntity user, @Nullable Hand hand, boolean selected) {
        boolean rightClickReversal = ModConfig.getConfig().rightClickReversal;
        boolean creative = user.isCreative();
        boolean isEmptyDamage = DamageMethods.isEmptyDamage(user, hand);
        boolean hasEffect = user.getActiveStatusEffects().containsKey(EffectRegistries.DEGAUSSING_EFFECT);
        boolean sneaking = user.isSneaking();
        boolean flying = user.getAbilities().flying;
        if (sneaking && flying) {
            sneaking = false;
        }
        if ((sneaking && !rightClickReversal) || (!sneaking && rightClickReversal) && selected) {
            if (!creative && isEmptyDamage) {
                return;
            }
            DamageMethods.addDamage(user, hand, 1,true);
            if (hasEffect) {
                user.removeStatusEffect(EffectRegistries.DEGAUSSING_EFFECT);
                user.playSound(SoundEvents.ENTITY_WANDERING_TRADER_DRINK_MILK, 1, 1);
                user.getItemCooldownManager().set(ItemRegistries.MAGNET_CONTROLLER_ITEM, 20);
            } else {
                user.addStatusEffect(new StatusEffectInstance(EffectRegistries.DEGAUSSING_EFFECT, 60 * 20, 0, true, false, true));
                user.playSound(SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION, 1, 1);
                user.getItemCooldownManager().set(ItemRegistries.MAGNET_CONTROLLER_ITEM, 6 * 20);
            }
        } else {
            changeMagnetEnable(user);
            user.getItemCooldownManager().set(ItemRegistries.MAGNET_CONTROLLER_ITEM, 20);
            user.getInventory().markDirty();
        }
    }

    public static void changeMagnetEnable(PlayerEntity user) {
        boolean display = ModConfig.getConfig().displayActionBar;
        boolean hasTag = user.getScoreboardTags().contains("MagnetCraft.MagnetOFF");
        boolean isClient = user.world.isClient;
        Text message;
        SoundEvent sound;
        if (hasTag) {
            user.removeScoreboardTag("MagnetCraft.MagnetOFF");
            sound = SoundEvents.BLOCK_BEACON_ACTIVATE;
            message = Text.translatable("text.magnetcraft.message.magnet_on");
        } else {
            user.addScoreboardTag("MagnetCraft.MagnetOFF");
            sound = SoundEvents.BLOCK_BEACON_DEACTIVATE;
            message = Text.translatable("text.magnetcraft.message.magnet_off");
        }
        user.playSound(sound, 1, 1);
        if (display && !isClient) {
            user.sendMessage(message, true);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        boolean client = user.world.isClient;
        if (client) {
            ClientOFF = user.getScoreboardTags().contains("MagnetCraft.MagnetOFF");
        } else {
            ServerOFF = user.getScoreboardTags().contains("MagnetCraft.MagnetOFF");
        }
        if (ClientOFF != ServerOFF) {
            if (client) {
                if (ServerOFF) {
                    user.addScoreboardTag("MagnetCraft.MagnetOFF");
                } else {
                    user.removeScoreboardTag("MagnetCraft.MagnetOFF");
                }
            }
        }
        boolean enabled = !user.getScoreboardTags().contains("MagnetCraft.MagnetOFF");
        stack.getOrCreateNbt().putBoolean("Enable", enabled);
    }
}

