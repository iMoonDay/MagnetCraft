package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.entities.MagneticIronGolemEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Consumer;

@Mixin(EntityType.class)
public class EntityTypeMixin {

//    private boolean hasLodestone;

    @Inject(method = "spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/nbt/NbtCompound;Ljava/util/function/Consumer;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/SpawnReason;ZZ)Lnet/minecraft/entity/Entity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void spawn(ServerWorld world, @Nullable NbtCompound itemNbt, @Nullable Consumer<Object> afterConsumer, BlockPos pos, SpawnReason reason, boolean alignPosition, boolean invertY, CallbackInfoReturnable<@Nullable Object> cir, Entity entity) {
        if (entity instanceof MagneticIronGolemEntity golem && reason == SpawnReason.SPAWN_EGG) {
//            this.hasLodestone = golem.getRandom().nextBoolean();
            golem.setHasLodestone(golem.getRandom().nextBoolean());
        }
    }

//    @Inject(method = "spawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/nbt/NbtCompound;Ljava/util/function/Consumer;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/SpawnReason;ZZ)Lnet/minecraft/entity/Entity;", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
//    public void spawnClient(ServerWorld world, @Nullable NbtCompound itemNbt, @Nullable Consumer<Object> afterConsumer, BlockPos pos, SpawnReason reason, boolean alignPosition, boolean invertY, CallbackInfoReturnable<@Nullable Object> cir, Entity entity) {
//        if (entity instanceof MagneticIronGolemEntity && reason == SpawnReason.SPAWN_EGG && this.hasLodestone && !world.isClient) {
//            PacketByteBuf buf = PacketByteBufs.create();
//            buf.writeInt(entity.getId());
//            PlayerLookup.tracking(entity).forEach(player -> ServerPlayNetworking.send(player, GOLEM_PACKET_ID, buf));
//        }
//    }

}
