package com.imoonday.magnetcraft.common.blocks;

import com.imoonday.magnetcraft.common.blocks.entities.LodestoneEntity;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LodestoneBlock extends BlockWithEntity {
    public LodestoneBlock(Settings settings) {
        super(settings);
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
        nbt.putBoolean("redstone", true);
        nbt.putInt("direction", 0);
        nbt.putBoolean("filter", false);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.readNbt(nbt);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) return ActionResult.FAIL;
        int direction = blockEntity.createNbt().getInt("direction");
        NbtCompound nbt = new NbtCompound();
        Direction side = hit.getSide();
        if (player.isSneaky()) {
            switch (side) {
                case SOUTH -> nbt.putInt("direction", direction == 1 ? 0 : 1);
                case WEST -> nbt.putInt("direction", direction == 2 ? 0 : 2);
                case NORTH -> nbt.putInt("direction", direction == 3 ? 0 : 3);
                case EAST -> nbt.putInt("direction", direction == 4 ? 0 : 4);
                case UP -> nbt.putInt("direction", direction == 5 ? 0 : 5);
                case DOWN -> nbt.putInt("direction", direction == 6 ? 0 : 6);
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
        if (blockEntity == null) return;
        boolean redstone = blockEntity.createNbt().getBoolean("redstone");
        double dis = blockEntity.createNbt().getDouble("dis");
        int direction = blockEntity.createNbt().getInt("direction");
        String directionText = "text.magnetcraft.message.direction." + direction;
        Text text = redstone ? Text.translatable("text.magnetcraft.message.redstone_mode").append(Text.translatable("block.magnetcraft.lodestone.tooltip.3", dis)).append(Text.translatable(directionText)) : Text.translatable("block.magnetcraft.lodestone.tooltip.3", dis).append(Text.translatable(directionText));
        if (!world.isClient && player != null) player.sendMessage(text, true);
    }
}
