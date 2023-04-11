package com.imoonday.magnetcraft.common.entities.entrance;

import com.imoonday.magnetcraft.common.blocks.ElectromagneticShuttleBaseBlock;
import com.imoonday.magnetcraft.common.blocks.entities.ElectromagneticShuttleBaseEntity;
import com.imoonday.magnetcraft.registries.common.BlockRegistries;
import com.imoonday.magnetcraft.registries.common.EntityRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ShuttleEntranceEntity extends Entity {

    public static final String CONNECTED_ENTITY = "ConnectedEntity";
    public static final String SOURCE = "Source";
    public static final String SOURCE_POS = "SourcePos";
    protected UUID connectedEntity;
    protected boolean isSource;
    protected BlockPos sourcePos;

    public ShuttleEntranceEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true;
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.setPitch(0);
    }

    public ShuttleEntranceEntity(World world, boolean isSource, BlockPos sourcePos) {
        this(EntityRegistries.SHUTTLE_ENTRANCE, world);
        this.isSource = isSource;
        this.sourcePos = sourcePos;
    }

    @Override
    public void tick() {
        if (this.world instanceof ServerWorld serverWorld) {
            Entity entity = serverWorld.getEntity(this.connectedEntity);
            if (entity == null) {
                this.discard();
                return;
            }
        }
        if (this.sourcePos == null) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(this.sourcePos);
        if (!(blockEntity instanceof ElectromagneticShuttleBaseEntity base)) {
            return;
        }
        if (!base.isConnecting()) {
            this.discard();
            return;
        }
        BlockState state = this.world.getBlockState(this.sourcePos);
        if (state.isOf(BlockRegistries.ELECTROMAGNETIC_SHUTTLE_BASE_BLOCK)) {
            Boolean powered = state.get(ElectromagneticShuttleBaseBlock.POWERED);
            this.setInvisible(!powered);
            if (!powered) {
                return;
            }
        } else {
            if (this.world instanceof ServerWorld serverWorld) {
                Entity entity = serverWorld.getEntity(this.connectedEntity);
                if (entity != null) {
                    entity.discard();
                }
                this.discard();
            }
        }
        PlayerEntity player = this.world.getClosestPlayer(this, 15);
        if (player != null) {
            Vec3d pos = EntityAnchorArgumentType.EntityAnchor.EYES.positionAt(this);
            double offsetX = player.getX() - pos.x;
            double offsetZ = player.getZ() - pos.z;
            double rotation = MathHelper.atan2(offsetZ, offsetX);
            while (rotation >= (float) Math.PI) {
                rotation -= (float) Math.PI * 2;
            }
            while (rotation < (float) (-Math.PI)) {
                rotation += (float) Math.PI * 2;
            }
            this.setYaw(MathHelper.lerp(1, this.getYaw(), MathHelper.wrapDegrees((float) (rotation * 57.2957763671875) - 90.0f)));
        } else {
            float uniqueOffset = -2.8647919f;
            float nextYaw = this.getYaw() + uniqueOffset;
            while (nextYaw > 180) {
                nextYaw -=360;
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
            onCollide(entity);
        }
    }

    private void onCollide(Entity entity) {
        if (this.connectedEntity == null) {
            return;
        }
        BlockEntity blockEntity = this.world.getBlockEntity(sourcePos);
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
            this.sourcePos = new BlockPos(pos[0], pos[1], pos[2]);
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putUuid(CONNECTED_ENTITY, this.connectedEntity);
        nbt.putBoolean(SOURCE, this.isSource);
        nbt.putIntArray(SOURCE_POS, new int[]{this.sourcePos.getX(), this.sourcePos.getY(), this.sourcePos.getZ()});
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

}
