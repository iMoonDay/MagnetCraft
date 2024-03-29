package com.imoonday.magnetcraft.common.blocks;

import com.imoonday.magnetcraft.common.blocks.entities.LodestoneEntity;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LodestoneBlock extends BlockWithEntity {

    public static final String REDSTONE = "redstone";
    public static final String DIS = "dis";
    public static final String DIRECTION = "direction";
    public static final String FILTER = "filter";

    public LodestoneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.magnetcraft.lodestone.tooltip.1")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
        tooltip.add(Text.translatable("block.magnetcraft.lodestone.tooltip.2")
                .formatted(Formatting.GRAY).formatted(Formatting.BOLD));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LodestoneEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlockRegistries.LODESTONE_ENTITY, LodestoneEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean(REDSTONE, true);
        nbt.putInt(DIRECTION, 0);
        nbt.putBoolean(FILTER, false);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.readNbt(nbt);
            blockEntity.markDirty();
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return ActionResult.FAIL;
        }
        int direction = blockEntity.createNbt().getInt(DIRECTION);
        NbtCompound nbt = new NbtCompound();
        Direction side = hit.getSide();
        if (player.isSneaky()) {
            switch (side) {
                case SOUTH -> nbt.putInt(DIRECTION, direction == 1 ? 0 : 1);
                case WEST -> nbt.putInt(DIRECTION, direction == 2 ? 0 : 2);
                case NORTH -> nbt.putInt(DIRECTION, direction == 3 ? 0 : 3);
                case EAST -> nbt.putInt(DIRECTION, direction == 4 ? 0 : 4);
                case UP -> nbt.putInt(DIRECTION, direction == 5 ? 0 : 5);
                case DOWN -> nbt.putInt(DIRECTION, direction == 6 ? 0 : 6);
            }
        } else {
            Block block = Block.getBlockFromItem(player.getStackInHand(hand).getItem());
            if (block instanceof CarvedPumpkinBlock || block.getDefaultState().isOf(BlockRegistries.MAGNET_BLOCK)) {
                return ActionResult.PASS;
            }
            if (!world.isClient) {
                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
                if (screenHandlerFactory != null) {
                    player.openHandledScreen(screenHandlerFactory);
                }
            }
        }
        if (!world.isClient) {
            blockEntity.readNbt(nbt);
            blockEntity.markDirty();
            if (player.isSneaky()) {
                showState(world, pos, player);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LodestoneEntity lodestoneEntity) {
                ItemScatterer.spawn(world, pos, lodestoneEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    public static void showState(World world, BlockPos pos, @Nullable PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return;
        }
        boolean redstone = blockEntity.createNbt().getBoolean(REDSTONE);
        double dis = blockEntity.createNbt().getDouble(DIS);
        int direction = blockEntity.createNbt().getInt(DIRECTION);
        String directionText = "text.magnetcraft.message.direction." + direction;
        Text text = redstone ? Text.translatable("text.magnetcraft.message.redstone_mode").append(Text.translatable("block.magnetcraft.lodestone.tooltip.3", dis)).append(Text.translatable(directionText)) : Text.translatable("block.magnetcraft.lodestone.tooltip.3", dis).append(Text.translatable(directionText));
        if (!world.isClient && player != null) {
            player.sendMessage(text, true);
        }
    }
}
