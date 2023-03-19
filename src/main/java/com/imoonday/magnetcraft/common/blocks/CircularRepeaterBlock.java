package com.imoonday.magnetcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.TickPriority;

public class CircularRepeaterBlock extends RepeaterBlock {

    private static final IntProperty TICK = IntProperty.of("tick", 0, 20);
    private static final BooleanProperty OUTPUTING = BooleanProperty.of("outputing");

    public CircularRepeaterBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(TICK, 1).with(OUTPUTING, true).with(POWERED, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = super.getPlacementState(ctx);
        if (blockState != null) {
            World world = ctx.getWorld();
            BlockPos pos = ctx.getBlockPos();
            return blockState.with(POWERED, this.hasPower(world, pos, blockState));
        }
        return super.getPlacementState(ctx);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.scheduleBlockTick(pos, this, 1);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean powered = this.isLocked(world, pos, state) ? state.get(POWERED) : this.hasPower(world, pos, state);
        int tick = state.get(TICK) + 1;
        boolean outputing = state.get(OUTPUTING);
        int maxTick = switch (state.get(DELAY)) {
            case 1 -> 1;
            case 2 -> 4;
            case 3 -> 10;
            case 4 -> 20;
            default -> 0;
        };
        if (tick > maxTick) {
            tick = 1;
            outputing = !outputing;
        }
        world.setBlockState(pos, state.with(TICK, tick).with(OUTPUTING, outputing).with(POWERED, powered), Block.NOTIFY_LISTENERS);
        world.scheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.getWeakRedstonePower(world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!state.get(POWERED)) {
            return 0;
        }
        if (state.get(FACING) == direction) {
            return this.getOutputLevel(world, pos, state);
        }
        return 0;
    }

    @Override
    protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
        return state.get(OUTPUTING) && state.get(POWERED) ? 15 : 0;
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 1;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, DELAY, LOCKED, POWERED, TICK, OUTPUTING);
    }

}
