package com.imoonday.magnetcraft.common.entities.entrance;

import com.imoonday.magnetcraft.common.blocks.ElectromagneticShuttleBaseBlock;
import com.imoonday.magnetcraft.common.blocks.entities.ElectromagneticShuttleBaseEntity;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.EntityRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShuttleEntranceEntity extends Entity {

    public static final String CONNECTED_ENTITY = "ConnectedEntity";
    public static final String SOURCE = "Source";
    public static final String SOURCE_POS = "SourcePos";
    public static final String CONNECTED_POS = "ConnectedPos";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String UNIQUE_OFFSET = "UniqueOffset";
    protected UUID connectedEntity;
    protected Vec3d connectedPos;
    protected boolean isSource;
    private float uniqueOffset;
    protected static final TrackedData<BlockPos> SOURCE_POS_DATA = DataTracker.registerData(ShuttleEntranceEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

    public ShuttleEntranceEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true;
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.setPitch(0);
    }

    public ShuttleEntranceEntity(World world, Vec3d pos, boolean isSource, BlockPos sourcePos, Vec3d connectedPos, float uniqueOffset) {
        this(EntityRegistries.SHUTTLE_ENTRANCE, world);
        this.setPosition(pos);
        this.isSource = isSource;
        this.uniqueOffset = uniqueOffset;
        this.setSourcePos(sourcePos);
        this.connectedPos = connectedPos;
        this.setChunkForced(true);
    }

    @Override
    public void tick() {
        if (world.isClient) return;
        setChunkForced(true);
        this.setInvisible(this.shouldBeInvisible());
        if (this.invalid()) return;
        if (this.isInvisible()) return;
        ShuttleEntranceEntity connectedEntity = getConnectedEntity();
        PlayerEntity player = connectedEntity == null ? this.world.getClosestPlayer(this, 5) : Optional.ofNullable(this.world.getClosestPlayer(this.isSource ? this : connectedEntity, 5)).orElse(this.world.getClosestPlayer(isSource ? connectedEntity : this, 5));
        if (player != null && !player.getBoundingBox().intersects(this.getBoundingBox())) {
            Vec3d pos = this.getPos();
            double offsetX = player.getX() - pos.x;
            double offsetZ = player.getZ() - pos.z;
            double rotation = MathHelper.atan2(offsetZ, offsetX);
            while (rotation >= (float) Math.PI) {
                rotation -= (float) Math.PI * 2;
            }
            while (rotation < (float) (-Math.PI)) {
                rotation += (float) Math.PI * 2;
            }
            float newYaw = MathHelper.wrapDegrees((float) (rotation * 57.2957763671875) - 90.0f);
            float yaw = MathHelper.lerp(1, this.getYaw(), newYaw);
            this.setYaw(yaw);
        } else {
            float connectedUniqueOffset = connectedEntity != null ? connectedEntity.getUniqueOffset() : 0;
            if (this.uniqueOffset == 0 || this.uniqueOffset != connectedUniqueOffset) {
                if (connectedUniqueOffset != 0) {
                    this.uniqueOffset = connectedUniqueOffset;
                } else {
                    float newUniqueOffset = world.random.nextFloat() + 2.0f;
                    this.uniqueOffset = newUniqueOffset;
                    if (connectedEntity != null) {
                        connectedEntity.setUniqueOffset(newUniqueOffset);
                    }
                }
            }
            float nextYaw = this.getYaw() + this.uniqueOffset;
            while (nextYaw > 180) {
                nextYaw -= 360;
            }
            while (nextYaw < -180) {
                nextYaw = 360 - nextYaw;
            }
            this.setYaw(nextYaw);
        }
        List<Entity> entities = this.world.getOtherEntities(this, this.getBoundingBox());
        for (Entity entity : entities) {
            if (entity.isShuttling()) {
                continue;
            }
            if (entity instanceof PlayerEntity playerEntity && playerEntity.isSneaking()) {
                continue;
            }
            onCollide(entity);
        }
    }

    private boolean invalid() {
        ElectromagneticShuttleBaseEntity blockEntity = getSourceBlockEntity();
        BlockState state = getSourceBlockState();
        boolean invalid = this.connectedPos == null || blockEntity == null || !blockEntity.isConnecting() || !getSelfUuid(blockEntity).equals(this.getUuid()) || !state.isOf(BlockRegistries.ELECTROMAGNETIC_SHUTTLE_BASE_BLOCK);
        if (invalid) {
            this.discard();
            return true;
        }
        return false;
    }

    private UUID getSelfUuid(ElectromagneticShuttleBaseEntity blockEntity) {
        return this.isSource ? blockEntity.getSourceEntity() : blockEntity.getConnectedEntity();
    }

    public void setChunkForced(boolean forced) {
        if (this.world instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().setChunkForced(new ChunkPos(this.getBlockPos()), forced);
        }
    }

    @Nullable
    private ElectromagneticShuttleBaseEntity getSourceBlockEntity() {
        BlockEntity blockEntity = world.getBlockEntity(this.getSourcePos());
        if (blockEntity instanceof ElectromagneticShuttleBaseEntity entity) {
            return entity;
        }
        return null;
    }

    @Nullable
    public ShuttleEntranceEntity getConnectedEntity() {
        if (this.world instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().setChunkForced(new ChunkPos(BlockPos.ofFloored(this.connectedPos)), true);
            if (serverWorld.getEntity(this.connectedEntity) instanceof ShuttleEntranceEntity entity) {
                return entity;
            }
            List<? extends ShuttleEntranceEntity> list = serverWorld.getEntitiesByType(TypeFilter.instanceOf(ShuttleEntranceEntity.class), entrance -> entrance.getUuid().equals(this.connectedEntity));
            if (!list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        ElectromagneticShuttleBaseEntity entity = this.getSourceBlockEntity();
        if (entity != null) {
            entity.setConnecting(false);
        }
        this.world.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.VOICE);
        setChunkForced(false);
    }

    public BlockPos getSourcePos() {
        return this.dataTracker.get(SOURCE_POS_DATA);
    }

    public void setSourcePos(BlockPos pos) {
        this.dataTracker.set(SOURCE_POS_DATA, pos);
    }

    public float getUniqueOffset() {
        return this.uniqueOffset;
    }

    public void setUniqueOffset(float uniqueOffset) {
        this.uniqueOffset = uniqueOffset;
    }

    private void onCollide(Entity entity) {
        ElectromagneticShuttleBaseEntity blockEntity = this.getSourceBlockEntity();
        if (blockEntity == null) {
            return;
        }
        if (!blockEntity.isConnecting()) {
            return;
        }
        ArrayList<Vec3d> route = new ArrayList<>(blockEntity.getRoute());
        if (!this.isSource) {
            Collections.reverse(route);
        }
        entity.shuttle(route);
    }

    public void setConnectedEntity(ShuttleEntranceEntity entity) {
        this.connectedEntity = entity.getUuid();
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(SOURCE_POS_DATA, BlockPos.ORIGIN);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains(CONNECTED_ENTITY)) {
            this.connectedEntity = nbt.getUuid(CONNECTED_ENTITY);
        }
        if (nbt.contains(SOURCE)) {
            this.isSource = nbt.getBoolean(SOURCE);
        }
        if (nbt.contains(SOURCE_POS) && nbt.getIntArray(SOURCE_POS).length == 3) {
            int[] pos = nbt.getIntArray(SOURCE_POS);
            this.setSourcePos(new BlockPos(pos[0], pos[1], pos[2]));
        }
        if (nbt.contains(CONNECTED_POS)) {
            NbtCompound pos = nbt.getCompound(CONNECTED_POS);
            this.connectedPos = new Vec3d(pos.getDouble(X), pos.getDouble(Y), pos.getDouble(Z));
        }
        if (nbt.contains(UNIQUE_OFFSET)) {
            this.setUniqueOffset(nbt.getFloat(UNIQUE_OFFSET));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putUuid(CONNECTED_ENTITY, this.connectedEntity);
        nbt.putBoolean(SOURCE, this.isSource);
        nbt.putIntArray(SOURCE_POS, new int[]{this.getSourcePos().getX(), this.getSourcePos().getY(), this.getSourcePos().getZ()});
        NbtCompound pos = new NbtCompound();
        pos.putDouble(X, this.connectedPos.x);
        pos.putDouble(Y, this.connectedPos.y);
        pos.putDouble(Z, this.connectedPos.z);
        nbt.put(CONNECTED_POS, pos);
        nbt.putFloat(UNIQUE_OFFSET, this.uniqueOffset);
    }

    @Override
    public boolean shouldRender(double distance) {
        return !this.isInvisible();
    }

    private boolean shouldBeInvisible() {
        BlockState state = getSourceBlockState();
        if (state.isOf(BlockRegistries.ELECTROMAGNETIC_SHUTTLE_BASE_BLOCK)) {
            return !state.get(ElectromagneticShuttleBaseBlock.POWERED);
        }
        return true;
    }

    private BlockState getSourceBlockState() {
        return this.world.getBlockState(this.getSourcePos());
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

}
