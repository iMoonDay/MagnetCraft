package com.imoonday.magnetcraft.common.blocks.maglev;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.TickPriority;

public class MaglevComparatorBlock extends ComparatorBlock {
    public MaglevComparatorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        TickPriority tickPriority = this.isTargetNotAligned(world, pos, state) ? TickPriority.HIGH : TickPriority.NORMAL;
        world.scheduleBlockTick(pos, this, 2, tickPriority);
    }

}
