package com.imoonday.magnetcraft.common.blocks;

import com.imoonday.magnetcraft.common.blocks.entities.LodestoneEntity;
import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
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

import java.util.Objects;

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
        return checkType(type, BlockRegistries.LODESTONE_ENTITY, (world1, pos, state1, blockEntity) -> LodestoneEntity.tick(world1, pos));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        assert world != null;
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("redstone", true);
        nbt.putInt("direction", 0);
        Objects.requireNonNull(world.getBlockEntity(pos)).readNbt(nbt);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        boolean isLodestone = world.getBlockState(pos).isOf(BlockRegistries.LODESTONE_BLOCK);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        boolean redstone = Objects.requireNonNull(blockEntity).createNbt().getBoolean("redstone");
        double dis = Objects.requireNonNull(blockEntity).createNbt().getDouble("dis");
        int direction = Objects.requireNonNull(blockEntity).createNbt().getInt("direction");
        NbtCompound nbt = new NbtCompound();
        Direction side = hit.getSide();
        if (side == Direction.UP) {
            if (!world.isClient) {
                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
                if (screenHandlerFactory != null) {
                    player.openHandledScreen(screenHandlerFactory);
                }
            }
        } else {
            if (isLodestone && hand == Hand.MAIN_HAND) {
                if (player.isSneaky()) {
                    if (redstone) {
                        nbt.putBoolean("redstone", false);
                        nbt.putDouble("dis", 0);
                    } else if (dis < ModConfig.getConfig().value.lodestoneMaxDis) {
                        nbt.putDouble("dis", dis + ModConfig.getConfig().value.disEachClick);
                    } else {
                        nbt.putBoolean("redstone", true);
                        nbt.putDouble("dis", 0);
                    }
                } else {
                    if (direction < 6 && direction >= 0) {
                        nbt.putInt("direction", direction + 1);
                    } else {
                        nbt.putInt("direction", 0);
                    }
                }
                if (!world.isClient) {
                    blockEntity.readNbt(nbt);
                    blockEntity.markDirty();
                }
                showState(world, pos, player);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LodestoneEntity) {
                ItemScatterer.spawn(world, pos, (LodestoneEntity) blockEntity);
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

    public static Text showState(World world, BlockPos pos, @Nullable PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        boolean redstone = Objects.requireNonNull(blockEntity).createNbt().getBoolean("redstone");
        double dis = Objects.requireNonNull(blockEntity).createNbt().getDouble("dis");
        int direction = Objects.requireNonNull(blockEntity).createNbt().getInt("direction");
        String directionText = "text.magnetcraft.message.direction." + direction;
        Text text;
        if (!redstone) {
            text = Text.translatable("block.magnetcraft.lodestone.tooltip.3", dis)
                    .append(Text.translatable(directionText));
        } else {
            text = Text.translatable("text.magnetcraft.message.redstone_mode")
                    .append(Text.translatable("block.magnetcraft.lodestone.tooltip.3", dis))
                    .append(Text.translatable(directionText));
        }
        if (!world.isClient && player != null) player.sendMessage(text, true);
        return text;
    }
}
