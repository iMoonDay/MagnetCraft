package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.special.IdentifierRegistries;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class CreatureMethod {

    public static boolean entityCanAttract = false;

    public static void attractCreatures(ItemStack mainhandStack, ItemStack offhandStack, LivingEntity entity, double dis, String hand) {

        boolean magnetOff = entity.getScoreboardTags().contains("MagnetOFF");

        int degaussingDis = 15;//消磁距离

        if (entity instanceof PlayerEntity && entity.getWorld().isClient) {

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(degaussingDis);
            buf.writeDouble(dis);
            buf.writeByte(1);
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
                            && e.distanceTo(entity) <= degaussingDis && !e.isSpectator()).isEmpty();
        }

        if (magnetOff || !entityCanAttract) return;

        if (mainhandStack == ItemStack.EMPTY
                && Objects.equals(hand, "mainhand")
                && entity.getMainHandStack().getItem() == Items.AIR) return;

        if (offhandStack == ItemStack.EMPTY
                && Objects.equals(hand, "offhand")
                && entity.getOffHandStack().getItem() == Items.AIR) return;

        if (mainhandStack == ItemStack.EMPTY && offhandStack == ItemStack.EMPTY
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
                        e -> (e.getScoreboardTags().contains(entity.getEntityName())
                                && e instanceof LivingEntity && e.distanceTo(entity) <= dis))
                .forEach(e -> {

                    double move_x = (entity.getX() - e.getX()) * 0.05;
                    double move_y = (entity.getY() - e.getY()) * 0.05;
                    double move_z = (entity.getZ() - e.getZ()) * 0.05;

                    ((LivingEntity) e).addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 3 * 20, 0, false, false));
                    ((LivingEntity) e).addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 3 * 20, 5, false, false));
                    e.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(entity.getX(), entity.getY() + 1, entity.getZ()));
                    e.setVelocity(new Vec3d(move_x, move_y, move_z));

                    if (Objects.equals(hand, "mainhand") || Objects.equals(hand, "hand")) {
                        NbtCompound nbt = entity.getMainHandStack().getOrCreateNbt();
                        int tick = entity.getMainHandStack().getOrCreateNbt().getInt("usedTick") + 1;
                        nbt.putInt("usedTick", tick);
                        entity.getMainHandStack().setNbt(nbt);
                    }

                    if (Objects.equals(hand, "offhand") || Objects.equals(hand, "hand")) {
                        NbtCompound nbt = entity.getOffHandStack().getOrCreateNbt();
                        int tick = entity.getOffHandStack().getOrCreateNbt().getInt("usedTick") + 1;
                        nbt.putInt("usedTick", tick);
                        entity.getOffHandStack().setNbt(nbt);
                    }

                });
    }
}
