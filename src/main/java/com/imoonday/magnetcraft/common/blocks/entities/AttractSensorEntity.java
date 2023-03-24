package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.api.MagnetCraftEntity;
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
import java.util.stream.Collectors;

/**
 * @author iMoonDay
 */
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
        HashMap<Double, Direction> disList = world.getOtherEntities(null, Box.from(new BlockBox(pos)).expand(30), MagnetCraftEntity::isAttracting).stream().collect(Collectors.toMap(otherEntity -> Math.sqrt(pos.getSquaredDistance(otherEntity.getPos())), otherEntity -> Direction.getFacing(pos.getX() - otherEntity.getX(), pos.getY() - otherEntity.getY(), pos.getZ() - otherEntity.getZ()), (a, b) -> b, HashMap::new));
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
