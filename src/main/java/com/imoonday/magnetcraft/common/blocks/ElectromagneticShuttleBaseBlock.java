package com.imoonday.magnetcraft.common.blocks;

import com.imoonday.magnetcraft.common.blocks.entities.ElectromagneticShuttleBaseEntity;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class ElectromagneticShuttleBaseBlock extends BlockWithEntity {

    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final String RECORDING = "Recording";
    public static final String SOURCE_POS = "SourcePos";
    public static final String POS = "Pos";

    public ElectromagneticShuttleBaseBlock(Settings settings) {
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
        return new ElectromagneticShuttleBaseEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlockRegistries.ELECTROMAGNETIC_SHUTTLE_BASE_ENTITY, (world1, pos, state1, entity) -> ElectromagneticShuttleBaseEntity.tick(world1, entity));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.scheduleBlockTick(pos, this, 1);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0, 0, 0, 16, 12, 16);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, state.with(POWERED, world.isReceivingRedstonePower(pos)));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        world.scheduleBlockTick(pos, this, 1);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        world.scheduleBlockTick(pos, this, 1);
        return state;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        ItemStack stack = player.getStackInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (stack.isOf(ItemRegistries.ELECTROMAGNETIC_RECORDER_ITEM)) {
            Vec3d center = new Vec3d(pos.toCenterPos().getX(), state.getCollisionShape(world, pos).offset(pos.getX(), pos.getY(), pos.getZ()).getMax(Direction.Axis.Y), pos.toCenterPos().getZ());
            if (!player.getPos().isInRange(center, 2)) {
                player.sendMessage(Text.literal("距离过远"), true);
                return ActionResult.SUCCESS;
            }
            if (!state.get(POWERED)) {
                player.sendMessage(Text.literal("未充能"), true);
                return ActionResult.SUCCESS;
            }
            if (!player.getPos().equals(center)) {
                player.refreshPositionAfterTeleport(center);
                player.world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.VOICE, 1, 1);
            }
            stack.getOrCreateNbt().putBoolean(RECORDING, true);
            stack.getOrCreateNbt().putIntArray(SOURCE_POS, new int[]{pos.getX(), pos.getY(), pos.getZ()});
            stack.getOrCreateNbt().put(POS, new NbtList());
            if (blockEntity instanceof ElectromagneticShuttleBaseEntity base) {
                if (base.isConnecting()) {
                    base.setConnecting(false);
                    base.clearEntity((ServerWorld) world);
                }
            }
            return ActionResult.SUCCESS;
        }
        if (blockEntity instanceof ElectromagneticShuttleBaseEntity base) {
            if (base.isConnecting()) {
                String string = base.getRoute().get(base.getRoute().size() - 1).toString();
                player.sendMessage(Text.literal("当前绑定坐标: " + string), true);
            } else {
                player.sendMessage(Text.literal("当前未绑定坐标"), true);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return state.get(POWERED) ? PistonBehavior.BLOCK : PistonBehavior.NORMAL;
    }

}
