package com.imoonday.magnetcraft.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class MaglevLever extends LeverBlock {
    public MaglevLever(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

}
