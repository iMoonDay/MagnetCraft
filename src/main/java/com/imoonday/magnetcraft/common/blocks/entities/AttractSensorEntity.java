package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.common.tags.BlockTags;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

public class AttractSensorEntity extends BlockEntity {

    private int power;
    private Direction direction;
    private boolean hasDirection;

    public AttractSensorEntity(BlockPos pos, BlockState state) {
        super(BlockRegistries.ATTRACT_SENSOR_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AttractSensorEntity entity) {
        if (world.isClient) {
            return;
        }
        HashMap<Double, Direction> disList = world.getOtherEntities(null, Box.from(new BlockBox(pos)).expand(30), entity1 -> entity1.isAttracting() && !blocked(world, pos, entity1)).stream().collect(Collectors.toMap(otherEntity -> Math.sqrt(pos.getSquaredDistance(otherEntity.getPos())), otherEntity -> Direction.getFacing(pos.getX() - otherEntity.getX(), pos.getY() - otherEntity.getY(), pos.getZ() - otherEntity.getZ()), (a, b) -> b, HashMap::new));
        if (disList.isEmpty()) {
            entity.direction = Direction.UP;
            entity.power = 0;
        } else {
            int minDis = Collections.min(disList.keySet()).intValue();
            entity.direction = disList.getOrDefault(Collections.min(disList.keySet()), Direction.UP);
            entity.power = 30 - minDis >= 0 ? (30 - minDis) / 2 : 0;
        }
        world.updateNeighborsAlways(pos, state.getBlock());
    }

    private static boolean blocked(World world, BlockPos pos, Entity entity) {
        for (double d = 0; d <= 1; d += 0.01) {
            Vec3d entityPos = entity.getEyePos();
            Vec3d newPos = pos.toCenterPos().add(entityPos.subtract(pos.toCenterPos()).multiply(d));
            BlockPos newBlockPos = BlockPos.ofFloored(newPos);
            BlockState newBlockState = world.getBlockState(newBlockPos);
            if (newBlockState.isIn(BlockTags.BLOCK_ATTRACT_BLOCKS)) {
                VoxelShape voxelShape = newBlockState.getOutlineShape(entity.world, newBlockPos, createHoldingShapeContext());
                VoxelShape voxelShape2 = voxelShape.offset(newBlockPos.getX(), newBlockPos.getY(), newBlockPos.getZ());
                boolean collide = VoxelShapes.matchesAnywhere(voxelShape2, VoxelShapes.cuboid(new Box(newBlockPos)), BooleanBiFunction.AND);
                if (collide) {
                    return true;
                }
            }
        }
        return false;
    }

    @NotNull
    public static ShapeContext createHoldingShapeContext() {
        return new ShapeContext() {
            @Override
            public boolean isDescending() {
                return false;
            }

            @Override
            public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
                return false;
            }

            @Override
            public boolean isHolding(Item item) {
                return true;
            }

            @Override
            public boolean canWalkOnFluid(FluidState stateAbove, FluidState state) {
                return false;
            }
        };
    }

    public Direction getDirection() {
        return direction;
    }

    public int getPower() {
        return power;
    }

    public boolean isHasDirection() {
        return hasDirection;
    }

    public void setHasDirection(boolean hasDirection) {
        this.hasDirection = hasDirection;
    }

}
