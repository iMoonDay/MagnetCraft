package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author iMoonDay
 */
@Mixin(Block.class)
public class BlockMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V"), method = "afterBreak", cancellable = true)
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack, CallbackInfo ci) {
        boolean hasEnchantment = stack.hasEnchantment(EnchantmentRegistries.AUTOMATIC_COLLECTION_ENCHANTMENT);
        if (hasEnchantment) {
            Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, player, stack).forEach(itemStack -> player.getInventory().offerOrDrop(itemStack));
            ci.cancel();
        }
    }

}
