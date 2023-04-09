package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ElectromagneticShuttleEntity extends BlockEntity {

    private BlockPos teleportPos;
    private boolean valid;

    public ElectromagneticShuttleEntity(BlockPos pos, BlockState state) {
        super(BlockRegistries.ELECTROMAGNETIC_SHUTTLE_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, ElectromagneticShuttleEntity entity) {
        if (entity.valid) {
            BlockPos blockPos = entity.teleportPos;
            BlockState blockState = world.getBlockState(blockPos);
            entity.valid = blockState.isOf(state.getBlock()) && !blockPos.equals(pos);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("posX") && nbt.contains("posY") && nbt.contains("posZ")) {
            this.teleportPos = new BlockPos(nbt.getInt("posX"), nbt.getInt("posY"), nbt.getInt("posZ"));
        }
        if (nbt.contains("valid")) {
            this.valid = nbt.getBoolean("valid");
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        if (this.teleportPos == null) {
            this.teleportPos = new BlockPos(0, 0, 0);
        }
        nbt.putInt("posX", this.teleportPos.getX());
        nbt.putInt("posY", this.teleportPos.getY());
        nbt.putInt("posZ", this.teleportPos.getZ());
        nbt.putBoolean("valid", this.valid);
        super.writeNbt(nbt);
    }

    public BlockPos getTeleportPos() {
        return this.teleportPos;
    }

    public void setTeleportPos(BlockPos teleportPos) {
        this.teleportPos = teleportPos;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
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
