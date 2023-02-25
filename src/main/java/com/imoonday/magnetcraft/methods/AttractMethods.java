package com.imoonday.magnetcraft.methods;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class AttractMethods {

    public enum Hand {
        MAINHAND,
        OFFHAND,
        HAND,
        NONE
    }

    public static void attractItems(@Nullable ItemStack mainhandStack, @Nullable ItemStack offhandStack, LivingEntity entity, boolean selected, double dis, Hand hand) {
        boolean magnetOff = entity.getScoreboardTags().contains("MagnetCraft.MagnetOFF");
        boolean isMainhand = hand == AttractMethods.Hand.MAINHAND;
        boolean isOffhand = hand == AttractMethods.Hand.OFFHAND;
        boolean isHand = hand == AttractMethods.Hand.HAND;
        boolean mainhandHasEnch = EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.MAINHAND, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean offhandHasEnch = EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.OFFHAND, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean equipmentsHasEnch = EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.HEAD, EnchantmentRegistries.ATTRACT_ENCHANTMENT) || EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.CHEST, EnchantmentRegistries.ATTRACT_ENCHANTMENT) || EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.FEET, EnchantmentRegistries.ATTRACT_ENCHANTMENT) || EnchantmentMethods.hasEnchantment(entity, EquipmentSlot.LEGS, EnchantmentRegistries.ATTRACT_ENCHANTMENT);
        boolean mainhandEmpty = selected && !equipmentsHasEnch && !offhandHasEnch && mainhandStack == ItemStack.EMPTY && isMainhand && entity.getMainHandStack().getItem() == Items.AIR;
        boolean offhandEmpty = selected && !equipmentsHasEnch && !mainhandHasEnch && offhandStack == ItemStack.EMPTY && isOffhand && entity.getOffHandStack().getItem() == Items.AIR;
        boolean handEmpty = selected && !equipmentsHasEnch && mainhandStack == ItemStack.EMPTY && offhandStack == ItemStack.EMPTY && isHand && entity.getMainHandStack().getItem() == Items.AIR && entity.getOffHandStack().getItem() == Items.AIR;
        boolean isEmpty = mainhandEmpty || offhandEmpty || handEmpty;
        boolean client = entity.getWorld().isClient;
        boolean entityCanAttract;
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        if (!client) {
            entityCanAttract = entity.getWorld().getOtherEntities(null, entity.getBoundingBox().expand(degaussingDis), e -> (e instanceof LivingEntity && ((LivingEntity) e).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && e.distanceTo(entity) <= degaussingDis && !e.isSpectator())).isEmpty();
            if (!magnetOff && entityCanAttract && !isEmpty) {
                attracting(entity, dis);
            }
        }
    }

    public static void attractItems(Entity entity, double dis) {
        boolean client = entity.getWorld().isClient;
        boolean entityCanAttract;
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        if (!client) {
            entityCanAttract = entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(degaussingDis), e -> (e instanceof LivingEntity && ((LivingEntity) e).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && e.distanceTo(entity) <= degaussingDis && !e.isSpectator())).isEmpty();
            if (entityCanAttract) {
                attracting(entity, dis);
            }
        }
    }

    public static void attractItems(World world, Vec3d pos, double dis) {
        boolean client = world.isClient;
        boolean entityCanAttract;
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        if (!client) {
            entityCanAttract = world.getOtherEntities(null, new Box(pos.getX() - degaussingDis, pos.getY() - degaussingDis, pos.getZ() - degaussingDis, pos.getX() + degaussingDis, pos.getY() + degaussingDis, pos.getZ() + degaussingDis), e -> (e instanceof LivingEntity && ((LivingEntity) e).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && MathHelper.sqrt((float) e.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())) <= degaussingDis && !e.isSpectator())).isEmpty();
            if (entityCanAttract) {
                attracting(world, pos, dis);
            }
        }
    }

    public static void attracting(Entity entity, double dis) {
        entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(dis), e -> (e instanceof ItemEntity || e instanceof ExperienceOrbEntity && e.distanceTo(entity) <= dis && e.distanceTo(entity) > 0.5)).forEach(e -> {
            boolean hasNearerPlayer;
            boolean hasNearerEntity = false;
            boolean player = entity.isPlayer();
            boolean client = entity.getWorld().isClient;
            boolean whitelistEnable = ModConfig.getConfig().whitelist.enable;
            boolean blacklistEnable = ModConfig.getConfig().blacklist.enable;
            String[] whitelist = ModConfig.getConfig().whitelist.list;
            String[] blacklist = ModConfig.getConfig().blacklist.list;
            String item;
            boolean whitelistPass;
            boolean blacklistPass;
            boolean hasDegaussingPlayer;
            boolean pass = true;
            int degaussingDis = ModConfig.getConfig().value.degaussingDis;
            if (e instanceof ItemEntity) {
                item = Registries.ITEM.getId(((ItemEntity) e).getStack().getItem()).toString();
                whitelistPass = Arrays.stream(whitelist).toList().contains(item);
                blacklistPass = !Arrays.stream(blacklist).toList().contains(item);
                hasDegaussingPlayer = !e.world.getOtherEntities(e, e.getBoundingBox().expand(degaussingDis), o -> (o instanceof LivingEntity && ((LivingEntity) o).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && e.distanceTo(o) <= degaussingDis)).isEmpty();
                pass = (!whitelistEnable || whitelistPass) && (!blacklistEnable || blacklistPass) && !hasDegaussingPlayer;
            }
            if (pass) {
                if (!client) {
                    if (player) {
                        hasNearerPlayer = e.getWorld().getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), dis, o -> (o.getScoreboardTags().contains("MagnetCraft.isAttracting"))) != entity;
                    } else {
                        hasNearerPlayer = e.getWorld().getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), dis, o -> (o.getScoreboardTags().contains("MagnetCraft.isAttracting"))) != null;
                        hasNearerEntity = !e.getWorld().getOtherEntities(e, entity.getBoundingBox().expand(dis), o -> (!(o.isPlayer()) && o.distanceTo(e) < entity.distanceTo(e) && o.getScoreboardTags().contains("MagnetCraft.isAttracting"))).isEmpty();
                    }
                    if (!hasNearerPlayer && !hasNearerEntity) {
                        double move_x = (entity.getX() - e.getX()) * 0.05;
                        double move_y = (entity.getEyeY() - e.getY()) * 0.05;
                        double move_z = (entity.getZ() - e.getZ()) * 0.05;
                        boolean stop = (e.getVelocity().getX() == 0.0 || e.getVelocity().getZ() == 0.0) && (e.getVelocity().getY() > 0.0 || e.getVelocity().getY() < -0.12);
                        if (stop) {
                            e.setVelocity(new Vec3d(move_x, 0.25, move_z));
                            e.setVelocityClient(move_x, 0.25, move_z);
                        } else {
                            e.setVelocity(new Vec3d(move_x, move_y, move_z));
                            e.setVelocityClient(move_x, move_y, move_z);
                        }
                        PlayerLookup.tracking(e).forEach(o -> o.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(e)));
                    }
                }
            }
        });
    }

    public static void attracting(World world, Vec3d pos, double dis) {
        world.getOtherEntities(null, new Box(pos.getX() - dis, pos.getY() - dis, pos.getZ() - dis, pos.getX() + dis, pos.getY() + dis, pos.getZ() + dis), e -> (e instanceof ItemEntity || e instanceof ExperienceOrbEntity && MathHelper.sqrt((float) e.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())) <= dis && MathHelper.sqrt((float) e.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ())) > 0.5)).forEach(e -> {
            float f = (float) (pos.getX() - e.getX());
            float g = (float) (pos.getY() - e.getY());
            float h = (float) (pos.getZ() - e.getZ());
            float blockDistanceTo = MathHelper.sqrt(f * f + g * g + h * h);
            boolean client = world.isClient;
            boolean hasNearerPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), dis, o -> (o.getScoreboardTags().contains("MagnetCraft.isAttracting"))) != null;
            boolean whitelistEnable = ModConfig.getConfig().whitelist.enable;
            boolean blacklistEnable = ModConfig.getConfig().blacklist.enable;
            String[] whitelist = ModConfig.getConfig().whitelist.list;
            String[] blacklist = ModConfig.getConfig().blacklist.list;
            String item;
            boolean whitelistPass;
            boolean blacklistPass;
            boolean hasDegaussingPlayer;
            boolean pass = true;
            boolean hasNearerEntity;
            int degaussingDis = ModConfig.getConfig().value.degaussingDis;
            if (e instanceof ItemEntity) {
                item = Registries.ITEM.getId(((ItemEntity) e).getStack().getItem()).toString();
                whitelistPass = Arrays.stream(whitelist).toList().contains(item);
                blacklistPass = !Arrays.stream(blacklist).toList().contains(item);
                hasDegaussingPlayer = !e.world.getOtherEntities(e, e.getBoundingBox().expand(degaussingDis), o -> (o instanceof LivingEntity && ((LivingEntity) o).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && e.distanceTo(o) <= degaussingDis)).isEmpty();
                pass = (!whitelistEnable || whitelistPass) && (!blacklistEnable || blacklistPass) && !hasDegaussingPlayer;
            }
            if (pass) {
                if (!client) {
                    hasNearerEntity = !world.getOtherEntities(e, new Box(pos.getX() - dis, pos.getY() - dis, pos.getZ() - dis, pos.getX() + dis, pos.getY() + dis, pos.getZ() + dis), o -> (!(o.isPlayer()) && o.distanceTo(e) < blockDistanceTo && o.getScoreboardTags().contains("MagnetCraft.isAttracting"))).isEmpty();
                    if (!hasNearerPlayer && !hasNearerEntity) {
                        double move_x = (pos.getX() - e.getX()) * 0.05;
                        double move_y = (pos.getY() - e.getY()) * 0.05;
                        double move_z = (pos.getZ() - e.getZ()) * 0.05;
                        boolean stop = (e.getVelocity().getX() == 0.0 || e.getVelocity().getZ() == 0.0) && (e.getVelocity().getY() > 0.0 || e.getVelocity().getY() < -0.12);
                        if (stop) {
                            e.setVelocity(new Vec3d(move_x, 0.25, move_z));
                            e.setVelocityClient(move_x, 0.25, move_z);
                        } else {
                            e.setVelocity(new Vec3d(move_x, move_y, move_z));
                            e.setVelocityClient(move_x, move_y, move_z);
                        }
                        PlayerLookup.tracking(e).forEach(o -> o.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(e)));
                    }
                }
            }
        });
    }
}
