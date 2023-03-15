package com.imoonday.magnetcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.block.RedstoneWireBlock.POWER;

public class VerticalRepeaterBlock extends FacingBlock {

    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final DirectionProperty UP_DOWN_FACING = DirectionProperty.of("facing", facing -> facing == Direction.UP || facing == Direction.DOWN);

    public VerticalRepeaterBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(UP_DOWN_FACING, Direction.UP).with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP_DOWN_FACING, POWERED);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(UP_DOWN_FACING, rotation.rotate(state.get(UP_DOWN_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(UP_DOWN_FACING)));
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos blockPos = pos.offset(state.get(UP_DOWN_FACING));
        BlockState blockState = world.getBlockState(blockPos);
        List<Integer> powers = new ArrayList<>();
        powers.addAll(Arrays.stream(Direction.values()).map(direction1 -> blockState.getStrongRedstonePower(world, blockPos, direction1)).collect(Collectors.toCollection(ArrayList::new)));
        powers.addAll(Arrays.stream(Direction.values()).map(direction1 -> blockState.getWeakRedstonePower(world, blockPos, direction1)).collect(Collectors.toCollection(ArrayList::new)));
        world.setBlockState(pos, state.with(POWERED, Collections.max(powers) > 0), Block.NOTIFY_LISTENERS);
        this.updateNeighbors(world, pos, state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        this.scheduleTick(world, pos);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private void scheduleTick(WorldAccess world, BlockPos pos) {
        if (!world.isClient() && !world.getBlockTickScheduler().isQueued(pos, this)) {
            world.scheduleBlockTick(pos, this, 2);
        }
    }

    protected void updateNeighbors(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(UP_DOWN_FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        world.updateNeighbor(blockPos, this, pos);
        world.updateNeighborsExcept(blockPos, this, direction);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.getWeakRedstonePower(world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(POWERED) && state.get(UP_DOWN_FACING) == direction) {
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            List<Integer> powers = new ArrayList<>();
            powers.addAll(Arrays.stream(Direction.values()).map(direction1 -> blockState.getStrongRedstonePower(world, blockPos, direction1)).collect(Collectors.toCollection(ArrayList::new)));
            powers.addAll(Arrays.stream(Direction.values()).map(direction1 -> blockState.getWeakRedstonePower(world, blockPos, direction1)).collect(Collectors.toCollection(ArrayList::new)));
            return blockState.getBlock() instanceof RedstoneWireBlock ? world.getBlockState(blockPos).get(POWER) : Collections.max(powers);
        }
        return 0;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getPlayerLookDirection();
        if (direction != Direction.UP && direction != Direction.DOWN) {
            direction = Direction.UP;
        }
        return this.getDefaultState().with(UP_DOWN_FACING, direction);
    }

}
