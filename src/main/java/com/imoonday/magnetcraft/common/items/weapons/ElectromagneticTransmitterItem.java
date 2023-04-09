package com.imoonday.magnetcraft.common.items.weapons;

import com.imoonday.magnetcraft.common.entities.bomb.ElectromagneticPulseBombEntity;
import com.imoonday.magnetcraft.common.tags.FluidTags;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class ElectromagneticTransmitterItem extends Item implements Vanishable {

    public static final Predicate<ItemStack> BATTERY_PROJECTILES = stack -> stack.isOf(ItemRegistries.MAGNETIC_BATTERY);
    public static final int START_USING_TICK = 5;
    protected int repairTick = 0;

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.ELECTROMAGNETIC_TRANSMITTER_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> itemStack.isDamaged() ? 0.0F : 1.0F);
        ModelPredicateProviderRegistry.register(ItemRegistries.ELECTROMAGNETIC_TRANSMITTER_ITEM, new Identifier("aiming"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack && !cannotUse(entity, stack) ? 1.0f : 0.0f);
    }

    public ElectromagneticTransmitterItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        String key = stack.getTranslationKey() + ".tooltip";
        tooltip.add(Text.translatable(key).formatted(Formatting.GRAY).formatted(Formatting.BOLD));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        this.onStoppedUsing(stack, world, user, 0);
        return stack;
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) {
            return;
        }
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        if (i < START_USING_TICK) {
            return;
        }
        if (!world.isClient) {
            if (noBattery(player) && stack.isDamaged()) {
                return;
            }
            float speed = MathHelper.clamp(i / 10.0f, 0.5f, 2.0f);
            ElectromagneticPulseBombEntity entity = new ElectromagneticPulseBombEntity(world, user, speed, isExplosive(), stack);
            world.spawnEntity(entity);
            world.playSoundFromEntity(null, entity, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);
            if (!player.getAbilities().creativeMode) {
                if (player.getInventory().containsAny(BATTERY_PROJECTILES)) {
                    ItemStack offHandStack = player.getOffHandStack();
                    if (offHandStack.isOf(ItemRegistries.MAGNETIC_BATTERY)) {
                        offHandStack.decrement(1);
                    } else {
                        for (int j = 0; j < player.getInventory().size(); j++) {
                            ItemStack itemStack = player.getInventory().getStack(j);
                            if (itemStack.isOf(ItemRegistries.MAGNETIC_BATTERY)) {
                                itemStack.decrement(1);
                                break;
                            }
                        }
                    }
                } else {
                    stack.setDamage(stack.getMaxDamage());
                }
            }
        }
        player.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    private static boolean noBattery(PlayerEntity player) {
        return !player.getInventory().containsAny(BATTERY_PROJECTILES);
    }

    protected boolean isExplosive() {
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!itemStack.isDamaged() && user.isSneaking() && !user.getAbilities().creativeMode) {
            itemStack.setDamage(itemStack.getMaxDamage());
            user.getInventory().offerOrDrop(new ItemStack(ItemRegistries.MAGNETIC_BATTERY));
            user.playSoundIfNotSilent(SoundEvents.BLOCK_BEACON_DEACTIVATE);
            return TypedActionResult.success(itemStack);
        }
        if (cannotUse(user, itemStack)) {
            return TypedActionResult.fail(itemStack);
        }
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    protected static boolean cannotUse(LivingEntity user, ItemStack itemStack) {
        return user instanceof PlayerEntity player ? itemStack.isDamaged() && noBattery(player) : itemStack.isDamaged();
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) {
            return;
        }
        if (stack.isDamaged()) {
            if (entity.isSubmergedIn(FluidTags.MAGNETIC_FLUID) && entity.canAttract()) {
                this.repairTick += 3;
            } else if (entity.isAttracting() && entity.canAttract() && (selected || entity instanceof PlayerEntity player && ItemStack.areEqual(player.getOffHandStack(), stack))) {
                this.repairTick++;
            } else {
                if (world.random.nextFloat() < 0.05f) {
                    this.repairTick++;
                }
            }
            int repairRequiredTick = 3 * 20;
            while (this.repairTick >= repairRequiredTick) {
                stack.addDamage(world.random, -1, false);
                this.repairTick -= repairRequiredTick;
            }
        }
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (stack.isDamaged() && otherStack.isOf(ItemRegistries.MAGNETIC_BATTERY) && clickType == ClickType.RIGHT) {
            otherStack.decrement(1);
            stack.setDamage(0);
            player.playSoundIfNotSilent(SoundEvents.BLOCK_BEACON_ACTIVATE);
            return true;
        }
        return false;
    }
}