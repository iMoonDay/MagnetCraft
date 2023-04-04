package com.imoonday.magnetcraft.common.blocks;

import com.imoonday.magnetcraft.common.blocks.entities.LodestoneEntity;
import com.imoonday.magnetcraft.common.blocks.entities.MagneticShulkerBackpackEntity;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MagneticShulkerBackpackBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty HANGING = BooleanProperty.of("hanging");
    public static final Identifier CONTENTS = new Identifier("contents");

    public MagneticShulkerBackpackBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(HANGING, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, HANGING);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MagneticShulkerBackpackEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlockRegistries.MAGNETIC_SHULKER_BACKPACK_ENTITY, (world1, pos, state1, entity) -> MagneticShulkerBackpackEntity.tick(world1, pos, state1));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        return state.get(HANGING) ? getShapeHanging(direction) : getShapeOnGround(direction);
    }

    private VoxelShape getShapeOnGround(Direction direction) {
        VoxelShape shape = VoxelShapes.empty();
        switch (direction) {
            case SOUTH -> {
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.375, 0.875, 0.5625, 0.625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.5625, 0.375, 0.8125, 0.625, 0.5625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0.625, 0.4375, 0.6875, 0.6875, 0.5));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.625, 0.4375, 0.375, 0.6875, 0.5));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.6875, 0.4375, 0.625, 0.75, 0.5));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.5625, 0.25, 0.75, 0.625, 0.375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.5625, 0.25, 0.3125, 0.625, 0.375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.25, 0.125, 0.3125, 0.5, 0.1875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.5, 0.1875, 0.75, 0.5625, 0.25));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.5, 0.1875, 0.3125, 0.5625, 0.25));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.25, 0.125, 0.75, 0.5, 0.1875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.1875, 0.1875, 0.3125, 0.25, 0.25));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.1875, 0.1875, 0.75, 0.25, 0.25));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.0625, 0.3125, 0.3125, 0.125, 0.375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.0625, 0.3125, 0.75, 0.125, 0.375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.125, 0.25, 0.3125, 0.1875, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.125, 0.25, 0.75, 0.1875, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.625, 0.875, 0.5, 0.6875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.5, 0.625, 0.8125, 0.5625, 0.6875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0, 0.6875, 0.8125, 0.5, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.0625, 0.75, 0.75, 0.4375, 0.8125));
            }
            case NORTH -> {
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.375, 0.875, 0.5625, 0.625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.5625, 0.4375, 0.8125, 0.625, 0.625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0.625, 0.5, 0.6875, 0.6875, 0.5625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.625, 0.5, 0.375, 0.6875, 0.5625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.6875, 0.5, 0.625, 0.75, 0.5625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.5625, 0.625, 0.75, 0.625, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.5625, 0.625, 0.3125, 0.625, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.25, 0.8125, 0.3125, 0.5, 0.875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.5, 0.75, 0.75, 0.5625, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.5, 0.75, 0.3125, 0.5625, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.25, 0.8125, 0.75, 0.5, 0.875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.1875, 0.75, 0.3125, 0.25, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.1875, 0.75, 0.75, 0.25, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.0625, 0.625, 0.3125, 0.125, 0.6875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.0625, 0.625, 0.75, 0.125, 0.6875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.125, 0.6875, 0.3125, 0.1875, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.125, 0.6875, 0.75, 0.1875, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0, 0.3125, 0.875, 0.5, 0.375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.5, 0.3125, 0.8125, 0.5625, 0.375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0, 0.25, 0.8125, 0.5, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.0625, 0.1875, 0.75, 0.4375, 0.25));
            }
            case EAST -> {
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0, 0.125, 0.625, 0.5625, 0.875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.5625, 0.1875, 0.5625, 0.625, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.625, 0.625, 0.5, 0.6875, 0.6875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.625, 0.3125, 0.5, 0.6875, 0.375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.6875, 0.375, 0.5, 0.75, 0.625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.5625, 0.6875, 0.375, 0.625, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.5625, 0.25, 0.375, 0.625, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.25, 0.25, 0.1875, 0.5, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.5, 0.6875, 0.25, 0.5625, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.5, 0.25, 0.25, 0.5625, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.25, 0.6875, 0.1875, 0.5, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.1875, 0.25, 0.25, 0.25, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.1875, 0.6875, 0.25, 0.25, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.0625, 0.25, 0.375, 0.125, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.0625, 0.6875, 0.375, 0.125, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.125, 0.25, 0.3125, 0.1875, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.125, 0.6875, 0.3125, 0.1875, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0, 0.125, 0.6875, 0.5, 0.875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0.5, 0.1875, 0.6875, 0.5625, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0, 0.1875, 0.75, 0.5, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.0625, 0.25, 0.8125, 0.4375, 0.75));
            }
            case WEST -> {
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0, 0.125, 0.625, 0.5625, 0.875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.5625, 0.1875, 0.625, 0.625, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.625, 0.625, 0.5625, 0.6875, 0.6875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.625, 0.3125, 0.5625, 0.6875, 0.375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5, 0.6875, 0.375, 0.5625, 0.75, 0.625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0.5625, 0.6875, 0.75, 0.625, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0.5625, 0.25, 0.75, 0.625, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.25, 0.25, 0.875, 0.5, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.5, 0.6875, 0.8125, 0.5625, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.5, 0.25, 0.8125, 0.5625, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.25, 0.6875, 0.875, 0.5, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.1875, 0.25, 0.8125, 0.25, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.1875, 0.6875, 0.8125, 0.25, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0.0625, 0.25, 0.6875, 0.125, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0.0625, 0.6875, 0.6875, 0.125, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.125, 0.25, 0.75, 0.1875, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.125, 0.6875, 0.75, 0.1875, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0, 0.125, 0.375, 0.5, 0.875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.5, 0.1875, 0.375, 0.5625, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0, 0.1875, 0.3125, 0.5, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.0625, 0.25, 0.25, 0.4375, 0.75));
            }
        }
        return shape;
    }

    private static VoxelShape getShapeHanging(Direction direction) {
        VoxelShape shape = VoxelShapes.empty();
        switch (direction) {
            case SOUTH -> {
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.0625, 0, 0.875, 0.625, 0.25));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.625, 0, 0.8125, 0.6875, 0.1875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0.6875, 0.0625, 0.6875, 0.8125, 0.125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.6875, 0.0625, 0.375, 0.8125, 0.125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.8125, 0.0625, 0.625, 0.875, 0.125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.0625, 0.25, 0.875, 0.5625, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.5625, 0.25, 0.8125, 0.625, 0.3125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.0625, 0.3125, 0.8125, 0.5625, 0.375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.125, 0.375, 0.75, 0.5, 0.4375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.8125, 0.125, 0.5625, 0.875, 0.1875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.75, 0, 0.5625, 0.8125, 0.1875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.8125, 0, 0.5625, 0.9375, 0.0625));
            }
            case NORTH -> {
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.0625, 0.75, 0.875, 0.625, 1));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.625, 0.8125, 0.8125, 0.6875, 1));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.6875, 0.875, 0.375, 0.8125, 0.9375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0.6875, 0.875, 0.6875, 0.8125, 0.9375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.8125, 0.875, 0.625, 0.875, 0.9375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.0625, 0.6875, 0.875, 0.5625, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.5625, 0.6875, 0.8125, 0.625, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.1875, 0.0625, 0.625, 0.8125, 0.5625, 0.6875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.125, 0.5625, 0.75, 0.5, 0.625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.8125, 0.8125, 0.5625, 0.875, 0.875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.75, 0.8125, 0.5625, 0.8125, 1));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.4375, 0.8125, 0.9375, 0.5625, 0.9375, 1));
            }
            case EAST -> {
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.0625, 0.125, 0.25, 0.625, 0.875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.625, 0.1875, 0.1875, 0.6875, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.6875, 0.3125, 0.125, 0.8125, 0.375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.6875, 0.625, 0.125, 0.8125, 0.6875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.0625, 0.8125, 0.375, 0.125, 0.875, 0.625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.0625, 0.125, 0.3125, 0.5625, 0.875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.25, 0.5625, 0.1875, 0.3125, 0.625, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.3125, 0.0625, 0.1875, 0.375, 0.5625, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.375, 0.125, 0.25, 0.4375, 0.5, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.125, 0.8125, 0.4375, 0.1875, 0.875, 0.5625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.75, 0.4375, 0.1875, 0.8125, 0.5625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0.8125, 0.4375, 0.0625, 0.9375, 0.5625));
            }
            case WEST -> {
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.75, 0.0625, 0.125, 1, 0.625, 0.875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.625, 0.1875, 1, 0.6875, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0.6875, 0.625, 0.9375, 0.8125, 0.6875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0.6875, 0.3125, 0.9375, 0.8125, 0.375));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.875, 0.8125, 0.375, 0.9375, 0.875, 0.625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.0625, 0.125, 0.75, 0.5625, 0.875));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.6875, 0.5625, 0.1875, 0.75, 0.625, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.625, 0.0625, 0.1875, 0.6875, 0.5625, 0.8125));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.5625, 0.125, 0.25, 0.625, 0.5, 0.75));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.8125, 0.4375, 0.875, 0.875, 0.5625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.8125, 0.75, 0.4375, 1, 0.8125, 0.5625));
                shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0.9375, 0.8125, 0.4375, 1, 0.9375, 0.5625));
            }
        }
        return shape;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        if (player.isSpectator()) {
            return ActionResult.CONSUME;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MagneticShulkerBackpackEntity) {
            if (player.isSneaking()) {
                getDroppedStacks(state, (ServerWorld) world, pos, blockEntity).forEach(stack -> {
                    if (stack.isOf(BlockRegistries.MAGNETIC_SHULKER_BACKPACK_ITEM) && player.getEquippedStack(EquipmentSlot.CHEST).isEmpty()) {
                        player.equipStack(EquipmentSlot.CHEST, stack);
                    } else {
                        player.getInventory().offerOrDrop(stack);
                    }
                });
                player.getInventory().markDirty();
                world.removeBlock(pos, false);
                world.removeBlockEntity(pos);
            } else {
                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
                if (screenHandlerFactory != null) {
                    player.openHandledScreen(screenHandlerFactory);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MagneticShulkerBackpackEntity && player.isSneaking() && player.getMainHandStack().isEmpty()) {
            getDroppedStacks(state, (ServerWorld) world, pos, blockEntity).forEach(stack -> {
                if (stack.isOf(BlockRegistries.MAGNETIC_SHULKER_BACKPACK_ITEM) && player.getMainHandStack().isEmpty()) {
                    player.setStackInHand(Hand.MAIN_HAND, stack);
                } else {
                    player.getInventory().offerOrDrop(stack);
                }
            });
            player.getInventory().markDirty();
            world.removeBlock(pos, false);
            world.removeBlockEntity(pos);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MagneticShulkerBackpackEntity backpack) {
            if (!world.isClient && player.isCreative() && !backpack.isEmpty()) {
                ItemStack itemStack = new ItemStack(BlockRegistries.MAGNETIC_SHULKER_BACKPACK_ITEM);
                blockEntity.setStackNbt(itemStack);
                if (backpack.hasCustomName()) {
                    itemStack.setCustomName(backpack.getCustomName());
                }
                ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            } else {
                backpack.checkLootInteraction(player);
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        BlockEntity blockEntity = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof MagneticShulkerBackpackEntity backpack) {
            builder = builder.putDrop(CONTENTS, (context, consumer) -> {
                for (int i = 0; i < backpack.size(); ++i) {
                    consumer.accept(backpack.getStack(i));
                }
            });
        }
        return super.getDroppedStacks(state, builder);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = super.getPlacementState(ctx);
        Direction opposite = ctx.getHorizontalPlayerFacing().getOpposite();
        Direction side = ctx.getSide();
        BlockPos blockPos = ctx.getBlockPos().offset(side.getOpposite());
        World world = ctx.getWorld();
        BlockState state = world.getBlockState(blockPos);
        boolean hanging = side != Direction.UP && side != Direction.DOWN && state.isSideSolidFullSquare(world, blockPos, side);
        if (blockState != null) {
            return blockState.with(FACING, hanging ? side : opposite).with(HANGING, hanging);
        }
        return super.getPlacementState(ctx);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(HANGING) && direction == state.get(FACING).getOpposite()) {
            return state.with(HANGING, neighborState.isSideSolidFullSquare(world, neighborPos, direction.getOpposite()));
        }
        return state;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName() && world.getBlockEntity(pos) instanceof MagneticShulkerBackpackEntity backpack) {
            backpack.setCustomName(itemStack.getName());
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof ItemEntity item) {
            if (world.getBlockEntity(pos) instanceof MagneticShulkerBackpackEntity backpack && backpack.canInsert(0, item.getStack(), null)) {
                LodestoneEntity.insertItemIntoInventory(item, backpack.getInvStackList());
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MagneticShulkerBackpackEntity) {
            world.updateComparators(pos, state.getBlock());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

}
