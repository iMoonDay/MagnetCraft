package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.methods.AttractMethod;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LodestoneEntity extends BlockEntity {

    public LodestoneEntity(BlockPos pos, BlockState state) {
        super(BlockRegistries.LODESTONE_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos) {
        boolean hasPowered = world.isReceivingRedstonePower(pos);
        double dis = world.getReceivedRedstonePower(pos) * 2 + 1;
        if (hasPowered) {
            AttractMethod.attractItems(world, pos, dis);
        }
    }
}
