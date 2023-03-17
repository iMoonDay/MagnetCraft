package com.imoonday.magnetcraft.common.blocks;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class MagneticPressurePlateBlock extends PressurePlateBlock {


    public MagneticPressurePlateBlock(ActivationRule type, Settings settings, BlockSetType blockSetType) {
        super(type, settings, blockSetType);
    }

    @Override
    protected int getRedstoneOutput(World world, BlockPos pos) {
        Box box = BOX.offset(pos);
        List<Entity> list = world.getOtherEntities(null,box, Entity::isPlayer);
        if (!list.isEmpty()) {
            for (Entity entity : list) {
                if (entity.canAvoidTraps()) continue;
                return 15;
            }
        }
        return 0;
    }

}
