package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.special.IdentifierRegistries;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class CreatureMethod {

    public static boolean entityCanAttract = false;

    public static void attractCreatures(ItemStack mainhandStack, ItemStack offhandStack, LivingEntity entity, double dis, String hand) {
        boolean magnetOff = entity.getScoreboardTags().contains("MagnetOFF");
        boolean mainhandHasEnch = NbtClassMethod.hasEnchantment(entity, EquipmentSlot.MAINHAND, "magnetcraft:attract");
        boolean offhandHasEnch = NbtClassMethod.hasEnchantment(entity, EquipmentSlot.OFFHAND, "magnetcraft:attract");
        boolean equipmentsHasEnch = NbtClassMethod.hasEnchantment(entity, EquipmentSlot.HEAD, "magnetcraft:attract") || NbtClassMethod.hasEnchantment(entity, EquipmentSlot.CHEST, "magnetcraft:attract") || NbtClassMethod.hasEnchantment(entity, EquipmentSlot.FEET, "magnetcraft:attract") || NbtClassMethod.hasEnchantment(entity, EquipmentSlot.LEGS, "magnetcraft:attract");
        boolean mainhandEmpty = !equipmentsHasEnch && !offhandHasEnch && mainhandStack == ItemStack.EMPTY && Objects.equals(hand, "mainhand") && entity.getMainHandStack().getItem() == Items.AIR;
        boolean offhandEmpty = !equipmentsHasEnch && !mainhandHasEnch && offhandStack == ItemStack.EMPTY && Objects.equals(hand, "offhand") && entity.getOffHandStack().getItem() == Items.AIR;
        boolean handEmpty = !equipmentsHasEnch && mainhandStack == ItemStack.EMPTY && offhandStack == ItemStack.EMPTY && Objects.equals(hand, "hand") && entity.getMainHandStack().getItem() == Items.AIR && entity.getOffHandStack().getItem() == Items.AIR;
        boolean isEmpty = mainhandEmpty || offhandEmpty || handEmpty;
        boolean player = entity instanceof PlayerEntity;
        boolean client = entity.getWorld().isClient;
        boolean spectator = entity.isSpectator();
        boolean creative = player && ((PlayerEntity) entity).isCreative();
        boolean isMainhand = Objects.equals(hand, "mainhand");
        boolean isOffhand = Objects.equals(hand, "offhand");
        boolean isHand = Objects.equals(hand, "hand");
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        if (player && client) {
            ClientPlayNetworking.send(IdentifierRegistries.GET_OTHER_ENTITIES_PACKET_ID, PacketByteBufs.empty());
        } else {
            entityCanAttract = entity.getWorld().getOtherEntities(null, new Box(entity.getPos().getX() + degaussingDis, entity.getPos().getY() + degaussingDis, entity.getPos().getZ() + degaussingDis, entity.getPos().getX() - degaussingDis, entity.getPos().getY() - degaussingDis, entity.getPos().getZ() - degaussingDis), e -> (e instanceof LivingEntity && ((LivingEntity) e).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT)) && e.distanceTo(entity) <= degaussingDis && !e.isSpectator()).isEmpty();
        }
        if (!magnetOff && entityCanAttract && !isEmpty) {
            entity.getWorld().getOtherEntities(entity, new Box(entity.getPos().getX() + dis, entity.getPos().getY() + dis, entity.getPos().getZ() + dis, entity.getPos().getX() - dis, entity.getPos().getY() - dis, entity.getPos().getZ() - dis), e -> (e.getScoreboardTags().contains(entity.getEntityName()) && e instanceof LivingEntity && e.distanceTo(entity) <= dis)).forEach(e -> {
                double move_x = (entity.getX() - e.getX()) * 0.05;
                double move_y = (entity.getY() - e.getY()) * 0.05;
                double move_z = (entity.getZ() - e.getZ()) * 0.05;
                ((LivingEntity) e).addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 3 * 20, 0, false, false));
                ((LivingEntity) e).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 10 * 20, 0, false, false));
                e.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(entity.getX(), entity.getY() + 1, entity.getZ()));
                boolean stop = (e.getVelocity().getX() == 0.0 || e.getVelocity().getZ() == 0.0);
                if (stop) {
                    e.setVelocity(new Vec3d(move_x, 0.25, move_z));
                } else {
                    e.setVelocity(new Vec3d(move_x, move_y, move_z));
                }
                if (!spectator && !creative) {
                    ItemStack stack;
                    if (isMainhand || isHand) {
                        stack = entity.getMainHandStack();
                        int tick = stack.getOrCreateNbt().getInt("usedTick") + 1;
                        stack.getOrCreateNbt().putInt("usedTick", tick);
                    }
                    if (isOffhand || isHand) {
                        stack = entity.getOffHandStack();
                        int tick = stack.getOrCreateNbt().getInt("usedTick") + 1;
                        stack.getOrCreateNbt().putInt("usedTick", tick);
                    }
                }
            });
        }
    }
}
