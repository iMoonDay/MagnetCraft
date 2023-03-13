package com.imoonday.magnetcraft.mixin;

import com.imoonday.magnetcraft.api.EntityAttractNbt;
import com.imoonday.magnetcraft.common.items.magnets.CreatureMagnetItem;
import com.imoonday.magnetcraft.methods.AttractMethods;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Entity.class)
public class EntityMixin implements EntityAttractNbt {

    protected NbtCompound attractData = new NbtCompound();
    protected double attractDis = 0;
    protected boolean isAttracting = false;
    protected boolean enable = true;
    protected UUID attractOwner = CreatureMagnetItem.EMPTY_UUID;

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        this.attractDis = this.getAttractDis();
        this.isAttracting = this.isAttracting();
        this.enable = this.getEnable();
        this.attractOwner = this.getAttractOwner();
        if (entity.isAttracting() && entity.getEnable() && entity.isAlive()) {
            AttractMethods.attracting(entity, entity.getAttractDis());
        }
    }

    @Override
    public NbtCompound getAttractData() {
        if (attractData == null) {
            attractData = new NbtCompound();
        }
        return this.attractData;
    }

    @Override
    public boolean clearAttractData() {
        this.attractData = new NbtCompound();
        return true;
    }

    @Override
    public double getAttractDis() {
        if (!this.attractData.contains("AttractDis")) {
            this.attractData.putDouble("AttractDis", 0);
        }
        return this.attractData.getDouble("AttractDis");
    }

    @Override
    public void setAttractDis(double dis) {
        this.attractData.putDouble("AttractDis", dis);
    }

    @Override
    public boolean isAttracting() {
        if (!this.attractData.contains("isAttracting")) {
            this.attractData.putBoolean("isAttracting", false);
        }
        return this.attractData.getBoolean("isAttracting");
    }

    @Override
    public void setAttracting(boolean attracting) {
        this.attractData.putBoolean("isAttracting", attracting);
        if (!attracting) {
            this.attractData.putDouble("AttractDis", 0);
        }
    }

    @Override
    public void setAttracting(boolean attracting, double dis) {
        setAttracting(attracting);
        setAttractDis(dis);
    }

    @Override
    public boolean getEnable() {
        if (!this.attractData.contains("Enable")) {
            this.attractData.putBoolean("Enable", true);
        }
        return this.attractData.getBoolean("Enable");
    }

    @Override
    public void setEnable(boolean enable) {
        this.attractData.putBoolean("Enable", enable);
    }

    @Override
    public UUID getAttractOwner() {
        if (!this.attractData.contains("AttractOwner")) {
            this.attractData.putUuid("AttractOwner", CreatureMagnetItem.EMPTY_UUID);
        }
        return this.attractData.getUuid("AttractOwner");
    }

    @Override
    public void setAttractOwner(UUID uuid) {
        this.attractData.putUuid("AttractOwner", uuid);
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    public void writePocketsDataToNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        this.attractData.putDouble("AttractDis", this.getAttractDis());
        this.attractData.putBoolean("isAttracting", this.isAttracting());
        this.attractData.putBoolean("Enable", this.getEnable());
        this.attractData.putUuid("AttractOwner", this.getAttractOwner());
        nbt.put("AttractData", this.attractData);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    public void readPocketsDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("AttractData")) {
            this.attractData = nbt.getCompound("AttractData");
            if (nbt.getCompound("AttractData").contains("AttractDis")) {
                this.attractDis = nbt.getCompound("AttractData").getDouble("AttractDis");
            }
            if (nbt.getCompound("AttractData").contains("isAttracting")) {
                this.isAttracting = nbt.getCompound("AttractData").getBoolean("isAttracting");
            }
            if (nbt.getCompound("AttractData").contains("Enable")) {
                this.enable = nbt.getCompound("AttractData").getBoolean("Enable");
            }
            if (nbt.getCompound("AttractData").contains("AttractOwner")) {
                this.attractOwner = nbt.getCompound("AttractData").getUuid("AttractOwner");
            }
        }
    }

}
