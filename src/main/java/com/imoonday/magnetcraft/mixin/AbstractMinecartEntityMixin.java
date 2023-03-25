package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractMinecartEntity.class)
public class AbstractMinecartEntityMixin {

    @Redirect(method = "moveOnRail", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    public boolean isPowered(BlockState instance, Block block) {
        return instance.isOf(Blocks.POWERED_RAIL) || instance.isOf(BlockRegistries.MAGLEV_POWERED_RAIL_BLOCK);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    public boolean isActivator(BlockState instance, Block block) {
        return instance.isOf(Blocks.ACTIVATOR_RAIL) || instance.isOf(BlockRegistries.MAGLEV_ACTIVATOR_RAIL_BLOCK);
    }

}
