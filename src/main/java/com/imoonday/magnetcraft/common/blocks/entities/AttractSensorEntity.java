package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.api.EntityAttractNbt;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashMap;

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
        HashMap<Double, Direction> disList = new HashMap<>();
        world.getOtherEntities(null, Box.from(new BlockBox(pos)).expand(30), otherEntity -> ((EntityAttractNbt) otherEntity).isAttracting())
                .forEach(otherEntity -> disList.put(Math.sqrt(pos.getSquaredDistance(otherEntity.getPos())), Direction.getFacing(pos.getX() - otherEntity.getX(), pos.getY() - otherEntity.getY(), pos.getZ() - otherEntity.getZ())));
        if (!disList.isEmpty()) {
            int minDis = Collections.min(disList.keySet()).intValue();
            entity.direction = disList.getOrDefault(Collections.min(disList.keySet()), Direction.UP);
            entity.power = 30 - minDis >= 0 ? (30 - minDis) / 2 : 0;
        } else {
            entity.direction = Direction.UP;
            entity.power = 0;
        }
        world.updateNeighborsAlways(pos, state.getBlock());
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
