package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.methods.EnchantmentMethods;
import com.imoonday.magnetcraft.methods.TeleportMethods;
import com.imoonday.magnetcraft.registries.common.EnchantmentRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Shadow @Final protected StateManager<Block, BlockState> stateManager;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V"), method = "afterBreak", cancellable = true)
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack, CallbackInfo ci) {
        boolean hasEnchantment = EnchantmentMethods.hasEnchantment(stack, EnchantmentRegistries.AUTOMATIC_COLLECTION_ENCHANTMENT);
        if (hasEnchantment) {
            Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, player, stack).forEach(e -> TeleportMethods.giveItemStackToPlayer(player, e));
            ci.cancel();
        }
    }

}
