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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ShuttleEntranceEntity extends Entity {

    protected UUID connectedEntity;
    protected boolean isSource;
    protected BlockPos sourcePos;

    public ShuttleEntranceEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public ShuttleEntranceEntity(World world, boolean isSource, BlockPos sourcePos) {
        super(EntityRegistries.SHUTTLE_ENTRANCE, world);
        this.isSource = isSource;
        this.sourcePos = sourcePos;
        this.noClip = true;
        this.setNoGravity(true);
        this.setInvulnerable(true);
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
            Vec3d pos = player.getPos().multiply(1, 0, 1).add(0, this.getEyeY(), 0);
            this.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, pos);
        } else {
            this.setYaw(this.getYaw() + 1 / 360f);
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
            ArrayList<Vec3d> route = base.getRoute();
            if (!this.isSource) {
                Collections.reverse(route);
            }
            entity.shuttle(route);
        }
    }

    public UUID getConnectedEntity() {
        return this.connectedEntity;
    }

    public void setConnectedEntity(ShuttleEntranceEntity entity) {
        this.connectedEntity = entity.getUuid();
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }

    @Override
    public boolean shouldRender(double distance) {
        return distance <= 15;
    }

}
