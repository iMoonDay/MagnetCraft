package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.registries.common.EntityRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.entity.LargeEntitySpawnHelper;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {

    @Inject(method = "interactMob", at = @At(value = "HEAD"), cancellable = true)
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(ItemRegistries.CREATURE_MAGNET_ITEM)) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }

    @Inject(method = "summonGolem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LargeEntitySpawnHelper;trySpawnAt(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;IIILnet/minecraft/entity/LargeEntitySpawnHelper$Requirements;)Ljava/util/Optional;"), cancellable = true)
    public void summonGolem(ServerWorld world, long time, int requiredCount, CallbackInfo ci) {
        VillagerEntity entity = (VillagerEntity) (Object) this;
        Box box = entity.getBoundingBox().expand(10.0, 10.0, 10.0);
        List<VillagerEntity> list = world.getNonSpectatingEntities(VillagerEntity.class, box);
        Random random = entity.getRandom();
        if (random.nextBetween(1, 4) == 1) {
            if (LargeEntitySpawnHelper.trySpawnAt(EntityRegistries.MAGNETIC_IRON_GOLEM, SpawnReason.MOB_SUMMONED, world, entity.getBlockPos(), 10, 8, 6, LargeEntitySpawnHelper.Requirements.IRON_GOLEM).isPresent()) {
                list.forEach(GolemLastSeenSensor::rememberIronGolem);
                ci.cancel();
            }
        }
    }

}
