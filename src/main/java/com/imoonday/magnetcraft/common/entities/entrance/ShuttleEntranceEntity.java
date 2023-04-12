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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShuttleEntranceEntity extends Entity {

    public static final String CONNECTED_ENTITY = "ConnectedEntity";
    public static final String SOURCE = "Source";
    public static final String SOURCE_POS = "SourcePos";
    protected UUID connectedEntity;
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

    public ShuttleEntranceEntity(World world, boolean isSource, BlockPos sourcePos, float uniqueOffset) {
        this(EntityRegistries.SHUTTLE_ENTRANCE, world);
        this.isSource = isSource;
        this.uniqueOffset = uniqueOffset;
        this.setSourcePos(sourcePos);
    }

    @Override
    public void tick() {
        if (world.isClient) {
            return;
        }
        ShuttleEntranceEntity connectedEntity = getConnectedEntity();
        if (connectedEntity == null) {
            this.discard();
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(this.getSourcePos());
        if (!(blockEntity instanceof ElectromagneticShuttleBaseEntity base) || !base.isConnecting()) {
            this.discard();
            return;
        }
        if (!this.isSourcePowered()) {
            return;
        }
        BlockState state = this.world.getBlockState(this.getSourcePos());
        if (state.isOf(BlockRegistries.ELECTROMAGNETIC_SHUTTLE_BASE_BLOCK)) {
            this.setInvisible(!this.isSourcePowered());
        } else {
            connectedEntity.discard();
            this.discard();
            return;
        }
        PlayerEntity player = Optional.ofNullable(this.world.getClosestPlayer(this.isSource ? this : connectedEntity, 5)).orElse(this.world.getClosestPlayer(isSource ? connectedEntity : this, 5));
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
            float connectedUniqueOffset = connectedEntity.getUniqueOffset();
            if (this.uniqueOffset == 0 || this.uniqueOffset != connectedUniqueOffset) {
                if (connectedUniqueOffset != 0) {
                    this.uniqueOffset = connectedUniqueOffset;
                } else {
                    float newUniqueOffset = world.random.nextFloat() + 2.0f;
                    this.uniqueOffset = newUniqueOffset;
                    connectedEntity.setUniqueOffset(newUniqueOffset);
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
            if (entity instanceof PlayerEntity playerEntity) {
                if (playerEntity.isSneaking()) {
                    continue;
                }
            }
            onCollide(entity);
        }
    }

    @Nullable
    public ShuttleEntranceEntity getConnectedEntity() {
        if (world instanceof ServerWorld serverWorld) {
            Entity entity = serverWorld.getEntity(this.connectedEntity);
            return entity instanceof ShuttleEntranceEntity entrance ? entrance : null;
        }
        return null;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        this.world.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.VOICE);
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
        if (this.connectedEntity == null) {
            return;
        }
        BlockEntity blockEntity = this.world.getBlockEntity(getSourcePos());
        if (blockEntity == null) {
            return;
        }
        if (blockEntity instanceof ElectromagneticShuttleBaseEntity base) {
            if (!base.isConnecting()) {
                return;
            }
            ArrayList<Vec3d> route = new ArrayList<>(base.getRoute());
            if (!this.isSource) {
                Collections.reverse(route);
            }
            entity.shuttle(route);
        }
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
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putUuid(CONNECTED_ENTITY, this.connectedEntity);
        nbt.putBoolean(SOURCE, this.isSource);
        nbt.putIntArray(SOURCE_POS, new int[]{this.getSourcePos().getX(), this.getSourcePos().getY(), this.getSourcePos().getZ()});
    }

    @Override
    public boolean shouldRender(double distance) {
        return isSourcePowered();
    }

    private boolean isSourcePowered() {
        BlockState state = this.world.getBlockState(this.getSourcePos());
        if (state.isOf(BlockRegistries.ELECTROMAGNETIC_SHUTTLE_BASE_BLOCK)) {
            return state.get(ElectromagneticShuttleBaseBlock.POWERED);
        }
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

}
