package com.imoonday.magnetcraft.common.blocks.maglev;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

/**
 * @author iMoonDay
 */
public class MaglevComparatorBlock extends ComparatorBlock {
    public MaglevComparatorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }
}
