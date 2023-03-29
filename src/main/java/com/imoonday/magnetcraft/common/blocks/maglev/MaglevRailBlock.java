package com.imoonday.magnetcraft.common.blocks.maglev;

import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import static net.minecraft.registry.tag.BlockTags.RAILS;

public class MaglevRailBlock extends RailBlock {

    public static final BooleanProperty PASSABLE = BooleanProperty.of("passable");

    public MaglevRailBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, RailShape.NORTH_SOUTH).with(WATERLOGGED, false).with(PASSABLE, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, WATERLOGGED, PASSABLE);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return true;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient || !world.getBlockState(pos).isOf(this)) {
            return;
        }
        this.updateBlockState(state, world, pos, sourceBlock);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state);
    }

    public static VoxelShape getShape(BlockState state) {
        return state.contains(PASSABLE) && state.get(PASSABLE) ? VoxelShapes.empty() : Block.createCuboidShape(0.0, -0.1, 0.0, 16.0, 0.0, 16.0);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return railExpand(world, pos, player, hand, hit);
    }

    public static ActionResult railExpand(World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        boolean isRail = stack.isOf(Item.fromBlock(BlockRegistries.MAGLEV_RAIL_BLOCK)) || stack.isOf(Item.fromBlock(BlockRegistries.MAGLEV_POWERED_RAIL_BLOCK)) || stack.isOf(Item.fromBlock(BlockRegistries.MAGLEV_ACTIVATOR_RAIL_BLOCK)) || stack.isOf(Item.fromBlock(BlockRegistries.MAGLEV_DETECTOR_RAIL_BLOCK));
        if (isRail) {
            Direction direction = player.getMovementDirection();
            BlockState state;
            RailShape shape = direction == Direction.NORTH || direction == Direction.SOUTH ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST;
            state = stack.isOf(Item.fromBlock(BlockRegistries.MAGLEV_RAIL_BLOCK)) ? Block.getBlockFromItem(stack.getItem()).getDefaultState().with(SHAPE, shape) : Block.getBlockFromItem(stack.getItem()).getDefaultState().with(Properties.STRAIGHT_RAIL_SHAPE, shape);
            boolean up = hit.getSide() == Direction.UP;
            boolean down = hit.getSide() == Direction.DOWN;
            if (up || down) {
                BlockPos offsetPos = pos.offset(direction);
                if (down) {
                    offsetPos = offsetPos.up();
                }
                if (world.getBlockState(offsetPos).isAir()) {
                    world.setBlockState(offsetPos, state);
                    if (!player.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }
                    player.playSound(state.getSoundGroup().getPlaceSound(), 1, 1);
                    return ActionResult.SUCCESS;
                } else {
                    int times = 1;
                    while (world.getBlockState(offsetPos).isIn(RAILS)) {
                        offsetPos = offsetPos.offset(direction);
                        if (down) {
                            offsetPos = offsetPos.up();
                        }
                        if (++times > 15) {
                            return ActionResult.CONSUME;
                        }
                    }
                    if (world.getBlockState(offsetPos).isAir()) {
                        world.setBlockState(offsetPos, state);
                        if (!player.getAbilities().creativeMode) {
                            stack.decrement(1);
                        }
                        player.playSound(state.getSoundGroup().getPlaceSound(), 1, 1);
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
