package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.api.MagnetCraftWorld;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;

@Mixin(World.class)
public class WorldMixin implements MagnetCraftWorld {

    @Override
    public void attractItems(Vec3d pos, double dis, boolean filter, ArrayList<Item> allowedItems) {
        World world = (World) (Object) this;
        int degaussingDis = ModConfig.getValue().degaussingDis;
        if (!world.isClient) {
            boolean blockCanAttract = world.getOtherEntities(null, Box.from(pos).expand(degaussingDis), otherEntity -> (otherEntity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && pos.isInRange(otherEntity.getPos(), degaussingDis) && !otherEntity.isSpectator())).isEmpty();
            if (blockCanAttract) {
                tryAttract(world, pos, dis, filter, allowedItems);
            }
        }
    }

    private static void tryAttract(World world, Vec3d pos, double dis, boolean filter, ArrayList<Item> allowedItems) {
        int degaussingDis = ModConfig.getValue().degaussingDis;
        boolean whitelistEnable = ModConfig.getConfig().whitelist.enable;
        boolean blacklistEnable = ModConfig.getConfig().blacklist.enable;
        ArrayList<String> whitelist = ModConfig.getConfig().whitelist.list;
        ArrayList<String> blacklist = ModConfig.getConfig().blacklist.list;
        world.getOtherEntities(null, new Box(BlockPos.ofFloored(pos)).expand(dis), targetEntity -> (targetEntity instanceof ItemEntity || targetEntity instanceof ExperienceOrbEntity && targetEntity.getPos().isInRange(BlockPos.ofFloored(pos).toCenterPos(), dis) && !targetEntity.getPos().isInRange(pos, 0.5))).forEach(targetEntity -> {
            float f = (float) (pos.getX() - targetEntity.getX());
            float g = (float) (pos.getY() - targetEntity.getY());
            float h = (float) (pos.getZ() - targetEntity.getZ());
            float blockDistanceTo = MathHelper.sqrt(f * f + g * g + h * h);
            boolean pass = true;
            if (targetEntity instanceof ItemEntity itemEntity) {
                String item = Registries.ITEM.getId(itemEntity.getStack().getItem()).toString();
                boolean noDegaussingEntity = targetEntity.world.getOtherEntities(targetEntity, targetEntity.getBoundingBox().expand(degaussingDis), otherEntity -> (otherEntity instanceof LivingEntity && ((LivingEntity) otherEntity).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && targetEntity.getPos().isInRange(otherEntity.getPos(), degaussingDis))).isEmpty();
                pass = (!whitelistEnable || whitelist.contains(item)) && (!blacklistEnable || !blacklist.contains(item)) && noDegaussingEntity && (!filter || allowedItems.contains(itemEntity.getStack().getItem()));
            }
            if (pass && !world.isClient && targetEntity.canReachTo(pos)) {
                boolean hasNearerEntity = !world.getOtherEntities(targetEntity, new Box(BlockPos.ofFloored(pos)).expand(dis), otherEntity -> !(otherEntity instanceof PlayerEntity) && otherEntity.getPos().isInRange(targetEntity.getPos(), blockDistanceTo) && otherEntity.isAttracting() && targetEntity.canReachTo(otherEntity.getPos())).isEmpty();
                boolean hasNearerPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), dis, entity -> entity.isAttracting() && targetEntity.canReachTo(entity.getPos())) != null;
                boolean hasNearerBlock = false;
                Vec3d otherBlockPos = targetEntity.getAttractSource();
                if (otherBlockPos != null) {
                    hasNearerBlock = targetEntity.getPos().distanceTo(otherBlockPos) < targetEntity.getPos().distanceTo(pos);
                }
                if (!hasNearerPlayer && !hasNearerEntity && !hasNearerBlock) {
                    targetEntity.setAttractSource(pos);
                    Vec3d vec = pos.subtract(targetEntity.getPos()).multiply(0.05);
                    targetEntity.setVelocity(targetEntity.horizontalCollision ? vec.multiply(1, 0, 1).add(0, 0.25, 0) : vec);
                    PlayerLookup.tracking(targetEntity).forEach(player -> player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(targetEntity)));
                }
            }
        });
    }

}
