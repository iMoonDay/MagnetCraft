package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.methods.AttractMethods;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;

public class AttractSensorEntity extends BlockEntity {

    private int power;

    public AttractSensorEntity(BlockPos pos, BlockState state) {
        super(BlockRegistries.ATTRACT_SENSOR_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AttractSensorEntity entity) {
        if (world.isClient) {
            return;
        }
        ArrayList<Double> disList = new ArrayList<>();
        world.getOtherEntities(null, Box.from(new BlockBox(pos)).expand(30), AttractMethods::isAttracting).forEach(e -> disList.add(Math.sqrt(pos.getSquaredDistance(e.getPos()))));
        int minDis = !disList.isEmpty() ? Collections.min(disList).intValue() : 30;
        entity.power = 30 - minDis >= 0 ? (30 - minDis) / 2 : 0;
        world.updateNeighborsAlways(pos, state.getBlock());
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("Power", this.power);
    }
}
