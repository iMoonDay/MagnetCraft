package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class ElectromagneticShuttleBaseEntity extends BlockEntity {

    private boolean connecting = false;
    private ArrayList<Vec3d> route = new ArrayList<>();

    public ElectromagneticShuttleBaseEntity(BlockPos pos, BlockState state) {
        super(BlockRegistries.ELECTROMAGNETIC_SHUTTLE_BASE_ENTITY, pos, state);
    }

    public static void tick(ElectromagneticShuttleBaseEntity entity) {
        if (!entity.connecting) {
            entity.setRoute(new ArrayList<>());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Connecting")) {
            this.connecting = nbt.getBoolean("Connecting");
        }
        if (nbt.contains("Route")) {
            ArrayList<Vec3d> tickPosList = new ArrayList<>();
            for (NbtElement nbtElement : nbt.getList("Route", NbtElement.COMPOUND_TYPE)) {
                NbtCompound nbtCompound = (NbtCompound) nbtElement;
                tickPosList.add(new Vec3d(nbtCompound.getDouble("x"), nbtCompound.getDouble("y"), nbtCompound.getDouble("z")));
            }
            this.route = tickPosList;
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        if (this.route == null) {
            this.route = new ArrayList<>();
        }
        nbt.putBoolean("Connecting", this.connecting);
        NbtList tickPosList = new NbtList();
        for (Vec3d pos : this.route) {
            NbtCompound tickPos = new NbtCompound();
            tickPos.putDouble("x", pos.x);
            tickPos.putDouble("y", pos.y);
            tickPos.putDouble("z", pos.z);
            tickPosList.add(tickPos);
        }
        nbt.put("Route", tickPosList);
        super.writeNbt(nbt);
    }

    public boolean isConnecting() {
        return this.connecting;
    }

    public void setConnecting(boolean connecting) {
        this.connecting = connecting;
    }

    public ArrayList<Vec3d> getRoute() {
        return this.route;
    }

    public void setRoute(ArrayList<Vec3d> route) {
        this.route = route;
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
