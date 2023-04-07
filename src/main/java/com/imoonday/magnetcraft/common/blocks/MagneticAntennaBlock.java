package com.imoonday.magnetcraft.common.blocks;

import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RodBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

public class MagneticAntennaBlock extends RodBlock implements Waterloggable {

    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty ACTIVATED = BooleanProperty.of("activated");
    public static final IntProperty POWER = Properties.POWER;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public MagneticAntennaBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP).with(POWERED, false).with(POWER, 0).with(ACTIVATED, false).with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, POWER, ACTIVATED, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide()).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState magnetBlockState = world.getBlockState(pos.offset(state.get(FACING).getOpposite()));
        return magnetBlockState.isOf(BlockRegistries.MAGNET_BLOCK) || magnetBlockState.isOf(BlockRegistries.NETHERITE_MAGNET_BLOCK) || magnetBlockState.isOf(BlockRegistries.LODESTONE_BLOCK);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.scheduleBlockTick(pos, this, 1);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(state.get(FACING).getOpposite()), this);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        world.scheduleBlockTick(pos, this, 1);
        return state;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        update(state, world, pos);
        world.scheduleBlockTick(pos, this, 1);
    }

    private void update(BlockState state, ServerWorld world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos magnetBlockPos = getMagnetBlockPos(world, pos);
        boolean isFirst = isFirst(state, world, pos, direction);
        boolean strongPowered = hasStrongActivatedBlock(world, pos, direction);
        boolean powered = (hasActivatedBlock(world, pos, direction) && isFirst) || strongPowered;
        boolean activated = hasRedstonePower(world, direction, magnetBlockPos) && !powered;
        int power = getPower(world, pos, direction, powered, strongPowered);
        world.setBlockState(pos, state.with(POWERED, powered).with(POWER, power).with(ACTIVATED, activated), Block.NOTIFY_LISTENERS);
        world.updateNeighborsAlways(pos.offset(direction.getOpposite()), this);
    }

    private boolean hasStrongActivatedBlock(ServerWorld world, BlockPos pos, Direction direction) {
        return IntStream.rangeClosed(1, 30).mapToObj(i -> pos.offset(direction, i)).takeWhile(blockPos -> notBlocked(world, blockPos, direction)).anyMatch(blockPos -> isActivatedBlock(world, direction, blockPos) && getMagnetBlockState(world, blockPos).isOf(BlockRegistries.NETHERITE_MAGNET_BLOCK));
    }

    private int getPower(ServerWorld world, BlockPos pos, Direction direction, boolean powered, boolean strongPowered) {
        AtomicInteger power = new AtomicInteger();
        if (strongPowered) {
            IntStream.rangeClosed(1, 30).mapToObj(i -> pos.offset(direction, i)).takeWhile(blockPos -> notBlocked(world, blockPos, direction)).filter(blockPos -> isActivatedBlock(world, direction, blockPos) && getMagnetBlockState(world, blockPos).isOf(BlockRegistries.NETHERITE_MAGNET_BLOCK)).findFirst().ifPresentOrElse(pos1 -> power.set(getRedstonePower(world, powered, pos1)), () -> power.set(0));
        } else {
            IntStream.rangeClosed(1, 15).mapToObj(i -> pos.offset(direction, i)).takeWhile(blockPos -> notBlocked(world, blockPos, direction)).filter(blockPos -> isActivatedBlock(world, direction, blockPos)).findFirst().ifPresentOrElse(pos1 -> power.set(getRedstonePower(world, powered, pos1)), () -> power.set(0));
        }
        return power.intValue();
    }

    private static int getRedstonePower(ServerWorld world, boolean powered, BlockPos pos) {
        return powered ? getMagnetBlockState(world, pos).isOf(BlockRegistries.LODESTONE_BLOCK) ? getMagnetBlockState(world, pos).getComparatorOutput(world, getMagnetBlockPos(world, pos)) : world.getReceivedRedstonePower(getMagnetBlockPos(world, pos)) : 0;
    }

    private static boolean hasRedstonePower(ServerWorld world, Direction direction, BlockPos pos) {
        return isLodestone(world, pos) ? hasComparatorOutput(world, pos) : hasStrongRedstonePower(world, direction, pos);
    }

    private static boolean hasStrongRedstonePower(ServerWorld world, Direction direction, BlockPos pos) {
        return Arrays.stream(Direction.values()).filter(direction1 -> !direction1.equals(direction)).anyMatch(direction1 -> world.getStrongRedstonePower(pos.offset(direction1), direction1) > 0);
    }

    private boolean hasActivatedBlock(ServerWorld world, BlockPos pos, Direction direction) {
        return IntStream.rangeClosed(1, 15).mapToObj(i -> pos.offset(direction, i)).takeWhile(blockPos -> notBlocked(world, blockPos, direction)).anyMatch(pos1 -> isActivatedBlock(world, direction, pos1));
    }

    private static boolean notBlocked(ServerWorld world, BlockPos blockPos, Direction direction) {
        BlockState state = world.getBlockState(blockPos);
        return !state.isOf(BlockRegistries.MAGNETIC_FILTER_GlASS_BLOCK) && (!state.isOf(BlockRegistries.MAGNETIC_FILTER_LAYER_BLOCK) || !state.get(MagneticFilterLayerBlock.FACING).equals(direction) && !state.get(MagneticFilterLayerBlock.FACING).equals(direction.getOpposite()));
    }

    private boolean isFirst(BlockState state, ServerWorld world, BlockPos pos, Direction direction) {
        return IntStream.rangeClosed(1, 15).mapToObj(i -> world.getBlockState(pos.offset(direction, i))).filter(state1 -> state1.isOf(this)).takeWhile(state1 -> !isActivatedBlock(state, state1)).noneMatch(state1 -> state1.get(FACING).equals(state.get(FACING)));
    }

    private static boolean isActivatedBlock(BlockState state, BlockState state1) {
        return state1.get(FACING).equals(state.get(FACING).getOpposite()) && state1.get(ACTIVATED);
    }

    private static boolean hasComparatorOutput(ServerWorld world, BlockPos pos) {
        return world.getBlockState(pos).getComparatorOutput(world, pos) > 0;
    }

    private static BlockPos getMagnetBlockPos(ServerWorld world, BlockPos pos) {
        return pos.offset(world.getBlockState(pos).get(FACING).getOpposite());
    }

    private static boolean isLodestone(ServerWorld world, BlockPos pos) {
        return world.getBlockState(pos).isOf(BlockRegistries.LODESTONE_BLOCK);
    }

    private boolean isActivatedBlock(ServerWorld world, Direction direction, BlockPos pos) {
        return world.getBlockState(pos).isOf(this) && world.getBlockState(pos).get(ACTIVATED) && world.getBlockState(pos).get(FACING).equals(direction.getOpposite());
    }

    private static BlockState getMagnetBlockState(ServerWorld world, BlockPos pos) {
        return world.getBlockState(getMagnetBlockPos(world, pos));
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return this.getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (direction != state.get(FACING)) {
            return 0;
        }
        return state.get(POWERED) ? state.get(POWER) : 0;
    }

    @NotNull
    public static ToIntFunction<BlockState> getLuminance() {
        return state -> state.get(ACTIVATED) ? 15 : (state.get(POWERED) ? 8 : 0);
    }

}
