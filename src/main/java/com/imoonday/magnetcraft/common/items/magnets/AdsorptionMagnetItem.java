package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.api.MagnetCraftEntity;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * @author iMoonDay
 */
public class AdsorptionMagnetItem extends Item {

    public static final String CURRENT_ENTITY = "CurrentEntity";

    public AdsorptionMagnetItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.ADSORPTION_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> livingEntity instanceof PlayerEntity player && player.getItemCooldownManager().isCoolingDown(ItemRegistries.ADSORPTION_MAGNET_ITEM) ? 0.0F : itemStack.isBroken() ? 0.0F : 1.0F);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.translatable("item.magnetcraft.adsorption_magnet.tooltip.9")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("item.magnetcraft.adsorption_magnet.tooltip.10")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(BlockRegistries.MAGNET_BLOCK.asItem());
    }

    @Override
    public boolean hasRecipeRemainder() {
        return true;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return new ItemStack(ItemRegistries.ADSORPTION_MAGNET_CRAFTING_MODULE_ITEM);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return removeOrGet(world, user, hand);
    }

    @NotNull
    private static TypedActionResult<ItemStack> removeOrGet(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking()) {
            return removeEntityUuid(user, hand);
        } else {
            getEntityCount(world, user);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    private static void getEntityCount(World world, PlayerEntity user) {
        int i = world.getOtherEntities(null, user.getBoundingBox().expand(30), entity -> (entity.isAdsorbedByBlock() || entity.isAdsorbedByEntity())).size();
        if (!user.world.isClient) {
            user.sendMessage(Text.translatable("item.magnetcraft.adsorption_magnet.tooltip.7", i));
        }
    }

    private static TypedActionResult<ItemStack> removeEntityUuid(PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(CURRENT_ENTITY)) {
            nbt.remove(CURRENT_ENTITY);
            if (!user.world.isClient) {
                user.sendMessage(Text.translatable("item.magnetcraft.adsorption_magnet.tooltip.8"));
            }
            user.getInventory().markDirty();
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return setBlockPos(context);
    }

    @NotNull
    private static ActionResult setBlockPos(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        Hand hand = context.getHand();
        if (player == null || !player.isSneaking() || stack.getNbt() == null || !stack.getNbt().contains(CURRENT_ENTITY) || stack.isBroken() || player.world.isClient) {
            return ActionResult.PASS;
        }
        Entity entity = ((ServerWorld) player.world).getEntity(stack.getNbt().getUuid(CURRENT_ENTITY));
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setAdsorptionBlockPos(pos, false);
            int dis = (int) livingEntity.getPos().distanceTo(pos.toCenterPos());
            stack.getNbt().remove(CURRENT_ENTITY);
            int randomDamage = player.getRandom().nextBetween(1, Math.max(dis, 1));
            IntStream.rangeClosed(1, randomDamage).forEach(i -> player.addDamage(hand, 1, true));
            player.sendMessage(Text.translatable("item.magnetcraft.adsorption_magnet.tooltip.6"));
            player.getInventory().markDirty();
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return setOrGet(user, entity, hand);
    }

    @NotNull
    private static ActionResult setOrGet(PlayerEntity user, LivingEntity entity, Hand hand) {
        ItemStack stackInHand = user.getStackInHand(hand);
        if (user.isSneaking()) {
            ActionResult result = setOrRemove(user, entity, hand, stackInHand);
            if (result != null) {
                return result;
            }
        } else {
            return getEntityUuid(user, entity, stackInHand);
        }
        return ActionResult.PASS;
    }

    @NotNull
    private static ActionResult getEntityUuid(PlayerEntity user, LivingEntity entity, ItemStack stackInHand) {
        if (!user.world.isClient) {
            if (entity instanceof PlayerEntity) {
                user.sendMessage(Text.translatable("item.magnetcraft.adsorption_magnet.tooltip.2"));
            } else {
                UUID id = entity.getUuid();
                stackInHand.getOrCreateNbt().putUuid(CURRENT_ENTITY, id);
                user.getInventory().markDirty();
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20, 0, false, false, false));
                user.sendMessage(Text.translatable("item.magnetcraft.adsorption_magnet.tooltip.1"));
            }
        }
        user.getInventory().markDirty();
        return ActionResult.SUCCESS;
    }

    @Nullable
    private static ActionResult setOrRemove(PlayerEntity user, LivingEntity entity, Hand hand, ItemStack stackInHand) {
        if (stackInHand.isBroken()) {
            return ActionResult.PASS;
        }
        if (!user.world.isClient) {
            if ((stackInHand.getNbt() == null || !stackInHand.getNbt().contains(CURRENT_ENTITY) || stackInHand.getNbt().getUuid(CURRENT_ENTITY).equals(entity.getUuid())) && !(entity instanceof PlayerEntity)) {
                if (entity.isAdsorbedByEntity() || entity.isAdsorbedByBlock()) {
                    entity.setAdsorbedByEntity(false);
                    entity.setAdsorbedByBlock(false);
                    if (stackInHand.getNbt() != null && stackInHand.getNbt().contains(CURRENT_ENTITY)) {
                        stackInHand.getNbt().remove(CURRENT_ENTITY);
                    }
                }
                user.sendMessage(Text.translatable("item.magnetcraft.adsorption_magnet.tooltip.5"));
                user.getInventory().markDirty();
                return ActionResult.SUCCESS;
            }
            Entity currentEntity = ((ServerWorld) user.world).getEntity(stackInHand.getNbt().getUuid(CURRENT_ENTITY));
            if (currentEntity != null) {
                currentEntity.setAdsorptionEntityId(entity.getUuid(), false);
                int dis = (int) currentEntity.getPos().distanceTo(entity.getPos());
                if (!user.world.isClient) {
                    stackInHand.getNbt().remove(CURRENT_ENTITY);
                    int randomDamage = user.getRandom().nextBetween(1, Math.max(dis, 1));
                    IntStream.rangeClosed(1, randomDamage).forEach(i -> user.addDamage(hand, 1, true));
                    user.sendMessage(Text.translatable("item.magnetcraft.adsorption_magnet.tooltip.4"));
                }
                user.getInventory().markDirty();
            } else {
                user.sendMessage(Text.translatable("item.magnetcraft.adsorption_magnet.tooltip.3"));
            }
        }
        user.getInventory().markDirty();
        return null;
    }

    public static void tickCheck(ServerWorld world) {
        List<? extends LivingEntity> entitiesAdsorbedByEntity = world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), MagnetCraftEntity::isAdsorbedByEntity);
        List<? extends LivingEntity> entitiesAdsorbedByBlock = world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), MagnetCraftEntity::isAdsorbedByBlock);
        entitiesAdsorbedByEntity.forEach(entity -> {
            Entity targetEntity = world.getEntity(entity.getAdsorptionEntityId());
            if (targetEntity instanceof LivingEntity livingEntity) {
                CreatureMagnetItem.followAttractOwner(entity, livingEntity, false);
            }
        });
        entitiesAdsorbedByBlock.forEach(entity -> {
            BlockPos pos = entity.getAdsorptionBlockPos();
            adsorbToBlockPos(entity, pos);
        });
    }

    private static void adsorbToBlockPos(LivingEntity entity, BlockPos blockPos) {
        if (entity.world.isClient) {
            return;
        }
        if (entity.world.getBlockState(blockPos).isAir()) {
            entity.setAdsorbedByBlock(false);
            return;
        }
        Vec3d pos = blockPos.toCenterPos();
        Vec3d vec = pos.subtract(entity.getPos()).multiply(0.05);
        if (entity.horizontalCollision) {
            vec = entity.getPos().isInRange(pos, 1) || entity.getBoundingBox().intersects(new Box(blockPos).expand(0.5, 0, 0.5)) ? Vec3d.ZERO : vec.multiply(1, 0, 1).add(0, 0.25, 0);
        }
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 2, 0, false, false));
        if (!entity.getBlockPos().equals(blockPos.up())) {
            entity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, pos);
        }
        entity.setVelocity(vec);
        if (!entity.isOnGround()) {
            entity.setIgnoreFallDamage(true);
        }
        PlayerLookup.tracking(entity).forEach(serverPlayer -> serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity)));
    }

}
