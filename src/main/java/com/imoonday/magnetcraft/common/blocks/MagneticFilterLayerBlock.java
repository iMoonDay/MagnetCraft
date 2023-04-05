package com.imoonday.magnetcraft.common.blocks;

import com.imoonday.magnetcraft.common.tags.BlockTags;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.DyeColor;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
//        if (context.isHolding(state.getBlock().asItem())) {
        return switch (state.get(FACING)) {
            case UP -> Block.createCuboidShape(0, 15, 0, 16, 16, 16);
            case DOWN -> Block.createCuboidShape(0, 0, 0, 16, 1, 16);
            case WEST -> Block.createCuboidShape(0, 0, 0, 1, 16, 16);
            case EAST -> Block.createCuboidShape(15, 0, 0, 16, 16, 16);
            case NORTH -> Block.createCuboidShape(0, 0, 0, 16, 16, 1);
            case SOUTH -> Block.createCuboidShape(0, 0, 15, 16, 16, 16);
        };
//        } else {
//            return VoxelShapes.empty();
//        }

    }

    public static boolean canBeAttractedTo(Entity entity, Vec3d pos) {
        for (double d = 0; d <= 1; d += 0.01) {
            Vec3d newPos = entity.getPos().add(pos.subtract(entity.getPos()).multiply(d));
            Vec3d offset = newPos.subtract(entity.getPos());
            Box newBox = entity.getBoundingBox().offset(offset);
            List<BlockPos> posList = BlockPos.stream(newBox).toList();
            for (BlockPos blockPos : posList) {
                BlockState blockState = entity.world.getBlockState(blockPos);
                if (!blockState.isIn(BlockTags.BLOCK_ATTRACT_BLOCKS)) {
                    continue;
                }
                VoxelShape voxelShape = blockState.getOutlineShape(entity.world, blockPos, ShapeContext.of(entity));
                VoxelShape voxelShape2 = voxelShape.offset(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                boolean collide = VoxelShapes.matchesAnywhere(voxelShape2, VoxelShapes.cuboid(newBox), BooleanBiFunction.AND);
                if (collide) {
                    return false;
                }
            }
        }
        return true;
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
