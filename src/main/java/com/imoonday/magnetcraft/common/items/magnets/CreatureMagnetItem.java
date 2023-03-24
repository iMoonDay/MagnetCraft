package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.api.AbstractSwitchableItem;
import com.imoonday.magnetcraft.config.ModConfig;
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
import java.util.UUID;

/**
 * @author iMoonDay
 */
public class CreatureMagnetItem extends AbstractSwitchableItem {

    public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final String ENABLE = "Enable";
    public static final String USED_TICK = "UsedTick";

    public CreatureMagnetItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.CREATURE_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> itemStack.getNbt() == null || !itemStack.getNbt().contains(ENABLE) ? 0.0F : itemStack.getOrCreateNbt().getBoolean(ENABLE) ? 1.0F : 0.0F);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        usedTickSet(stack);
        return stack;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.GOLDEN_APPLE);
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
            MagnetCraft.CooldownMethods.setCooldown(user, user.getStackInHand(hand), 20);
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
        boolean enable = stack.getOrCreateNbt().getBoolean(ENABLE);
        boolean hasAttractOwner = !entity.getAttractOwner().equals(CreatureMagnetItem.EMPTY_UUID);
        if ((((!sneaking && !reversal) || (sneaking && reversal)) || !sneakToSwitch) && enable && !cooling && entityCanAttract) {
            if (hasAttractOwner && !entity.isAdsorbedByEntity() && !entity.isAdsorbedByBlock()) {
                entity.setAttractOwner(CreatureMagnetItem.EMPTY_UUID);
            } else {
                entity.setAdsorbedByEntity(false);
                entity.setAdsorbedByBlock(false);
                entity.setAttractOwner(user.getUuid());
            }
            if (!creative) {
                MagnetCraft.CooldownMethods.setCooldown(user, stack, 20);
            }
        }
        return ActionResult.SUCCESS;
    }

    public static void followAttractOwner(LivingEntity followingEntity, LivingEntity attractingEntity, boolean usingMagnet) {
        if (attractingEntity.world.isClient || followingEntity.world.isClient || !attractingEntity.isAlive() || !followingEntity.isAlive()) {
            return;
        }
        Vec3d vec = attractingEntity.getPos().subtract(followingEntity.getPos()).multiply(0.05);
        if (followingEntity.horizontalCollision) {
            vec = vec.multiply(1, 0, 1).add(0, 0.25, 0);
        }
        followingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 2, 0, false, false));
        followingEntity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, attractingEntity.getEyePos());
        if (followingEntity.getPos().isInRange(attractingEntity.getPos(), 1) || followingEntity.collidesWith(attractingEntity) || followingEntity.getBoundingBox().intersects(attractingEntity.getBoundingBox().expand(0.1))) {
            vec = Vec3d.ZERO;
        }
        if (attractingEntity.getVelocity().y < -0.1 && followingEntity.getPos().y > attractingEntity.getY()) {
            vec = vec.multiply(1, 0, 1).add(0, attractingEntity.getVelocity().y, 0);
        } else if (attractingEntity.getVelocity().y == 0 && followingEntity.getPos().y > attractingEntity.getY()) {
            vec = vec.multiply(1, 0, 1).add(0, -0.75, 0);
        }
        followingEntity.setVelocity(vec);
        if (!followingEntity.isOnGround()) {
            followingEntity.setIgnoreFallDamage(true);
        }
        PlayerLookup.tracking(followingEntity).forEach(serverPlayer -> serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(followingEntity)));
        if (attractingEntity instanceof PlayerEntity player && usingMagnet && !player.isSpectator() && !(player.isCreative())) {
            if (player.getMainHandStack().isOf(ItemRegistries.CREATURE_MAGNET_ITEM)) {
                ItemStack stack = player.getMainHandStack();
                int tick = stack.getOrCreateNbt().getInt(USED_TICK);
                stack.getOrCreateNbt().putInt(USED_TICK, ++tick);
            } else {
                ItemStack stack = player.getOffHandStack();
                int tick = stack.getOrCreateNbt().getInt(USED_TICK);
                stack.getOrCreateNbt().putInt(USED_TICK, ++tick);
            }
        }
    }


    public static void usedTickCheck(ItemStack stack) {
        if (stack.getNbt() == null || !stack.getNbt().contains(USED_TICK)) {
            usedTickSet(stack);
        }
    }

    public static void usedTickSet(ItemStack stack) {
        stack.getOrCreateNbt().putInt(USED_TICK, 0);
    }

    @Override
    public int getEnchantability() {
        return 16;
    }

    public static void followingCheck(LivingEntity entity) {
        double creatureDis = ModConfig.getValue().creatureMagnetAttractDis;
        PlayerEntity playerByUuid = entity.world.getPlayerByUuid(entity.getAttractOwner());
        boolean hasAttractOwner = !entity.getAttractOwner().equals(CreatureMagnetItem.EMPTY_UUID);
        if (!(entity instanceof PlayerEntity) && hasAttractOwner && playerByUuid != null && entity.getPos().isInRange(playerByUuid.getPos(), creatureDis) && playerByUuid.canAttract()) {
            boolean attractOwnerMainhandCreature = playerByUuid.getMainHandStack().isOf(ItemRegistries.CREATURE_MAGNET_ITEM) && playerByUuid.getMainHandStack().getNbt() != null && playerByUuid.getMainHandStack().getNbt().getBoolean(ENABLE) && !MagnetCraft.DamageMethods.isEmptyDamage(playerByUuid, Hand.MAIN_HAND);
            boolean attractOwnerOffhandCreature = playerByUuid.getOffHandStack().isOf(ItemRegistries.CREATURE_MAGNET_ITEM) && playerByUuid.getOffHandStack().getNbt() != null && playerByUuid.getOffHandStack().getNbt().getBoolean(ENABLE) && !MagnetCraft.DamageMethods.isEmptyDamage(playerByUuid, Hand.OFF_HAND);
            if ((attractOwnerMainhandCreature || attractOwnerOffhandCreature) && !entity.isAdsorbedByEntity() && !entity.isAdsorbedByBlock()) {
                CreatureMagnetItem.followAttractOwner(entity, playerByUuid, true);
                entity.setFollowing(true);
                return;
            }
        }
        entity.setFollowing(false);
    }

}
