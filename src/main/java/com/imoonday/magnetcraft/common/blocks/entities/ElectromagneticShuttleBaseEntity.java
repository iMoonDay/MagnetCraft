package com.imoonday.magnetcraft.common.blocks.entities;

import com.imoonday.magnetcraft.common.entities.entrance.ShuttleEntranceEntity;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.UUID;

import static com.imoonday.magnetcraft.common.items.magnets.CreatureMagnetItem.EMPTY_UUID;

public class ElectromagneticShuttleBaseEntity extends BlockEntity {

    public static final String CONNECTING = "Connecting";
    public static final String ROUTE = "Route";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String SOURCE_ENTITY = "SourceEntity";
    public static final String CONNECTED_ENTITY = "ConnectedEntity";
    private boolean connecting = false;
    private ArrayList<Vec3d> route = new ArrayList<>();
    private UUID sourceEntity = EMPTY_UUID;
    private UUID connectedEntity = EMPTY_UUID;
    private Vec3d sourcePos;
    private Vec3d connectedPos;

    public ElectromagneticShuttleBaseEntity(BlockPos pos, BlockState state) {
        super(BlockRegistries.ELECTROMAGNETIC_SHUTTLE_BASE_ENTITY, pos, state);
    }

    public static void tick(World world, ElectromagneticShuttleBaseEntity entity) {
        if (entity.connecting && world instanceof ServerWorld serverWorld) {
            Entity connectedEntity = serverWorld.getEntity(entity.getConnectedEntity());
            Entity sourceEntity = serverWorld.getEntity(entity.getSourceEntity());
            if (sourceEntity == null || connectedEntity == null) {
                if (entity.sourcePos == null || entity.connectedPos == null) {
                    entity.clearEntity(serverWorld);
                    entity.setConnecting(false);
                }
                serverWorld.getChunkManager().setChunkForced(new ChunkPos(BlockPos.ofFloored(entity.sourcePos)), true);
                serverWorld.getChunkManager().setChunkForced(new ChunkPos(BlockPos.ofFloored(entity.connectedPos)), true);
                connectedEntity = serverWorld.getEntity(entity.getConnectedEntity());
                sourceEntity = serverWorld.getEntity(entity.getSourceEntity());
                if (sourceEntity == null || connectedEntity == null) {
                    entity.clearEntity(serverWorld);
                    entity.setConnecting(false);
                }
            }
        }
        if (!entity.connecting) {
            initialize(entity);
        }
    }

    private static void initialize(ElectromagneticShuttleBaseEntity entity) {
        entity.setRoute(new ArrayList<>());
        entity.setSourceEntity(EMPTY_UUID);
        entity.setConnectedEntity(EMPTY_UUID);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains(CONNECTING)) {
            this.connecting = nbt.getBoolean(CONNECTING);
        }
        if (nbt.contains(ROUTE)) {
            ArrayList<Vec3d> tickPosList = new ArrayList<>();
            for (NbtElement nbtElement : nbt.getList(ROUTE, NbtElement.COMPOUND_TYPE)) {
                NbtCompound nbtCompound = (NbtCompound) nbtElement;
                tickPosList.add(new Vec3d(nbtCompound.getDouble(X), nbtCompound.getDouble(Y), nbtCompound.getDouble(Z)));
            }
            this.route = tickPosList;
        }
        if (nbt.contains(SOURCE_ENTITY)) {
            this.sourceEntity = nbt.getUuid(SOURCE_ENTITY);
        }
        if (nbt.contains(CONNECTED_ENTITY)) {
            this.connectedEntity = nbt.getUuid(CONNECTED_ENTITY);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        if (this.route == null) {
            this.route = new ArrayList<>();
        }
        nbt.putBoolean(CONNECTING, this.connecting);
        NbtList tickPosList = new NbtList();
        for (Vec3d pos : this.route) {
            NbtCompound tickPos = new NbtCompound();
            tickPos.putDouble(X, pos.x);
            tickPos.putDouble(Y, pos.y);
            tickPos.putDouble(Z, pos.z);
            tickPosList.add(tickPos);
        }
        nbt.put(ROUTE, tickPosList);
        nbt.putUuid(SOURCE_ENTITY, this.sourceEntity);
        nbt.putUuid(CONNECTED_ENTITY, this.connectedEntity);
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

    public UUID getSourceEntity() {
        return this.sourceEntity;
    }

    public void setSourceEntity(UUID sourceEntity) {
        this.sourceEntity = sourceEntity;
    }

    public void setSourceEntity(ShuttleEntranceEntity sourceEntity) {
        this.sourceEntity = sourceEntity.getUuid();
        this.sourcePos = sourceEntity.getPos();
    }

    public UUID getConnectedEntity() {
        return this.connectedEntity;
    }

    public void setConnectedEntity(UUID connectedEntity) {
        this.connectedEntity = connectedEntity;
    }

    public void setConnectedEntity(ShuttleEntranceEntity connectedEntity) {
        this.connectedEntity = connectedEntity.getUuid();
        this.connectedPos = connectedEntity.getPos();
    }

    public void clearEntity(ServerWorld world) {
        Entity sourceEntity = world.getEntity(this.sourceEntity);
        Entity connectedEntity = world.getEntity(this.connectedEntity);
        if (sourceEntity != null) {
            sourceEntity.discard();
        }
        if (connectedEntity != null) {
            connectedEntity.discard();
        }
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
