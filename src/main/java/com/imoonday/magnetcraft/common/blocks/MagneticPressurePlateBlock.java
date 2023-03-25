package com.imoonday.magnetcraft.common.blocks;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MagneticPressurePlateBlock extends PressurePlateBlock {

    public MagneticPressurePlateBlock(ActivationRule type, Settings settings, BlockSetType blockSetType) {
        super(type, settings, blockSetType);
    }

    @Override
    protected int getRedstoneOutput(World world, BlockPos pos) {
        return world.getOtherEntities(null, BOX.offset(pos), entity -> entity instanceof PlayerEntity).stream().map(entity -> (PlayerEntity) entity).anyMatch(player -> !player.canAvoidTraps()) ? 15 : 0;
    }

}
