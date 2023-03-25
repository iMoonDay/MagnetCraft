package com.imoonday.magnetcraft.common.blocks.maglev;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class MaglevLeverBlock extends LeverBlock {

    public MaglevLeverBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

}
