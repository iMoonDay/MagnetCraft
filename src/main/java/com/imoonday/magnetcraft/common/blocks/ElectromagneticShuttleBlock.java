package com.imoonday.magnetcraft.common.blocks;

import com.imoonday.magnetcraft.common.blocks.entities.ElectromagneticShuttleEntity;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class ElectromagneticShuttleBlock extends BlockWithEntity {

    public static final BooleanProperty POWERED = Properties.POWERED;

    public ElectromagneticShuttleBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ElectromagneticShuttleEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlockRegistries.ELECTROMAGNETIC_SHUTTLE_ENTITY, ElectromagneticShuttleEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ElectromagneticShuttleEntity entity) {
            entity.setTeleportPos(new BlockPos(0, 0, 0));
            entity.setValid(false);
            entity.markDirty();
        }
        world.scheduleBlockTick(pos, this, 1);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, state.with(POWERED, world.isReceivingRedstonePower(pos)));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        world.scheduleBlockTick(pos, this, 1);
        return state;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.isOf(ItemRegistries.ELECTROMAGNET_ITEM)) {
            if (stack.getNbt() == null || !stack.getNbt().contains("x") || !stack.getNbt().contains("y") || !stack.getNbt().contains("z")) {
                stack.getOrCreateNbt().putInt("x", pos.getX());
                stack.getOrCreateNbt().putInt("y", pos.getY());
                stack.getOrCreateNbt().putInt("z", pos.getZ());
                return ActionResult.SUCCESS;
            } else {
                BlockPos blockPos = new BlockPos(stack.getOrCreateNbt().getInt("x"), stack.getOrCreateNbt().getInt("y"), stack.getOrCreateNbt().getInt("z"));
                if (blockPos.equals(pos)) {
                    return ActionResult.PASS;
                }
                BlockEntity blockEntity = world.getBlockEntity(blockPos);
                if (blockEntity instanceof ElectromagneticShuttleEntity entity && world.getBlockEntity(pos) instanceof ElectromagneticShuttleEntity entity1) {
                    if (entity1.getTeleportPos().equals(blockPos)) {
                        entity.setValid(false);
                    }
                    entity1.setTeleportPos(blockPos);
                    entity1.setValid(true);
                    entity1.markDirty();
                    stack.getOrCreateNbt().remove("x");
                    stack.getOrCreateNbt().remove("y");
                    stack.getOrCreateNbt().remove("z");
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!state.get(POWERED)) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ElectromagneticShuttleEntity shuttleEntity) {
            BlockPos blockPos = shuttleEntity.getTeleportPos();
            if (!shuttleEntity.isValid() || !world.getBlockState(blockPos).isOf(this) || !world.getBlockState(blockPos).get(POWERED) || !entity.doesNotCollide(blockPos.toCenterPos().x, blockPos.up().getY(), blockPos.toCenterPos().z)) {
                return;
            }
            if (entity.hasControllingPassenger()) {
                return;
            }
            entity.teleport(blockPos.toCenterPos().x, blockPos.up().getY(), blockPos.toCenterPos().z);
            world.playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.VOICE);
        }
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.BLOCK;
    }

}
