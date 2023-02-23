package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.config.ModConfig;
import com.imoonday.magnetcraft.methods.AttractMethod;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class LodestoneEntity extends BlockEntity {

    private boolean redstone;
    private double dis2;
    private int direction;

    public LodestoneEntity(BlockPos pos, BlockState state) {
        super(BlockRegistries.LODESTONE_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        NbtCompound nbt = Objects.requireNonNull(world.getBlockEntity(pos)).createNbt();
        double dis = nbt.getDouble("dis") <= ModConfig.getConfig().value.lodestoneMaxDis ? nbt.getDouble("dis") + 1 : ModConfig.getConfig().value.lodestoneMaxDis + 1;
        boolean enable = nbt.getBoolean("enable");
        int direction1 = nbt.getInt("direction");
        Vec3d centerPos = pos.toCenterPos();
        centerPos = centerPos.add(0, 0.5, 0);
        if (enable) {
            centerPos = switch (direction1) {
                case 1 -> centerPos.add(0, 0, 1);
                case 2 -> centerPos.add(-1, 0, 0);
                case 3 -> centerPos.add(0, 0, -1);
                case 4 -> centerPos.add(1, 0, 0);
                case 5 -> centerPos.add(0, 1, 0);
                case 6 -> centerPos.add(0, -1, 0);
                default -> centerPos;
            };
            AttractMethod.attractItems(world, centerPos, dis);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        boolean hasPowered = world != null && world.isReceivingRedstonePower(pos);
        int disPerPower = ModConfig.getConfig().value.disPerPower;
        double dis = world.getReceivedRedstonePower(pos) * disPerPower;
        if (redstone) {
            nbt.putBoolean("redstone", true);
            nbt.putBoolean("enable", hasPowered);
            nbt.putDouble("dis", dis);
        } else {
            nbt.putBoolean("redstone", false);
            nbt.putBoolean("enable", dis2 > 1);
            nbt.putDouble("dis", dis2);
        }
        nbt.putInt("direction", direction);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        redstone = nbt.getBoolean("redstone");
        dis2 = nbt.getDouble("dis");
        direction = nbt.getInt("direction");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
