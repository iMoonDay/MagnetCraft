package com.imoonday.magnetcraft.common.blocks;

import com.imoonday.magnetcraft.common.blocks.entities.DemagnetizerEntity;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * @author iMoonDay
 */
public class DemagnetizerBlock extends BlockWithEntity {

    public DemagnetizerBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DemagnetizerEntity(pos,state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlockRegistries.DEMAGNETIZER_ENTITY, (world1, pos, state1, entity) -> DemagnetizerEntity.tick(world1, pos));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

}
