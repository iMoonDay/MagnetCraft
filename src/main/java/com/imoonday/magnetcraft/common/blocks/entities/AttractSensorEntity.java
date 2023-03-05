package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.EffectRegistries;
import com.imoonday.magnetcraft.registries.common.ItemRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
        int degaussingDis = ModConfig.getConfig().value.degaussingDis;
        ArrayList<Double> disList = new ArrayList<>();
        world.getOtherEntities(null, Box.from(new BlockBox(pos)).expand(30), e -> (e.getScoreboardTags().contains("MagnetCraft.isAttracting") && e.world.getOtherEntities(null, e.getBoundingBox().expand(degaussingDis), o -> (o instanceof LivingEntity && ((LivingEntity) o).hasStatusEffect(EffectRegistries.DEGAUSSING_EFFECT) && o.distanceTo(e) <= degaussingDis && !o.isSpectator())).isEmpty() && (!(e instanceof LivingEntity) || !((LivingEntity) e).hasStatusEffect(EffectRegistries.UNATTRACT_EFFECT)) && (e instanceof PlayerEntity || e.world.getOtherEntities(null, e.getBoundingBox().expand(degaussingDis), o -> (o instanceof PlayerEntity && ((PlayerEntity) o).getInventory().containsAny(stack -> stack.isOf(ItemRegistries.PORTABLE_DEMAGNETIZER_ITEM) && stack.getNbt() != null && stack.getNbt().getBoolean("Enable")))).isEmpty()))).forEach(e -> disList.add(Math.sqrt(pos.getSquaredDistance(e.getPos()))));
        int minDis = !disList.isEmpty() ? Collections.min(disList).intValue() : 30;
        entity.power = 30 - minDis >= 0 ? (30 - minDis) / 2 : 0;
        world.updateNeighborsAlways(pos, state.getBlock());
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("Power", this.power);
    }
}
