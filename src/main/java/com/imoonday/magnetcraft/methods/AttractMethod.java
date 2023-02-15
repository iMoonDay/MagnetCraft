package com.imoonday.magnetcraft.methods;

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
import net.minecraft.network.PacketByteBuf;
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

        boolean equipmentsHasEnch = NbtClassMethod.hasEnchantment(entity, EquipmentSlot.HEAD, "magnetcraft:attract")
                || NbtClassMethod.hasEnchantment(entity, EquipmentSlot.CHEST, "magnetcraft:attract")
                || NbtClassMethod.hasEnchantment(entity, EquipmentSlot.FEET, "magnetcraft:attract")
                || NbtClassMethod.hasEnchantment(entity, EquipmentSlot.LEGS, "magnetcraft:attract");

        int degaussingDis = 15;//消磁距离

        if (entity instanceof PlayerEntity && entity.getWorld().isClient) {

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(degaussingDis);
            buf.writeDouble(dis);
            buf.writeByte(2);
            buf.retain();
            ClientPlayNetworking.send(IdentifierRegistries.GET_OTHER_ENTITIES_PACKET_ID, buf);

        } else {

            entityCanAttract = entity.getWorld().getOtherEntities(null, new Box(
                            entity.getPos().getX() + degaussingDis,
                            entity.getPos().getY() + degaussingDis,
                            entity.getPos().getZ() + degaussingDis,
                            entity.getPos().getX() - degaussingDis,
                            entity.getPos().getY() - degaussingDis,
                            entity.getPos().getZ() - degaussingDis),
                    e -> (e instanceof LivingEntity && ((LivingEntity) e)
                            .hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT))
                            && e.distanceTo(entity) <= degaussingDis && !e.isSpectator()).isEmpty()
                    && entity.getWorld().getOtherEntities(entity, new Box(
                            entity.getPos().getX() + degaussingDis,
                            entity.getPos().getY() + degaussingDis,
                            entity.getPos().getZ() + degaussingDis,
                            entity.getPos().getX() - degaussingDis,
                            entity.getPos().getY() - degaussingDis,
                            entity.getPos().getZ() - degaussingDis),
                    e -> (e instanceof LivingEntity && e.getScoreboardTags().contains("isAttracting")
                            && e.distanceTo(entity) <= dis && !e.isSpectator())).isEmpty();

        }

        if (magnetOff || !entityCanAttract) return;

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
                                && e.distanceTo(entity) <= dis))
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
                    }
                });
    }
}

