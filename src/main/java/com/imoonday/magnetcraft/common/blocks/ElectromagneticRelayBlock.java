package com.imoonday.magnetcraft.common.blocks;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Arrays;

public class ElectromagneticRelayBlock extends AbstractRedstoneGateBlock {

    protected static final BooleanProperty PASS = BooleanProperty.of("pass");
    protected static final IntProperty POWER = Properties.POWER;
    protected static final EnumProperty<PassFrom> PASS_FROM = EnumProperty.of("pass_from", PassFrom.class);

    public ElectromagneticRelayBlock(Settings settings) {
        super(settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(FACING, Direction.NORTH).with(POWERED, false).with(PASS, false).with(POWER, 0).with(PASS_FROM, PassFrom.NONE));
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (this.hasPower(world, pos, state) || this.hasInputSides(world, pos, state)) {
            world.scheduleBlockTick(pos, this, 1);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        Direction direction = state.get(FACING);
        Direction direction2 = direction.rotateYClockwise();
        Direction direction3 = direction.rotateYCounterclockwise();
        boolean left = this.getInputLevel(world, pos.offset(direction2), direction2) > 0;
        boolean right = this.getInputLevel(world, pos.offset(direction3), direction3) > 0;
        world.setBlockState(pos, state.with(POWER, getPower(world, pos, state)).with(POWERED, hasPower(world, pos, state)).with(PASS, this.hasInputSides(world, pos, state)).with(PASS_FROM, left && right ? PassFrom.BOTH : left ? PassFrom.LEFT : right ? PassFrom.RIGHT : PassFrom.NONE), Block.NOTIFY_LISTENERS);
    }

    protected boolean hasInputSides(WorldView world, BlockPos pos, BlockState state) {
        return this.getMaxInputLevelSides(world, pos, state) > 0;
    }

    @Override
    protected int getMaxInputLevelSides(WorldView world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        Direction direction2 = direction.rotateYClockwise();
        Direction direction3 = direction.rotateYCounterclockwise();
        return Math.max(this.getInputLevel(world, pos.offset(direction2), direction2), this.getInputLevel(world, pos.offset(direction3), direction3));
    }

    @Override
    protected int getInputLevel(WorldView world, BlockPos pos, Direction dir) {
        BlockState blockState = world.getBlockState(pos);
        if (this.isValidInput(blockState)) {
            if (blockState.isOf(Blocks.REDSTONE_BLOCK)) {
                return 15;
            }
            if (blockState.isOf(Blocks.REDSTONE_WIRE)) {
                return blockState.get(RedstoneWireBlock.POWER);
            }
            int[] powers = new int[]{blockState.getWeakRedstonePower(world, pos, dir.getOpposite()), blockState.getStrongRedstonePower(world, pos, dir.getOpposite()), world.getStrongRedstonePower(pos, dir)};
            return Arrays.stream(powers).max().getAsInt();
        }
        return 0;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        world.scheduleBlockTick(pos, this, 1);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return this.getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) && state.get(PASS) && state.get(FACING) == direction ? state.get(POWER) : 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, PASS, POWER, PASS_FROM);
    }

    protected enum PassFrom implements StringIdentifiable {
        LEFT("left"),
        RIGHT("right"),
        BOTH("both"),
        NONE("none");

        private final String name;

        PassFrom(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }

}
