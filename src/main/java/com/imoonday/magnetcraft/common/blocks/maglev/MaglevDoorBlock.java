package com.imoonday.magnetcraft.common.blocks.maglev;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

/**
 * @author iMoonDay
 */
public class MaglevDoorBlock extends DoorBlock {

    public MaglevDoorBlock(Settings settings, BlockSetType blockSetType) {
        super(settings, blockSetType);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }
}
