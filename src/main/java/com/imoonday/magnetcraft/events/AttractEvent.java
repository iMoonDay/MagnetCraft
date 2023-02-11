package com.imoonday.magnetcraft.events;

import com.imoonday.magnetcraft.MagnetCraft;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AttractEvent {
    public static void attractItems(@Nullable ItemStack mainhandStack, @Nullable ItemStack offhandStack, LivingEntity entity, boolean selected, double dis, @Nullable String hand) {

        boolean magnetOff = entity.getScoreboardTags().contains("MagnetOFF");

        boolean entityMainhandAllowed;
        boolean entityOffhandAllowed;

        boolean mainhandHasEnch = NbtEvent.hasEnchantment(entity, EquipmentSlot.MAINHAND, "magnetcraft:attract");
        boolean offhandHasEnch = NbtEvent.hasEnchantment(entity, EquipmentSlot.OFFHAND, "magnetcraft:attract");

        boolean equipmentsHasEnch = NbtEvent.hasEnchantment(entity, EquipmentSlot.HEAD, "magnetcraft:attract")
                || NbtEvent.hasEnchantment(entity, EquipmentSlot.CHEST, "magnetcraft:attract")
                || NbtEvent.hasEnchantment(entity, EquipmentSlot.FEET, "magnetcraft:attract")
                || NbtEvent.hasEnchantment(entity, EquipmentSlot.LEGS, "magnetcraft:attract");

        if (selected && mainhandStack != ItemStack.EMPTY) {
            assert mainhandStack != null;
            entityMainhandAllowed = mainhandStack.isOf(MagnetCraft.PERMANENT_MAGNET_ITEM);
        } else entityMainhandAllowed = false;

        if (selected && offhandStack != ItemStack.EMPTY) {
            assert offhandStack != null;
            entityOffhandAllowed = offhandStack.isOf(MagnetCraft.PERMANENT_MAGNET_ITEM);
        } else entityOffhandAllowed = false;

        int degaussingDis = 15;//消磁距离

        boolean canAttract = entity.getWorld().getOtherEntities(null, new Box(
                                entity.getPos().getX() + degaussingDis,
                                entity.getPos().getY() + degaussingDis,
                                entity.getPos().getZ() + degaussingDis,
                                entity.getPos().getX() - degaussingDis,
                                entity.getPos().getY() - degaussingDis,
                                entity.getPos().getZ() - degaussingDis),
                        e -> (e instanceof LivingEntity && ((LivingEntity) e)
                                .hasStatusEffect(MagnetCraft.DEGAUSSING_EFFECT)
                                && e.distanceTo(entity) <= degaussingDis && !e.isSpectator()))
                .isEmpty();

        if (magnetOff || !canAttract) return;//BUG:客户端和服务端不同步返回

        if (selected && !equipmentsHasEnch && !offhandHasEnch
                && mainhandStack == ItemStack.EMPTY
                && Objects.equals(hand, "mainhand")
                && entity.getMainHandStack().getItem() == Items.AIR) return;

        if (selected && !equipmentsHasEnch && !mainhandHasEnch
                && offhandStack == ItemStack.EMPTY
                && Objects.equals(hand, "offhand")
                && entity.getOffHandStack().getItem() == Items.AIR) return;

        if (selected && !equipmentsHasEnch && mainhandStack == ItemStack.EMPTY && offhandStack == ItemStack.EMPTY
                && Objects.equals(hand, "hand")
                && entity.getMainHandStack().getItem() == Items.AIR && entity.getOffHandStack().getItem() == Items.AIR)
            return;

        entity.getWorld().getOtherEntities(entity, new Box(
                                entity.getPos().getX() + dis,
                                entity.getPos().getY() + dis,
                                entity.getPos().getZ() + dis,
                                entity.getPos().getX() - dis,
                                entity.getPos().getY() - dis,
                                entity.getPos().getZ() - dis),
                        e -> (e instanceof ItemEntity
                                || e instanceof ExperienceOrbEntity
                                || (e.getScoreboardTags().contains(entity.getEntityName())
                                && e instanceof LivingEntity) && e.distanceTo(entity) <= dis))
                .forEach(e -> {

                    double move_x = (entity.getX() - e.getX()) * 0.05;
                    double move_y = (entity.getY() + 1 - e.getY()) * 0.05;
                    double move_z = (entity.getZ() - e.getZ()) * 0.05;

                    if (e instanceof ItemEntity || e instanceof ExperienceOrbEntity) {

                        if ((e.getVelocity().getX() == 0.0
                                || e.getVelocity().getZ() == 0.0)
                                && (e.getVelocity().getY() > 0.0
                                || e.getVelocity().getY() < -0.12)) {

                            e.setVelocity(new Vec3d(move_x, 0.25, move_z));

                        } else {

                            e.setVelocity(new Vec3d(move_x, move_y, move_z));

                        }

                    } else if (entityMainhandAllowed || entityOffhandAllowed) {

                        ((LivingEntity) e).addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20, 0, false, false));
                        e.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(entity.getX(), entity.getY() + 1, entity.getZ()));
                        e.move(MovementType.SELF, new Vec3d(move_x, move_y, move_z));

                    }
                });
    }
}

