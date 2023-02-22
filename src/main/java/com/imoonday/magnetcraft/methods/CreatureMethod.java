package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

public class CreatureMethod {

    public static void attractCreatures(ItemStack mainhandStack, ItemStack offhandStack, LivingEntity entity, double dis, AttractMethod.Hand hand) {
        boolean magnetOff = entity.getScoreboardTags().contains("MagnetCraft.MagnetOFF");
        boolean isMainhand = hand == AttractMethod.Hand.MAINHAND;
        boolean isOffhand = hand == AttractMethod.Hand.OFFHAND;
        boolean isHand = hand == AttractMethod.Hand.HAND;
        boolean mainhandHasEnch = NbtClassMethod.hasEnchantment(entity, EquipmentSlot.MAINHAND, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean offhandHasEnch = NbtClassMethod.hasEnchantment(entity, EquipmentSlot.OFFHAND, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean equipmentsHasEnch = NbtClassMethod.hasEnchantment(entity, EquipmentSlot.HEAD, EnchantmentRegistries.ATTRACT_ENCHANTMENT) || NbtClassMethod.hasEnchantment(entity, EquipmentSlot.CHEST, EnchantmentRegistries.ATTRACT_ENCHANTMENT) || NbtClassMethod.hasEnchantment(entity, EquipmentSlot.FEET, EnchantmentRegistries.ATTRACT_ENCHANTMENT) || NbtClassMethod.hasEnchantment(entity, EquipmentSlot.LEGS, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean mainhandEmpty = !equipmentsHasEnch && !offhandHasEnch && mainhandStack == ItemStack.EMPTY && isMainhand && entity.getMainHandStack().getItem() == Items.AIR;
        boolean offhandEmpty = !equipmentsHasEnch && !mainhandHasEnch && offhandStack == ItemStack.EMPTY && isOffhand && entity.getOffHandStack().getItem() == Items.AIR;
        boolean handEmpty = !equipmentsHasEnch && mainhandStack == ItemStack.EMPTY && offhandStack == ItemStack.EMPTY && isHand && entity.getMainHandStack().getItem() == Items.AIR && entity.getOffHandStack().getItem() == Items.AIR;
        boolean isEmpty = mainhandEmpty || offhandEmpty || handEmpty;
        boolean player = entity.isPlayer();
        boolean client = entity.getWorld().isClient;
        boolean spectator = entity.isSpectator();
        boolean creative = player && ((PlayerEntity) entity).isCreative();
        boolean entityCanAttract;
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        if (!client) {
            entityCanAttract = entity.getWorld().getOtherEntities(null, entity.getBoundingBox().expand(degaussingDis), e -> (e instanceof LivingEntity && ((LivingEntity) e).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT)) && e.distanceTo(entity) <= degaussingDis && !e.isSpectator()).isEmpty();
            if (!magnetOff && entityCanAttract && !isEmpty) {
                entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(dis), e -> (e.getScoreboardTags().contains(entity.getEntityName()) && e instanceof LivingEntity && e.distanceTo(entity) <= dis)).forEach(e -> {
                    double move_x = (entity.getX() - e.getX()) * 0.05;
                    double move_y = (entity.getY() - e.getY()) * 0.05;
                    double move_z = (entity.getZ() - e.getZ()) * 0.05;
                    ((LivingEntity) e).addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 3 * 20, 0, false, false));
                    ((LivingEntity) e).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 10 * 20, 0, false, false));
                    e.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(entity.getX(), entity.getY() + 1, entity.getZ()));
                    boolean stop = (e.getVelocity().getX() == 0.0 || e.getVelocity().getZ() == 0.0);
                    if (stop) {
                        e.setVelocity(new Vec3d(move_x, 0.25, move_z));
                        e.setVelocityClient(move_x, 0.25, move_z);
                    } else {
                        e.setVelocity(new Vec3d(move_x, move_y, move_z));
                        e.setVelocityClient(move_x, move_y, move_z);
                    }
                    PlayerLookup.tracking(e).forEach(o -> o.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(e)));
                    if (!spectator && !creative) {
                        ItemStack stack;
                        if (isMainhand || isHand) {
                            stack = entity.getMainHandStack();
                            int tick = stack.getOrCreateNbt().getInt("usedTick") + 1;
                            stack.getOrCreateNbt().putInt("usedTick", tick);
                        } else {
                            stack = entity.getOffHandStack();
                            int tick = stack.getOrCreateNbt().getInt("usedTick") + 1;
                            stack.getOrCreateNbt().putInt("usedTick", tick);
                        }
                    }
                });
            }
        }
    }
}
