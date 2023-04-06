package com.imoonday.magnetcraft.common.blocks;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class MagneticFilterLayerBlock extends FacingBlock implements Stainable {

    private final DyeColor color;

    public MagneticFilterLayerBlock(Settings settings) {
        super(settings);
        this.color = DyeColor.LIGHT_BLUE;
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.DOWN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.offset(state.get(FACING));
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isSideSolidFullSquare(world, blockPos, state.get(FACING).getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context.isHolding(state.getBlock().asItem()) || context.isDescending()) {
            return switch (state.get(FACING)) {
                case UP -> Block.createCuboidShape(0, 15, 0, 16, 16, 16);
                case DOWN -> Block.createCuboidShape(0, 0, 0, 16, 1, 16);
                case WEST -> Block.createCuboidShape(0, 0, 0, 1, 16, 16);
                case EAST -> Block.createCuboidShape(15, 0, 0, 16, 16, 16);
                case NORTH -> Block.createCuboidShape(0, 0, 0, 16, 16, 1);
                case SOUTH -> Block.createCuboidShape(0, 0, 15, 16, 16, 16);
            };
        } else {
            return VoxelShapes.empty();
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide().getOpposite());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }

}
