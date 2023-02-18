package com.imoonday.magnetcraft.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface MiningBlockCallback {

    Event<MiningBlockCallback> EVENT = EventFactory.createArrayBacked(MiningBlockCallback.class,
            (listeners) -> (world, player, stack, block, state, pos, blockEntity) -> {
                for (MiningBlockCallback listener : listeners) {
                    ActionResult result = listener.interact(world, player, stack, block, state, pos, blockEntity);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult interact(World world, PlayerEntity player, ItemStack stack, Block block, BlockState state, BlockPos pos, BlockEntity blockEntity);

}
