package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.api.SwitchableItem;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.CooldownMethods;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class CreatureMagnetItem extends SwitchableItem {

    public CreatureMagnetItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.CREATURE_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (itemStack.getNbt() == null || !itemStack.getNbt().contains("Enable")) return 0.0F;
            return itemStack.getOrCreateNbt().getBoolean("Enable") ? 1.0F : 0.0F;
        });
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        usedTickSet(stack);
        return stack;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.GOLDEN_APPLE) || super.canRepair(stack, ingredient);
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return new ItemStack(ItemRegistries.CREATURE_MAGNET_CRAFTING_MODULE_ITEM);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        usedTickSet(stack);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.magnetcraft.creature_magnet.tooltip.1")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.magnetcraft.creature_magnet.tooltip.2")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        boolean enableSneakToSwitch = ModConfig.getConfig().enableSneakToSwitch;
        boolean rightClickReversal = ModConfig.getConfig().rightClickReversal;
        boolean sneaking = user.isSneaking();
        boolean flying = user.getAbilities().flying;
        if (sneaking && flying) {
            sneaking = false;
        }
        if ((sneaking && !rightClickReversal) || (!sneaking && rightClickReversal)) {
            if (!enableSneakToSwitch) {
                return super.use(world, user, hand);
            }
            enabledSwitch(world, user, hand);
            CooldownMethods.setCooldown(user, user.getStackInHand(hand), 20);
        }
        return super.use(world, user, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        super.inventoryTick(stack, world, user, slot, selected);
        usedTickCheck(stack);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        boolean sneakToSwitch = ModConfig.getConfig().enableSneakToSwitch;
        boolean reversal = ModConfig.getConfig().rightClickReversal;
        boolean sneaking = user.isSneaking();
        boolean cooling = user.getItemCooldownManager().isCoolingDown(this);
        boolean entityCanAttract = !(entity instanceof PlayerEntity) && !(entity instanceof EnderDragonEntity) && !(entity instanceof WitherEntity);
        boolean creative = user.isCreative();
        boolean enable = stack.getOrCreateNbt().getBoolean("Enable");
        if ((((!sneaking && !reversal) || (sneaking && reversal)) || !sneakToSwitch) && enable && !cooling && entityCanAttract) {
            if (!entity.addScoreboardTag(user.getEntityName())) {
                entity.removeScoreboardTag(user.getEntityName());
            }
            if (!creative) {
                CooldownMethods.setCooldown(user, stack, 20);
            }
        }
        return ActionResult.PASS;
    }

    public static void attractCreatures(LivingEntity entity) {
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        int dis = ModConfig.getConfig().value.creatureMagnetAttractDis;
        boolean handingMagnet = entity.getMainHandStack().isOf(ItemRegistries.CREATURE_MAGNET_ITEM) || entity.getOffHandStack().isOf(ItemRegistries.CREATURE_MAGNET_ITEM);
        if (!entity.world.isClient && handingMagnet) {
            boolean entityCanAttract = entity.world.getOtherEntities(null, entity.getBoundingBox().expand(degaussingDis), otherEntity -> (otherEntity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT)) && entity.getPos().isInRange(otherEntity.getPos(), degaussingDis) && !otherEntity.isSpectator()).isEmpty();
            if (entity.getEnable() && entityCanAttract) {
                entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(dis), otherEntity -> (otherEntity.getScoreboardTags().contains(entity.getEntityName()) && otherEntity instanceof LivingEntity && otherEntity.getPos().isInRange(entity.getPos(), dis))).forEach(targetEntity -> {
                    LivingEntity livingEntity = (LivingEntity) targetEntity;
                    Vec3d vec = entity.getPos().subtract(targetEntity.getPos()).multiply(0.05);
                    if (targetEntity.horizontalCollision) {
                        vec = vec.multiply(1, 0, 1).add(0, 0.25, 0);
                    }
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 3 * 20, 0, false, false));
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 10 * 20, 0, false, false));
                    targetEntity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, entity.getEyePos());
                    if (targetEntity.getPos().isInRange(entity.getPos(), 1)) {
                        vec = Vec3d.ZERO;
                    }
                    if (entity.getVelocity().y < 0 && targetEntity.getPos().y > entity.getY()) {
                        vec = vec.multiply(1, 0, 1).add(0, entity.getVelocity().y, 0);
                    } else if (entity.getVelocity().y == 0 && targetEntity.getPos().y > entity.getY()) {
                        vec = vec.multiply(1, 0, 1).add(0, -0.75, 0);
                    }
                    targetEntity.setVelocity(vec);
                    PlayerLookup.tracking(targetEntity).forEach(serverPlayer -> serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(targetEntity)));
                    if (!entity.isSpectator() && !(entity instanceof PlayerEntity player && player.isCreative())) {
                        ItemStack stack;
                        if (entity.getMainHandStack().isOf(ItemRegistries.CREATURE_MAGNET_ITEM)) {
                            stack = entity.getMainHandStack();
                            int tick = stack.getOrCreateNbt().getInt("UsedTick") + 1;
                            stack.getOrCreateNbt().putInt("UsedTick", tick);
                        } else {
                            stack = entity.getOffHandStack();
                            int tick = stack.getOrCreateNbt().getInt("UsedTick") + 1;
                            stack.getOrCreateNbt().putInt("UsedTick", tick);
                        }
                    }
                });
            }
        }
    }

    public static void usedTickCheck(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains("UsedTick")) {
            usedTickSet(stack);
        }
    }

    public static void usedTickSet(ItemStack stack) {
        stack.getOrCreateNbt().putInt("UsedTick", 0);
    }

    @Override
    public int getEnchantability() {
        return 16;
    }

}
