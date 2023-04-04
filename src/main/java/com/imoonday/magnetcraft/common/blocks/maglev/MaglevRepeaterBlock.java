package com.imoonday.magnetcraft.common.blocks.maglev;

import net.minecraft.block.BlockState;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class MaglevRepeaterBlock extends RepeaterBlock {
    public MaglevRepeaterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

}
