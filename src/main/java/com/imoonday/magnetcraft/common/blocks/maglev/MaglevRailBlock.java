package com.imoonday.magnetcraft.common.blocks.maglev;

import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import static net.minecraft.registry.tag.BlockTags.RAILS;

/**
 * @author iMoonDay
 */
public class MaglevRailBlock extends RailBlock {
    public MaglevRailBlock(Settings settings) {
        super(settings);
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return railExpand(world, pos, player, hand, hit);
    }

    public static ActionResult railExpand(World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        boolean isRail = stack.isOf(Item.fromBlock(BlockRegistries.MAGLEV_RAIL_BLOCK)) || stack.isOf(Item.fromBlock(BlockRegistries.MAGLEV_POWERED_RAIL_BLOCK)) || stack.isOf(Item.fromBlock(BlockRegistries.MAGLEV_ACTIVATOR_RAIL_BLOCK)) || stack.isOf(Item.fromBlock(BlockRegistries.MAGLEV_DETECTOR_RAIL_BLOCK));
        if (isRail) {
            Direction direction = player.getMovementDirection();
            BlockState state;
            RailShape shape;
            if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                shape = RailShape.NORTH_SOUTH;
            } else {
                shape = RailShape.EAST_WEST;
            }
            if (stack.isOf(Item.fromBlock(BlockRegistries.MAGLEV_RAIL_BLOCK))) {
                state = Block.getBlockFromItem(stack.getItem()).getDefaultState().with(SHAPE, shape);
            } else {
                state = Block.getBlockFromItem(stack.getItem()).getDefaultState().with(Properties.STRAIGHT_RAIL_SHAPE, shape);
            }
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
                        times++;
                        if (times > 15) {
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
