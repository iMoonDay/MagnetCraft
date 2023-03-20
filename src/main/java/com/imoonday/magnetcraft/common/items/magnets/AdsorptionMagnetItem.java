package com.imoonday.magnetcraft.common.items.magnets;

import com.imoonday.magnetcraft.MagnetCraft;
import com.imoonday.magnetcraft.api.MagnetCraftEntity;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
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

import java.util.List;
import java.util.stream.IntStream;

public class AdsorptionMagnetItem extends Item {

    public AdsorptionMagnetItem(Settings settings) {
        super(settings);
    }

    public static void registerClient() {
        ModelPredicateProviderRegistry.register(ItemRegistries.ADSORPTION_MAGNET_ITEM, new Identifier("enabled"), (itemStack, clientWorld, livingEntity, provider) -> {
            if (livingEntity instanceof PlayerEntity player && player.getItemCooldownManager().isCoolingDown(ItemRegistries.ADSORPTION_MAGNET_ITEM)) {
                return 0.0F;
            }
            return MagnetCraft.DamageMethods.isEmptyDamage(itemStack) ? 0.0F : 1.0F;
        });
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if (user.isSneaking()) {
                NbtCompound nbt = user.getStackInHand(hand).getNbt();
                if (nbt != null && nbt.contains("CurrentEntity")) {
                    nbt.remove("CurrentEntity");
                    user.sendMessage(Text.literal("已清除选择"));
                }
            } else {
                int i = world.getOtherEntities(null, user.getBoundingBox().expand(30), entity -> (entity.isAdsorbedByBlock() || entity.isAdsorbedByEntity())).size();
                user.sendMessage(Text.literal("附近有 " + i + " 个正在被吸附的实体"));
            }
        }
        user.getInventory().markDirty();
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        Hand hand = context.getHand();
        if (player == null || !player.isSneaking()) {
            return ActionResult.PASS;
        }
        if (stack.getNbt() == null || !stack.getNbt().contains("CurrentEntity") || MagnetCraft.DamageMethods.isEmptyDamage(stack)) {
            return ActionResult.PASS;
        }
        Entity entity = player.world.getEntityById(stack.getNbt().getInt("CurrentEntity"));
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setAdsorptionBlockPos(pos);
            int dis = (int) livingEntity.getPos().distanceTo(pos.toCenterPos());
            if (!player.world.isClient) {
                stack.getNbt().remove("CurrentEntity");
                int randomDamage = player.getRandom().nextBetween(1, Math.max(dis, 1));
                IntStream.rangeClosed(1, randomDamage).forEach(i -> MagnetCraft.DamageMethods.addDamage(player, hand, 1, true));
                player.sendMessage(Text.literal("已绑定坐标"));
            }
            player.getInventory().markDirty();
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        ItemStack stackInHand = user.getStackInHand(hand);
        if (user.isSneaking()) {
            if (MagnetCraft.DamageMethods.isEmptyDamage(stackInHand)) {
                return ActionResult.PASS;
            }
            if (stackInHand.getNbt() == null || !stackInHand.getNbt().contains("CurrentEntity") || stackInHand.getNbt().getInt("CurrentEntity") == entity.getId()) {
                if (!(entity instanceof PlayerEntity)) {
                    if (entity.isAdsorbedByEntity() || entity.isAdsorbedByBlock()) {
                        entity.setAdsorbedByEntity(false);
                        entity.setAdsorbedByBlock(false);
                        if (stackInHand.getNbt() != null && stackInHand.getNbt().contains("CurrentEntity")) {
                            stackInHand.getNbt().remove("CurrentEntity");
                            user.getInventory().markDirty();
                        }
                    }
                    if (!user.world.isClient) {
                        user.sendMessage(Text.literal("已清除数据"));
                    }
                    return ActionResult.SUCCESS;
                }
            }
            Entity currentEntity = user.world.getEntityById(stackInHand.getNbt().getInt("CurrentEntity"));
            if (currentEntity != null) {
                currentEntity.setAdsorptionEntityId(entity.getUuid());
                int dis = (int) currentEntity.getPos().distanceTo(entity.getPos());
                if (!user.world.isClient) {
                    stackInHand.getNbt().remove("CurrentEntity");
                    int randomDamage = user.getRandom().nextBetween(1, Math.max(dis, 1));
                    IntStream.rangeClosed(1, randomDamage).forEach(i -> MagnetCraft.DamageMethods.addDamage(user, hand, 1, true));
                    user.sendMessage(Text.literal("已绑定实体"));
                }
                user.getInventory().markDirty();
            } else {
                if (!user.world.isClient) {
                    user.sendMessage(Text.literal("未找到选择的实体，请重新选择"));
                }
            }
        } else {
            if (entity instanceof PlayerEntity) {
                if (!user.world.isClient) {
                    user.sendMessage(Text.literal("无法选择玩家"));
                }
            } else {
                int id = entity.getId();
                stackInHand.getOrCreateNbt().putInt("CurrentEntity", id);
                user.getInventory().markDirty();
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20, 0, false, false, false));
                if (!user.world.isClient) {
                    user.sendMessage(Text.literal("已选择实体"));
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
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

    private static void adsorbToBlockPos(LivingEntity adsorbedEntity, BlockPos blockPos) {
        if (adsorbedEntity.world.isClient) {
            return;
        }
        Vec3d pos = blockPos.toCenterPos();
        Vec3d vec = pos.subtract(adsorbedEntity.getPos()).multiply(0.05);
        if (adsorbedEntity.horizontalCollision) {
            vec = adsorbedEntity.getPos().isInRange(pos, 1) || adsorbedEntity.getBoundingBox().intersects(new Box(blockPos).expand(0.5)) ? Vec3d.ZERO : vec.multiply(1, 0, 1).add(0, 0.25, 0);
        }
        adsorbedEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 2, 0, false, false));
        adsorbedEntity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, pos);
        adsorbedEntity.setVelocity(vec);
        if (!adsorbedEntity.isOnGround()) {
            adsorbedEntity.setIgnoreFallDamage(true);
        }
        PlayerLookup.tracking(adsorbedEntity).forEach(serverPlayer -> serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(adsorbedEntity)));
    }

}
