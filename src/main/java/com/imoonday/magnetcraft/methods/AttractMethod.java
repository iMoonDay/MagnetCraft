package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.special.IdentifierRegistries;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AttractMethod {

    public static boolean entityCanAttract = false;

    public static void attractItems(@Nullable ItemStack mainhandStack, @Nullable ItemStack offhandStack, LivingEntity entity, boolean selected, double dis, @Nullable String hand) {
        boolean magnetOff = entity.getScoreboardTags().contains("MagnetOFF");
        boolean mainhandHasEnch = NbtClassMethod.hasEnchantment(entity, EquipmentSlot.MAINHAND, "magnetcraft:attract");
        boolean offhandHasEnch = NbtClassMethod.hasEnchantment(entity, EquipmentSlot.OFFHAND, "magnetcraft:attract");
        boolean equipmentsHasEnch = NbtClassMethod.hasEnchantment(entity, EquipmentSlot.HEAD, "magnetcraft:attract") || NbtClassMethod.hasEnchantment(entity, EquipmentSlot.CHEST, "magnetcraft:attract") || NbtClassMethod.hasEnchantment(entity, EquipmentSlot.FEET, "magnetcraft:attract") || NbtClassMethod.hasEnchantment(entity, EquipmentSlot.LEGS, "magnetcraft:attract");
        boolean mainhandEmpty = selected && !equipmentsHasEnch && !offhandHasEnch && mainhandStack == ItemStack.EMPTY && Objects.equals(hand, "mainhand") && entity.getMainHandStack().getItem() == Items.AIR;
        boolean offhandEmpty = selected && !equipmentsHasEnch && !mainhandHasEnch && offhandStack == ItemStack.EMPTY && Objects.equals(hand, "offhand") && entity.getOffHandStack().getItem() == Items.AIR;
        boolean handEmpty = selected && !equipmentsHasEnch && mainhandStack == ItemStack.EMPTY && offhandStack == ItemStack.EMPTY && Objects.equals(hand, "hand") && entity.getMainHandStack().getItem() == Items.AIR && entity.getOffHandStack().getItem() == Items.AIR;
        boolean isEmpty = mainhandEmpty || offhandEmpty || handEmpty;
        boolean player = entity instanceof PlayerEntity;
        boolean client = entity.getWorld().isClient;
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        if (player && client) {
            ClientPlayNetworking.send(IdentifierRegistries.GET_OTHER_ENTITIES_PACKET_ID, PacketByteBufs.empty());
        } else {
            entityCanAttract = entity.getWorld().getOtherEntities(null, new Box(entity.getPos().getX() + degaussingDis, entity.getPos().getY() + degaussingDis, entity.getPos().getZ() + degaussingDis, entity.getPos().getX() - degaussingDis, entity.getPos().getY() - degaussingDis, entity.getPos().getZ() - degaussingDis), e -> (e instanceof LivingEntity && ((LivingEntity) e).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && e.distanceTo(entity) <= degaussingDis && !e.isSpectator())).isEmpty();
        }
        if (!magnetOff && entityCanAttract && !isEmpty) {
            entity.getWorld().getOtherEntities(entity, new Box(entity.getPos().getX() + dis, entity.getPos().getY() + dis, entity.getPos().getZ() + dis, entity.getPos().getX() - dis, entity.getPos().getY() - dis, entity.getPos().getZ() - dis), e -> (e instanceof ItemEntity || e instanceof ExperienceOrbEntity && e.distanceTo(entity) <= dis)).forEach(e -> {
                boolean nearest = e.getWorld().getOtherEntities(entity, new Box(entity.getPos().getX() + dis, entity.getPos().getY() + dis, entity.getPos().getZ() + dis, entity.getPos().getX() - dis, entity.getPos().getY() - dis, entity.getPos().getZ() - dis), o -> (o instanceof LivingEntity && o.getScoreboardTags().contains("isAttracting") && e.distanceTo(o) < e.distanceTo(entity))).isEmpty();
                if (nearest) {
                    double move_x = (entity.getX() - e.getX()) * 0.05;
                    double move_y = (entity.getY() + 1 - e.getY()) * 0.05;
                    double move_z = (entity.getZ() - e.getZ()) * 0.05;
                    boolean stop = (e.getVelocity().getX() == 0.0 || e.getVelocity().getZ() == 0.0) && (e.getVelocity().getY() > 0.0 || e.getVelocity().getY() < -0.12);
                    if (stop) {
                        e.setVelocity(new Vec3d(move_x, 0.25, move_z));
                    } else {
                        e.setVelocity(new Vec3d(move_x, move_y, move_z));
                    }
                }
            });
        }
    }
}

