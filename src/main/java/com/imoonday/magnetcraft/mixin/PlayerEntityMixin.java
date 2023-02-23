package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.common.blocks.LodestoneBlock;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
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
            World world = ((PlayerEntity) (Object) this).getWorld();
            if (world == null) return;
            MinecraftClient client = MinecraftClient.getInstance();
            HitResult hit = client.crosshairTarget;
            if (hit != null) {
                switch (hit.getType()) {
                    case BLOCK -> {
                        BlockHitResult blockHit = (BlockHitResult) hit;
                        BlockPos blockPos = blockHit.getBlockPos();
                        assert client.world != null;
                        BlockState blockState = client.world.getBlockState(blockPos);
                        Block block = blockState.getBlock();
                        if (blockState.isOf(BlockRegistries.LODESTONE_BLOCK)) {
                            LodestoneBlock.showState(world, blockPos, player);
                        }
                    }
                    case ENTITY -> {
                        EntityHitResult entityHit = (EntityHitResult) hit;
                        Entity entity = entityHit.getEntity();
                    }
                }
            }
        }
    }
}
