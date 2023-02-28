package com.imoonday.magnetcraft.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(at = @At(value = "HEAD"), method = "tick")
    public void checkLootingAt(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player != null) {
            World world = ((PlayerEntity) (Object) this).world;
            if (world == null) return;
//            MinecraftClient client = MinecraftClient.getInstance();
//            HitResult hit = client.crosshairTarget;
//            if (hit != null) {
//                if (Objects.requireNonNull(hit.getType()) == HitResult.Type.BLOCK) {
//                    BlockHitResult blockHit = (BlockHitResult) hit;
//                    BlockPos blockPos = blockHit.getBlockPos();
//                    assert client.world != null;
//                    BlockState blockState = client.world.getBlockState(blockPos);
//                    if (blockState.isOf(BlockRegistries.LODESTONE_BLOCK)) {
//                        LodestoneBlock.showState(world, blockPos, player);
//                    }
//                }
//            }
        }
    }
}
